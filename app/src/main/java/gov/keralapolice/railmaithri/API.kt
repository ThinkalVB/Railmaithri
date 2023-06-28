package gov.keralapolice.railmaithri

import okhttp3.MultipartBody
import okhttp3.Request
import org.json.JSONObject

class API {
    companion object {
        fun post(url: String, data: JSONObject?, token: String?): Request {
            val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            if (data != null) {
                for (key in data.keys()) {
                    body.addFormDataPart(key, data.get(key).toString())
                }
            }
            val request = Request.Builder()
            if(token != null){
                request.addHeader("Authorization", "Token $token")
            }
            return request.url(url).method("POST", body.build()).build()
        }

        fun get(url: String, token: String?): Request {
            val request = Request.Builder()
            if(token != null){
                request.addHeader("Authorization", "Token $token")
            }
            return request.url(url).build()
        }
    }

}