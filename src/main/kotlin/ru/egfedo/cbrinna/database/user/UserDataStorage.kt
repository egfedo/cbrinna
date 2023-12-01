package ru.egfedo.cbrinna.database.user

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

/**
 * Persistent user data manager. Loads the db from json and keeps it during runtime.
 * @see UserData
 */
object UserDataStorage {
    private val userMap: MutableMap<Long, UserData> = mutableMapOf()
    val filename = "../data/user/user_data.json"

    fun init() {
        userMap.clear()
        userMap += Json.decodeFromString<Map<Long, UserData>>(Path(filename).readText())
    }

    /**
     * Returns a copy of user data in storage.
     * @param key user telegram id
     * @return user data
     */
    operator fun get(key: Long) : UserData {
        val data = userMap[key] ?: return UserData(key)
        return data.copy()
    }

    /**
     * Writes a copy of UserData to user data storage.
     */
    operator fun set(key: Long, value: UserData) {
        userMap[key] = value.copy()
        save()                      // Never ask me why I save it each time :/
    }

    private fun save() {
        Path(filename).writeText(Json.encodeToString(userMap))
    }
}
