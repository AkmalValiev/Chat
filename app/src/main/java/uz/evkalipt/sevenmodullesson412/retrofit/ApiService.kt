package uz.evkalipt.sevenmodullesson412.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import uz.evkalipt.sevenmodullesson412.retrofit.models.MyResponce
import uz.evkalipt.sevenmodullesson412.retrofit.models.Sender

interface ApiService {

    @Headers(
        "Content-type:application/json",
        "Authorization:key=AAAAP6YYrSw:APA91bF7kiBrWU-KO56fsU105GM-9-P5EeShxDulLN3xkvAwjxJz9vQRzjUeH51n9VvfM9IymsbYjNuDOkPNIe44CpX5anI03xFJLMKBJNqOP-X1fQ1YVY9XlzCB_ARlLICAjAf3DphN"
    )
    @POST("fcm/send")
    fun sendNotification(@Body sender: Sender):Call<MyResponce>


}