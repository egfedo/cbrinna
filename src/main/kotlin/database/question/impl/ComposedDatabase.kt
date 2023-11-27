package database.question.dbimpl

import telegram.data.FrontendQuestion
import database.question.DatabaseInterface
import database.user.UserData

/**
 * This DB implementation has a list of nested DBs and just delegates
 * all work to them.
 */
class ComposedDatabase(contents: List<Database>) : DatabaseInterface {
    private val contents = contents
    override fun addEntries(filename: String): Int {
        return -1;
    }

    override fun getRandomEntries(amount: Int): MutableList<FrontendQuestion> {
        val entries: MutableList<FrontendQuestion> = mutableListOf()
        for (db in contents)
            entries.addAll(db.getRandomEntries(amount))
        entries.shuffle()
        return if (entries.size < amount) entries
            else entries.slice(0..<amount).toMutableList()
    }

    override fun getRandomEntries(amount: Int, userData: UserData, getMode: DatabaseInterface.GetMode): List<FrontendQuestion> {
        val entries: MutableList<FrontendQuestion> = mutableListOf()
        contents.forEach { entries.addAll(it.getRandomEntries(1000, userData, getMode)) }
        entries.shuffle()
        return if (entries.size < amount) entries
            else entries.slice(0..<amount).toMutableList()
    }

    override fun size(): Int {
        var size = 0
        for (db in contents)
            size += db.size()
        return size
    }

    override fun size(userData: UserData, getMode: DatabaseInterface.GetMode): Int {
        return contents.sumOf { it.size(userData, getMode) }
    }

}