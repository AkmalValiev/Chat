package uz.evkalipt.sevenmodullesson412.models

class Message {

    var message:String? = null
    var date:String? = null
    var formUid:String? = null
    var toUid:String? = null

    constructor()
    constructor(message: String?, date: String?, formUid: String?, toUid: String?) {
        this.message = message
        this.date = date
        this.formUid = formUid
        this.toUid = toUid
    }


}