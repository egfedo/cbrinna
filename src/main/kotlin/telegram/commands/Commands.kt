package telegram.commands

import database.question.DatabaseList
import database.user.UserDataStorage
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.InputHandler
import eu.vendeli.tgbot.annotations.ParamMapping
import eu.vendeli.tgbot.annotations.UnprocessedHandler
import eu.vendeli.tgbot.api.*
import eu.vendeli.tgbot.types.ParseMode
import eu.vendeli.tgbot.types.Text
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.chat.ChatType
import eu.vendeli.tgbot.types.inline.InlineQueryResult
import eu.vendeli.tgbot.types.internal.*
import telegram.TestsHandler
import kotlin.Pair

val categoryPlaceholders = mapOf(
    Pair("civilLaw", "гражданскому праву"),
    Pair("constLaw", "конституционному праву"),
    Pair("civilLawAll", "гражданскому праву"),
    Pair("constLawAll", "конституционному праву"),
    Pair("kt1", "темам к КТ 1 (КП и ГП)"),
    Pair("civilLawNew", "гражданскому праву (новые вопросы)"),
    Pair("constLawNew", "конституционному праву (новые вопросы)"),
)

class Commands {
    @CommandHandler(["/start"])
    suspend fun start(user: User, bot: TelegramBot, update: MessageUpdate) {
        if (update.message.chat.type == ChatType.Private) {
            message {
                "Ну здравствуй.\nЯ беру ваши файлы с тестов по праву и собираю из них базы для тренировки.\n" +
                        "Жду и от тебя файлов с тестами! Подробнее о формате файлов в /add\n" +
                        "Если хочешь потренироваться то не тормози, в /tests заходи!\n" +
                        "Свою статистику можно увидеть в /stats"
            }.send(user, bot)

        }
    }

    @CommandHandler(["/tests"])
    suspend fun tests(user: User, bot: TelegramBot, update: MessageUpdate) {
        if (update.message.chat.type == ChatType.Private) {
            TestsHandler.testsCommand(user, bot)
        }
    }

    private suspend fun deleteButtonMsg(user: User, bot: TelegramBot, update: CallbackQueryUpdate) {
        val msg = update.callbackQuery.message
        if (msg != null) {
            deleteMessage(msg.messageId).send(user, bot)
        }
    }

    @CommandHandler(["openCategory"], scope = [CommandScope.CALLBACK])
    suspend fun civilLaw(
        user: User,
        bot: TelegramBot,
        update: CallbackQueryUpdate,
        @ParamMapping("name") name: String
    ) {
        deleteButtonMsg(user, bot, update)
        TestsHandler.chooseFilter(user, bot, name)
    }

    @CommandHandler(["chooseAmount"], scope = [CommandScope.CALLBACK])
    suspend fun chooseAmountHandler(
        user: User,
        bot: TelegramBot,
        @ParamMapping("filter") filter: String,
        @ParamMapping("name") name: String,
        update: CallbackQueryUpdate
    ) {
        deleteButtonMsg(user, bot, update)
        TestsHandler.chooseAmount(user, bot, name, filter)
    }

    @CommandHandler(["startTest"], scope = [CommandScope.CALLBACK])
    suspend fun startTestHandler(
        user: User,
        bot: TelegramBot,
        update: CallbackQueryUpdate,
        @ParamMapping("amount") amount: Int,
        @ParamMapping("name") name: String,
        @ParamMapping("filter") filter: String
    ) {
        deleteButtonMsg(user, bot, update)
        TestsHandler.startTest(bot, user, name, amount, filter)
    }

    @InputHandler(["testRunning"])
    suspend fun testRunning(user: User, bot: TelegramBot, update: MessageUpdate) {
        if (update.message.chat.type == ChatType.Private) {
            TestsHandler.testRoutine(bot, user, update)
        }
    }

    @CommandHandler(["/stats"])
    suspend fun statsCmd(user: User, bot: TelegramBot) {
        val userData = UserDataStorage[user.id]

        var encounteredAmt = 0
        val encounteredList: MutableList<String> = mutableListOf()
        userData.encounteredQuestions.keys.forEach {
            encounteredAmt += userData.getEncounteredAmount(it, true)
            encounteredList.add("      » ${userData.getEncounteredAmount(it)} из ${DatabaseList.contents[it]?.size()} в базе по ${categoryPlaceholders[it]}")
        }

        val wrongList: MutableList<String> = mutableListOf()
        var wrongAmt = 0
        userData.wrongAnswerList.keys.forEach {
            wrongList.add("      » ${userData.getWrongAmount(it)} в базе по ${categoryPlaceholders[it]}")
            wrongAmt += userData.getWrongAmount(it, true)
        }


        message {
            "" - bold { "Твоя Статистика\n" } - "\n" -
                    bold { "- ${userData.correctAnswers} из ${userData.totalAnswers}" } - " правильных ответов\n\n" -
                    bold { "- $encounteredAmt" } - " вопросов ты видел из моей базы:\n" -
                    encounteredList.joinToString("\n", postfix = "\n") -
                    bold { "- $wrongAmt" } - " вопросов, на которые ты неправильно ответил:\n" -
                    wrongList.joinToString("\n")
        }.send(user, bot)

    }

    @CommandHandler(["/add"])
    suspend fun addCmd(user: User, bot: TelegramBot, update: MessageUpdate) {
        if (update.message.document != null) {
            message { "Новый файл!" }.send(1820143237, bot)
            forwardMessage(1820143237, user.id, update.message.messageId)
            message { "Файл успешно отправлен на модерацию!" }.send(user, bot)
        } else {
            message {
                "Для того чтобы получить доступ к некоторым " +
                        "базам, нужно сначала отправить свой результат теста по этой теме. " +
                        "Тест надо скинуть в .txt в том же виде, в котором просил Лёша.\n" +
                        "Сначала файл проходит модерацию, а затем Вы получаете доступ.\n" +
                        "Надо просто отправить файл мне."
            }.send(user, bot)
        }
    }

    @UnprocessedHandler
    suspend fun unprocessed(user: User, bot: TelegramBot, update: ProcessedUpdate) {
        if (update is InlineQueryUpdate) {
            val userData = UserDataStorage[user.id]

            var encounteredAmt = 0
            val encounteredList: MutableList<String> = mutableListOf()
            userData.encounteredQuestions.keys.forEach {
                encounteredAmt += userData.getEncounteredAmount(it, true)
                encounteredList.add("      \\* ${userData.getEncounteredAmount(it)} из ${DatabaseList.contents[it]?.size()} в базе по ${categoryPlaceholders[it]}")
            }

            val wrongList: MutableList<String> = mutableListOf()
            var wrongAmt = 0
            userData.wrongAnswerList.keys.forEach {
                wrongList.add("      \\* ${userData.getWrongAmount(it)} в базе по ${categoryPlaceholders[it]}")
                wrongAmt += userData.getWrongAmount(it, true)
            }

            val message = "*Статистика пользователя ${user.firstName}*:\n" +
                    "*\\- ${userData.correctAnswers} из ${userData.totalAnswers}* правильных ответов\n" +
                    "*\\- $encounteredAmt* пройденных вопросов из базы\n"
            /*encounteredList.joinToString("\n", postfix = "\n") +
                "*\\- $wrongAmt* непр:" +
                wrongList.joinToString("\n", postfix = "\n")*/

            val content = Text(message, parseMode = ParseMode.MarkdownV2)
            val result = InlineQueryResult.Article("dad", "Похвастаться своей статой", content,)
            answerInlineQuery(update.inlineQuery.id, result).options { cacheTime = 5 }.send(bot)
        }
        if (update is MessageUpdate) {
            if (update.message.document != null) {
                val userData = UserDataStorage[user.id]
                if ("fileSend" in userData!!.allowedDBs) {
                    message { "Я заблокировала твоё отправление файлов из-за спама!" }.send(user, bot)
                    return
                }
                message { "Новый файл!" }.send(-4092309160, bot)
                getFile(update.message.document!!.fileId)
                forwardMessage(-4092309160, user.id, update.message.messageId).send(-4092309160, bot)
                message { "Файл успешно отправлен на модерацию!" }.send(user, bot)
            }
            if (update.message.text!!.startsWith("/dbadd ")) {
                println(update.message.replyToMessage?.forwardFrom?.id)
                val id = update.message.replyToMessage?.forwardFrom?.id
                val splitted = update.message.text!!.split(" ")
                if (id != null) {
                    val userData = UserDataStorage[id]
                    userData.allowedDBs.add(splitted[1])
                    UserDataStorage[id] = userData
                }
            }
        }
    }

    @CommandHandler(["/id"])
    suspend fun idCmd(bot: TelegramBot, update: MessageUpdate) {
        if (update.message.chat.type == ChatType.Supergroup || update.message.chat.type == ChatType.Group) {
            message { "${update.message.chat.id}" }.send(update.message.chat.id, bot)
            forwardMessage(-4092309160, -4092309160, update.message.messageId)

        }
    }
}

//@CommandHandler(["/dbadd"])
//suspend fun dbaddCmd(bot: TelegramBot, update: MessageUpdate) {
//    if (update.message.chat.id == -4092309160) {
//        println(update.message.text)
//    }
//
//    answerInlineQuery("3848")
//}
