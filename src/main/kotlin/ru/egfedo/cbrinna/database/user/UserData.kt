package ru.egfedo.cbrinna.database.user

/**
 * Actual user data which is stored in json format.
 * @see UserDataStorage
 */
data class UserData(
    val id: Long,
    var correctAnswers: Int = 0,
    var totalAnswers: Int = 0,
    val allowedDBs: MutableSet<String> = mutableSetOf("civilLaw", "constLaw", "kt1"),
    val wrongAnswerList: MutableMap<String, MutableSet<String>> = mutableMapOf(
        Pair("civilLaw", mutableSetOf()),
        Pair("constLaw", mutableSetOf()),
    ),

    val encounteredQuestions: MutableMap<String, MutableSet<String>> = mutableMapOf(
        Pair("civilLaw", mutableSetOf()),
        Pair("constLaw", mutableSetOf()),),
    var lastMessage: Long = -1
) {
    @Transient
    val composedDBs: MutableMap<String, MutableSet<String>> = mutableMapOf(
        Pair("kt1", mutableSetOf("civilLaw", "constLaw"))
    )
    fun copy() : UserData {
        return UserData(id, correctAnswers, totalAnswers,
            allowedDBs.toMutableSet(),
            wrongAnswerList.toMutableMap(),
            encounteredQuestions.toMutableMap(), lastMessage)
    }

    fun getEncounteredAmount(key: String, ignoreComposed: Boolean = false) : Int {
        return getRawAmount(encounteredQuestions, key, ignoreComposed)
    }

    fun getWrongAmount(key: String, ignoreComposed: Boolean = false) : Int {
        return getRawAmount(wrongAnswerList, key, ignoreComposed)
    }

    private fun getRawAmount(map : MutableMap<String, MutableSet<String>>, key: String, ignoreComposed: Boolean) : Int {
        if (key in composedDBs) {
            if (ignoreComposed)
                return 0
            var size = 0
            composedDBs[key]?.forEach {
                size += map[it]?.size ?: 0
            }
            return size
        }
        val set = map[key] ?: return -1
        return set.size
    }

}
