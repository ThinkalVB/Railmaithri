package gov.keralapolice.railmaithri

import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class API {
    companion object {
        fun post(url: String, data: JSONObject?, token: String?,
                 file: ByteArray? = null, fileName: String? = null, fileLabel: String? = null): Request {
            val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            if (file != null && fileName != null && fileLabel != null) {
                body.addFormDataPart(
                    fileLabel,
                    fileName,
                    RequestBody.create("application/octet-stream".toMediaType(), file)
                )
            }
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

        fun get(url: String, token: String? = null, parameters: JSONObject? = null): Request {
            val queryURL = url.toHttpUrl().newBuilder()
            if (parameters != null) {
                for (key in parameters.keys()) {
                    queryURL.addQueryParameter(key, parameters.get(key).toString())
                }
            }
            val request = Request.Builder()
            if(token != null){
                request.addHeader("Authorization", "Token $token")
            }
            return request.url(queryURL.build()).build()
        }

        fun patch(url: String, data: JSONObject?, token: String?,
                 file: ByteArray? = null, fileName: String? = null, fileLabel: String? = null): Request {
            val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            if (file != null && fileName != null && fileLabel != null) {
                body.addFormDataPart(
                    fileLabel,
                    fileName,
                    RequestBody.create("application/octet-stream".toMediaType(), file)
                )
            }
            if (data != null) {
                for (key in data.keys()) {
                    body.addFormDataPart(key, data.get(key).toString())
                }
            }

            val request = Request.Builder()
            if(token != null){
                request.addHeader("Authorization", "Token $token")
            }
            return request.url(url).method("PATCH", body.build()).build()
        }
    }

}