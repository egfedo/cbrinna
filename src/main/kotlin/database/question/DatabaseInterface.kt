package database.question

import database.user.UserData
import telegram.data.FrontendQuestion

/**
 * Database interface since a database can be concrete or composed of other DBs.
 */
interface DatabaseInterface {
    enum class GetMode {
        All, Excluding, Only
    }
    fun addEntries(filename: String) : Int
    fun getRandomEntries(amount: Int) : MutableList<FrontendQuestion>
    fun size() : Int
    fun size(userData: UserData, getMode: GetMode = GetMode.Excluding): Int
    fun getRandomEntries(amount: Int, userData: UserData, getMode: GetMode = GetMode.Excluding): List<FrontendQuestion>
}