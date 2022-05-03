package uz.evkalipt.sevenmodullesson412.models

import java.io.Serializable

class ModelForPager:Serializable {

    var title:String? = null
    var listgroups:List<Users>? = null
    var listChats:List<Groups>? = null

    constructor(title: String?, listgroups: List<Users>?, listChats: List<Groups>?) {
        this.title = title
        this.listgroups = listgroups
        this.listChats = listChats
    }

    constructor()
}