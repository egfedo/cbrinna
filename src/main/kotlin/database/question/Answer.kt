package database.question

/**
 * Answer to a question. Implements Comparable for TreeSet to work.
 *
 */
class Answer () : Comparable<Answer> {
    var text: String;
    var correct : Boolean
    init {
        this.text = "text"
        this.correct = false
    }
    constructor(text: String, correct: Boolean) : this() {
        this.text = text
        this.correct = correct
    }
//    override operator fun equals(other: Any?): Boolean {
//        if (other is database.question.Answer)
//            return (this.text == other.text) && (this.correct == other.correct)
//        return false
//    }
    override fun compareTo(other: Answer) : Int {
        if (this.correct == other.correct) {
            return if (this.text > other.text) 1
            else if (this.text < other.text) -1
            else 0
        }
        else if (this.correct)
            return 1
        else
            return -1

    }

    override fun hashCode(): Int {
        val temp = text
        return temp.hashCode()
    }
    override fun toString() : String {
        return "$text - $correct"
    }

}