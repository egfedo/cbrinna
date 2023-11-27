package database.json

import database.question.Question
/**
 * FSR moshi just can't serialize a generic type soooo
 */
class MapWrapper () {

    var map: MutableMap<Int, Question>
    init {
        this.map = mutableMapOf();
    }
    constructor(map: MutableMap<Int, Question>) : this() {
        this.map = map
    }

}