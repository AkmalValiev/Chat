package uz.evkalipt.sevenmodullesson412.models

import java.io.Serializable

class Users:Serializable {
    var uid:String? = null
    var displayName:String? = null
    var endSms:String? = null
    var photoUrl:String? = null
    var online:Int? = 0
    var time:String? = null
    var cloudMessageToken:String? = null



    constructor()
    constructor(
        uid: String?,
        displayName: String?,
        endSms: String?,
        photoUrl: String?,
        online: Int?,
        time: String?,
        cloudMessageToken: String?
    ) {
        this.uid = uid
        this.displayName = displayName
        this.endSms = endSms
        this.photoUrl = photoUrl
        this.online = online
        this.time = time
        this.cloudMessageToken = cloudMessageToken
    }


}