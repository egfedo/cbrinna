package database.json

import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import database.question.Answer
import java.util.*

class TreeSetAdapter {
    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).add(AnswerAdapter()).build()
    private val jsonAdapter = moshi.adapter<MutableSet<String>>(MutableSet::class.java)
    private val answerAdapter = AnswerAdapter()
    @ToJson fun toJson(treeSet: TreeSet<Answer>) : String {
        val temp: MutableSet<Answer> = mutableSetOf()
        for (answer in treeSet) {
            temp.add(answer)
        }
        val temp2: MutableSet<String> = mutableSetOf()
        for (answer in temp) {
            temp2.add(answerAdapter.toJson(answer))
        }
        val json = jsonAdapter.toJson(temp2)
        return json
    }
    @FromJson fun fromJson(str: String) : TreeSet<Answer> {
        var mutated = str.replace("\\", "")
        val temp : Set<String>? = jsonAdapter.fromJson(mutated)
//        println(temp)
        val out : TreeSet<Answer> = TreeSet();
        if (temp != null) {
            for (question in temp) {
                out.add(answerAdapter.fromJson(question))
            }
        }
        return out
    }
}