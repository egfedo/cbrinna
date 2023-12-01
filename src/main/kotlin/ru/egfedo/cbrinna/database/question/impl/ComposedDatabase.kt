package database.question.dbimpl

import ru.egfedo.cbrinna.database.question.DatabaseInterface
import ru.egfedo.cbrinna.database.user.UserData
import ru.egfedo.cbrinna.telegram.data.FrontendQuestion

/**
 * This DB implementation has a list of nested DBs and just delegates
 * all work to them.
 */
class ComposedDatabase(private val contents: List<Database>) : DatabaseInterface {

    override fun addEntries(filename: String): Int {
        return -1
    }

    override fun getEntries(): Sequence<FrontendQuestion> {
        return contents.asSequence().flatMap { it.getEntries() }
    }

    override fun getEntries(userData: UserData, getMode: DatabaseInterface.GetMode): Sequence<FrontendQuestion> {
        return contents.asSequence().flatMap { it.getEntries(userData, getMode) }
    }

    override fun size(): Int {
        return contents.sumOf { it.size() }
    }

    override fun size(userData: UserData, getMode: DatabaseInterface.GetMode): Int {
        return contents.sumOf { it.size(userData, getMode) }
    }

}
