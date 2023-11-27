import java.util.*
import telegram.Bot

suspend fun main(args: Array<String>) {

//    val output: OutputStream = File("/Users/egfedo/IdeaProjects/CyberInna/db.json").outputStream()
//    output.bufferedWriter().use {it.write("")}
    Bot.start()


//    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).add(database.json.TreeSetAdapter()).build()
//    val adapter = moshi.adapter<SetWrapper>(SetWrapper::class.java)
//    val answers = sortedSetOf(
//        database.question.Answer("1", true),
//        database.question.Answer("2", false),
//        database.question.Answer("3", false),
//        database.question.Answer("4", false),
//        database.question.Answer("4", false),
//    )
//    print(answers)
//    val question = database.question.Question("what", answers)
//    val jsonQuestion = JsonQuestion(question)
//    val questions: MutableSet<database.question.Question> = mutableSetOf(question)
//    val wrapper = SetWrapper(questions)
//    val jsonString = adapter.toJson(wrapper)
//    println(jsonString)
//    val loadedQuestions = adapter.fromJson(jsonString)
//    if (loadedQuestions != null) {
//        println(loadedQuestions.set)
//    }
//    val blackjackHand = BlackjackHand(
//        Card('6', Suit.SPADES),
//        listOf(Card('4', Suit.CLUBS), Card('A', Suit.HEARTS))
//    )
//
//    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
//    val jsonAdapter: JsonAdapter<BlackjackHand> = moshi.adapter(BlackjackHand::class.java)
//
//    val json: String = jsonAdapter.toJson(blackjackHand)
//    println(json)
//    print(jsonAdapter.fromJson(json))

//    println(adapter.toJson(question))
//    println(question.hashCode())
//    println(adapter.fromJson(adapter.toJson(question)).hashCode())
//    val questions = FileParser.parse("/Users/egfedo/IdeaProjects/CyberInna/test2.txt").toMutableList()
//    print(questions[0].hashCode())
}