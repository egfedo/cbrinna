package database.question

import java.util.TreeSet

/**
 * Sort of a backend Question. Has a java TreeSet for answers since
 * for some reason sortedSetOf() always return TreeSet and it needs
 * to be sorted for hash to work.
 * Hash allows to filter already existing questions with a set.
 */
class Question() {
    enum class Type {
        OneAnswer, MultipleAnswers
    }
    var text: String
    var answers: TreeSet<Answer>
    var type: Type
    init {
        this.text = ""
        this.answers = TreeSet<Answer>()
        this.type = Type.OneAnswer
    }
    constructor(string: String, answers: TreeSet<Answer>) : this() {
        this.text = string
        this.answers = answers
        var count = 0;
        for (a in answers) {
            if (a.correct)
                count++
        }
        assert(count > 0)
        if (count > 1)
            this.type = Type.MultipleAnswers
        else
            this.type = Type.OneAnswer

    }
    override fun toString() : String {
        val builder = StringBuilder();
        builder.append("Вопрос: ").append(text).append("\n\n")
        var i = 1;
        for (a in answers) {
            builder.append(i).append(". ").append(a.text)
//            if (a.correct)
//                builder.append(" * ")
            builder.append("\n")
            i++
        }
        if (type == Type.OneAnswer)
            builder.append("\nВыберите один правильный ответ\n")
        else
            builder.append("\nВыберите один или несколько правильных ответов\n")
        return builder.toString()
    }
    // dk if I need it
    override operator fun equals(other: Any?) : Boolean {
        if (other is Question) {
            return (this.type == other.type) && (this.text == other.text)
        }
        return false
    }

    override fun hashCode(): Int {
        var out: String = text.hashCode().toString()
        for (a in answers)
            out += a.hashCode().toString()
        out += type.hashCode().toString()
        return out.hashCode()
    }
}