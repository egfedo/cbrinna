package database.json

import database.user.UserData

class UserDataMapWrapper () {
    var map: MutableMap<Long, UserData>
    init {
        this.map = mutableMapOf();
    }
    constructor(map: MutableMap<Long, UserData>) : this() {
        this.map = map
    }
}