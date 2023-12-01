package ru.egfedo.cbrinna.telegram.data

/**
 * User temporary data for currently running test.
 * @see telegram.TestsHandler
 */
data class UserTestTempData(
    val entries: List<FrontendQuestion>,
    var questionID: Int = 0,
    var correctCount: Int = 0,
    var inputBuffer: String = "",
    var wrongAnswers : List<String> = mutableListOf()
)
