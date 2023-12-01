package ru.egfedo.cbrinna.telegram.data

import ru.egfedo.cbrinna.database.question.DatabaseInterface

data class FrontendTest (
    val id: String,
    val displayName: String,
    val database: DatabaseInterface,
    val displayNameVariation: String
) {

}
