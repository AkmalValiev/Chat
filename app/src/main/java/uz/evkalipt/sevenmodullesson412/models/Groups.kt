package uz.evkalipt.sevenmodullesson412.models

import java.io.Serializable

class Groups : Serializable {

    var name:String? = null
    var uid:String? = null



    constructor()
    constructor(name: String?, uid: String?) {
        this.name = name
        this.uid = uid
    }
}