package database.question

import database.question.dbimpl.ComposedDatabase
import database.question.dbimpl.Database

/**
 * List of all DBs.
 */
object DatabaseList  {
    val contents: MutableMap<String, DatabaseInterface> = mutableMapOf()
    init {

        // Hardcoded atm

        val civilLawDatabase = Database("../data/questions/db.json", prefix="Г-", dbID = "civilLaw")
//        println("civil law db size: ${civilLawDatabase.size()}")
//        println(civilLawDatabase.addEntries("/Users/egfedo/IdeaProjects/CyberInna/test.txt"))
//    println(civilLawDatabase.addEntries("/Users/egfedo/IdeaProjects/CyberInna/test.txt"))
//        println(civilLawDatabase.addEntries("/Users/egfedo/IdeaProjects/CyberInna/civil2.txt"))
        val constDatabase = Database("../data/questions/constitutionDB.json", prefix="К-", dbID = "constLaw")
        //FileParser.parse("/Users/egfedo/IdeaProjects/CyberInna/const1.txt")
//        constDatabase.addEntries("/Users/egfedo/IdeaProjects/CyberInna/const1.txt")
//        println(constDatabase.addEntries("/Users/egfedo/IdeaProjects/CyberInna/const2.txt"))
        val kt1Database = ComposedDatabase(listOf(civilLawDatabase, constDatabase))
        contents["civilLaw"] = civilLawDatabase
        contents["constLaw"] = constDatabase
        contents["kt1"] = kt1Database
    }
}