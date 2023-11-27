package database.user

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import database.json.UserDataMapWrapper
import java.io.File
import java.io.InputStream

/**
 * Persistent user data manager. Loads the db from json and keeps it during runtime.
 * @see UserData
 */
object UserDataStorage {
    private val userMap: MutableMap<Long, UserData>
    val filename = "/Users/egfedo/IdeaProjects/CyberInna/user_data.json"
    private val adapter: JsonAdapter<UserDataMapWrapper>
    init {
        val inputStream: InputStream = File(filename).inputStream()
        val inputString = inputStream.bufferedReader().use { it.readText() }
        //println(inputString)
        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        adapter = moshi.adapter(UserDataMapWrapper::class.java)
        val output = adapter.fromJson(inputString)
        if (output != null) {
            userMap = output.map
//            println(output.set.toList()[output.set.size-1])
//            print(output.set)
        }
        else {
            userMap = mutableMapOf()
        }
//        print(this.contents)
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
        val file = File(filename)
        file.writeText("")
        val wrapper = UserDataMapWrapper(userMap)
//        println("save ${wrapper.map.size}")
        file.printWriter().use { it.print(adapter.toJson(wrapper)) }
    }
}