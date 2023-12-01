package telegram

import database.question.DatabaseInterface
import telegram.data.UserTestTempData
import database.question.DatabaseList
import database.user.UserData
import database.user.UserDataStorage
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.deleteMessage
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.interfaces.Keyboard
import eu.vendeli.tgbot.types.*
import eu.vendeli.tgbot.types.inline.InlineQueryResult
import eu.vendeli.tgbot.types.inline.InlineQueryResultsButton
import eu.vendeli.tgbot.types.internal.CallbackQueryUpdate
import eu.vendeli.tgbot.types.internal.MessageUpdate
import eu.vendeli.tgbot.types.internal.Response
import eu.vendeli.tgbot.types.internal.getOrNull
import eu.vendeli.tgbot.types.keyboard.ReplyKeyboardRemove
import eu.vendeli.tgbot.utils.builders.inlineKeyboardMarkup
import eu.vendeli.tgbot.utils.builders.replyKeyboardMarkup
import telegram.data.FrontendQuestion
import telegram.data.FrontendTest
import kotlin.math.round

object TestsHandler {
    private val singleKeyboard = replyKeyboardMarkup {
        + "1"
        + "2"
        + "3"
        + "4"
    }
    private val multipleKeyboard = replyKeyboardMarkup {
        + "1"
        + "2"
        + "3"
        + "4"
        newLine()
        + "Готово"
    }

    private val testList: MutableMap<String, FrontendTest> = mutableMapOf(
        Pair("constLaw", FrontendTest("constLaw", "Конституционное право", DatabaseList.contents["constLaw"]!!, "Конституционному праву") ),
        Pair("civilLaw", FrontendTest("civilLaw", "Гражданское право", DatabaseList.contents["civilLaw"]!!, "Гражданскому праву")),
        Pair("kt1", FrontendTest("kt1", "КТ 1", DatabaseList.contents["kt1"]!!, "Контрольной точке 1"))
    )
    private val userData: MutableMap<Long, UserTestTempData> = mutableMapOf()

    /**Sends a message to the user containing buttons
     * for different test categories.
     * @param user target user
     * @param bot active bot instance
        */
    suspend fun testsCommand(user: User, bot: TelegramBot) {
        deletePrevMessage(user, bot)
        val key = inlineKeyboardMarkup {
            var i = 0
            for (key in testList.keys) {
                val test = testList[key]!!
                test.displayName callback "openCategory?name=${test.id}"
                i++
                if (i % 2 == 0) {
                    br()
                }
            }
        }
        var displayList = ""
        for (test in testList.keys) {
            displayList += "\n- ${testList[test]!!.displayNameVariation}"
        }
        val response = message {
                bold { "Тебе чего, тесты нужны?" } - "\nЩас у меня есть тесты по:\n" -
                        displayList -
                        "\n\nВыбирай, что по душе"
            }.markup(key).sendAsync(user, bot).await()
        println(response.getOrNull()?.messageId)
        bufferPrevMessage(user, response)

    }

    /**Checks user stats and db and displays a menu
     * to choose different filters for the test.
     * @param user target user
     * @param bot active bot instance
     * @param categoryName category name (same as db id)
     */
    suspend fun chooseFilter(user: User, bot: TelegramBot, categoryName: String) {
        deletePrevMessage(user, bot)
        val userData = UserDataStorage[user.id]

        if (categoryName !in userData.allowedDBs) {
            message { "Кажись у тебя нет прав (хе-хе) на пользование этой базой. Подробнее в /add" }.send(user, bot)
            return
        }

        if (userData.getEncounteredAmount(categoryName) > 0 && userData.getEncounteredAmount(categoryName) != DatabaseList.contents[categoryName]?.size()) {
            val keyboard = inlineKeyboardMarkup {
                "Все" callback "chooseAmount?name=$categoryName&filter=All"
                "Новые" callback "chooseAmount?name=$categoryName&filter=New"
                br()
                if (userData.getWrongAmount(categoryName) != 0) {
                    "Неправильные ответы" callback "chooseAmount?name=$categoryName&filter=Wrong"
                }
            }
            val response = message {
                "О, я вижу, что ты у меня в базе по ${testList[categoryName]?.displayNameVariation} видел " - bold { "${userData.getEncounteredAmount(
                    categoryName
                )} вопросов из ${DatabaseList.contents[categoryName]?.size()}" } - "." -
                        "\nБерём все или только новые?"
            }.markup(keyboard).sendAsync(user, bot).await()
            bufferPrevMessage(user, response)

        }
        else {
            chooseAmount(user, bot, categoryName, filter = "All")
        }

    }

    /**Displays different question amount
     * options to the user
     * @param user target user
     * @param bot active bot instance
     * @param name category name (same as db id)
     * @param filter filter type (All, New or Wrong)
     */
    suspend fun chooseAmount(user: User, bot: TelegramBot, name: String, filter: String) {
        deletePrevMessage(user, bot)
        val userData = UserDataStorage[user.id]
        val key = inlineKeyboardMarkup {
            "5" callback "startTest?amount=5&name=${name}&filter=${filter}"
            "10" callback "startTest?amount=10&name=${name}&filter=${filter}"
            "20" callback "startTest?amount=20&name=${name}&filter=${filter}"
            newLine() // or br()
            "30" callback "startTest?amount=30&name=${name}&filter=${filter}"
            "50" callback "startTest?amount=50&name=${name}&filter=${filter}"
            "Все" callback "startTest?amount=1000&name=${name}&filter=${filter}"
        }
        val getMode = when (filter) {
            "All" -> DatabaseInterface.GetMode.All
            "New" -> DatabaseInterface.GetMode.Excluding
            "Wrong" -> DatabaseInterface.GetMode.Only
            else -> DatabaseInterface.GetMode.All
        }

        val response = message {
            """
            Так\-с, у меня в базе по ${testList[name]?.displayNameVariation} *${DatabaseList.contents[name]?.size(userData, getMode)} вопросов*\.
            Сколько берёшь?
            """.trimIndent()
        }.markup(key).options { parseMode = ParseMode.MarkdownV2 }.sendAsync(user, bot).await()
        bufferPrevMessage(user, response)
    }


    /**Displays current test question.
     * @param user target user
     * @param bot active bot instance
     */
    private suspend fun displayCurrentQuestion(bot: TelegramBot, user: User) {
        val userTempData = this.userData[user.id] ?: return
        val keyboard: Keyboard = if (userTempData.entries[userTempData.questionID].type == FrontendQuestion.Type.OneAnswer) singleKeyboard
        else multipleKeyboard
        message { userTempData.entries[userTempData.questionID].toString() + "Отправь мне сообщения с номерами ответов без проеблов"}.options { parseMode = ParseMode.MarkdownV2 }.markup(keyboard).send(user, bot)
    }

    /**Gets entries from db, initiates temp user data for this test,
     * and starts the test loop.
     * @param user target user
     * @param bot active bot instance
     * @param name category name (same as db id)
     * @param amount question amount
     * @param filter filter type (All, New or Wrong)
     */
    suspend fun startTest(bot: TelegramBot, user: User, name: String, amount: Int, filter: String) {
        deletePrevMessage(user, bot)
        var userTempData = this.userData[user.id]
        bot.inputListener[user] = "testRunning"
        if (userTempData != null) {
            if (userTempData.questionID != -1) {
                message { "Ты сейчас уже в тесте, куда ещё? Сначала заверши этот (/stop)" }.send(user, bot)
                return
            }
        }
        val userData = UserDataStorage[user.id]
        val entries = when (filter) {
            "All" -> DatabaseList.contents[name]!!.getRandomEntries(amount)
            "New" -> DatabaseList.contents[name]!!.getRandomEntries(amount, userData)
            "Wrong" -> DatabaseList.contents[name]!!.getRandomEntries(amount, userData, DatabaseInterface.GetMode.Only)
            else -> DatabaseList.contents[name]!!.getRandomEntries(amount)
        }

        this.userData[user.id] = UserTestTempData(entries)
        message { "*Поехали\\!*"}.options { parseMode = ParseMode.MarkdownV2 }.send(user, bot)
        displayCurrentQuestion(bot, user)
    }

    /**Gets the input and checks user answers
     * for being correct/incorrect. Inits the next question.
     * @param user target user
     * @param bot active bot instance
     * @param update tg api message update
     */
    suspend fun testRoutine(bot: TelegramBot, user: User, update: MessageUpdate) {
        if (update.message.text == "/stop") {
            val tempUserData = this.userData[user.id]!!
            if (tempUserData.questionID == 0)
                message {"Нихуя не сделал и ливнул, молодец!!!"}.markup(ReplyKeyboardRemove()).send(user, bot);
            else
                message {"Как скажешь. \nУ тебя ${tempUserData.correctCount} правильных ответов из ${tempUserData.questionID}. ${round(tempUserData.correctCount.toFloat()/tempUserData.questionID.toFloat()*100).toInt()}%"}.markup(
                    ReplyKeyboardRemove()
                ).send(user, bot)
            tempUserData.questionID = -1
            return
        }

        if (update.message.text?.toIntOrNull() == null && update.message.text != "Готово") {
            message {"И это студент, называется? Отправь число!"}.send(user, bot)
            bot.inputListener[user] = "testRunning"
            return
        }
        val msgText = update.message.text
        if (msgText == null) {
            bot.inputListener[user] = "testRunning"
            return
        }
        val tempUserData = this.userData[user.id]!!
        val currentQuestion = tempUserData.entries[tempUserData.questionID]
        if (currentQuestion.type == FrontendQuestion.Type.MultipleAnswers) {
            if (msgText != "Готово") {
                tempUserData.inputBuffer += msgText
                bot.inputListener[user] = "testRunning"
                return
            }
        }
        else
            tempUserData.inputBuffer = msgText

        var correct = true
        val answers = currentQuestion.answers

        for (char in tempUserData.inputBuffer) {
            if (char.digitToInt() > answers.size) {
                message {"Мракобесие! Такой цифры и нет даже! Ответь снова"}.send(user, bot)
                bot.inputListener[user] = "testRunning"
                return
            }
            if (!answers[char.digitToInt()-1].correct) {
                correct = false
                val correctAnswers : List<String> = answers.filter { it.correct }.map {it.text}
                message {"Ответ неправильный. На колени.\n Правильные ответы: \n ${correctAnswers.joinToString(prefix ="- ", separator="\n- ")}"}.send(user, bot)
                break
            }
        }
        val dbUserData = UserDataStorage[user.id]
        if (correct) {
            if (currentQuestion.originID !in dbUserData.wrongAnswerList[currentQuestion.originDB]!!)
                message { "И это правильный ответ!" }.send(user, bot)
            else {
                dbUserData.wrongAnswerList[currentQuestion.originDB]?.remove(currentQuestion.originID)
                message { "До этого ты ответил на этот вопрос неправильно!\nВ этот раз ты молодец, и я убрала этот вопрос из списка неправильных." }.send(user, bot)
            }
            dbUserData.correctAnswers++
            tempUserData.correctCount++
        }
        else dbUserData.wrongAnswerList[currentQuestion.originDB]?.add(currentQuestion.originID)

        dbUserData.encounteredQuestions[currentQuestion.originDB]?.add(currentQuestion.originID)
        dbUserData.totalAnswers++;
        UserDataStorage[user.id] = dbUserData
        tempUserData.inputBuffer = ""
        tempUserData.questionID++

        if (tempUserData.questionID >= tempUserData.entries.size) {
            message {"${tempUserData.correctCount} правильных ответов из ${tempUserData.entries.size}. ${round(tempUserData.correctCount.toFloat() / tempUserData.entries.size.toFloat() * 100).toInt()}%\n" +
                    "Тест окончен, свободен"}.markup(ReplyKeyboardRemove()).send(user, bot)
            tempUserData.questionID = -1
            println("Test end")
        }
        else {
            bot.inputListener[user] = "testRunning"
            displayCurrentQuestion(bot, user)
        }
    }
    private suspend fun deletePrevMessage(user: User, bot: TelegramBot) {
        val userData = UserDataStorage[user.id]
        println("last message: ${userData.lastMessage}")
        if (userData.lastMessage > 0) {
            deleteMessage(userData.lastMessage).send(user, bot)
            userData.lastMessage = -1
            UserDataStorage[user.id] = userData
        }
    }
    private fun bufferPrevMessage(user: User, response: Response<out Message>) {
        val sent = response.getOrNull()
        println(sent?.messageId)
        val id = sent!!.messageId
        val userData = UserDataStorage[user.id]
        userData.lastMessage = id
        UserDataStorage[user.id] = userData
    }
}