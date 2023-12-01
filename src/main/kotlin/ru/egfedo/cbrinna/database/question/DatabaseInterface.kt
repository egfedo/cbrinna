package ru.egfedo.cbrinna.database.question

import ru.egfedo.cbrinna.database.user.UserData
import ru.egfedo.cbrinna.telegram.data.FrontendQuestion

/**
 * Database interface since a database can be concrete or composed of other DBs.
 */
interface DatabaseInterface {
    enum class GetMode {
        All, Excluding, Only
    }

    fun addEntries(filename: String): Int
    fun getEntries(): Sequence<FrontendQuestion>
    fun size(): Int
    fun size(userData: UserData, getMode: GetMode = GetMode.Excluding): Int
    fun getEntries(userData: UserData, getMode: GetMode = GetMode.Excluding): Sequence<FrontendQuestion>
}
