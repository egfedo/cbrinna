package ru.egfedo.cbrinna.database.question

import database.question.dbimpl.ComposedDatabase
import database.question.dbimpl.Database

/**
 * List of all DBs.
 */
object DatabaseList {

    private val civilLawDatabase = Database("../data/questions/db.json", prefix = "Г-", dbID = "civilLaw").init()
    private val constDatabase = Database("../data/questions/constitutionDB.json", prefix = "К-", dbID = "constLaw").init()


    val contents = mapOf(
        "civilLaw" to civilLawDatabase,
        "constLaw" to constDatabase,
        "kt1" to ComposedDatabase(listOf(civilLawDatabase, constDatabase))
    )
}
