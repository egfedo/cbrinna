package telegram.data

import database.question.DatabaseInterface

data class FrontendTest (
    val id: String,
    val displayName: String,
    val database: DatabaseInterface,
    val displayNameVariation: String
) {

}