package gov.keralapolice.railmaithri

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.json.JSONObject

class ChangePassword : AppCompatActivity() {
    private lateinit var token: String
    private lateinit var clientNT:      OkHttpClient
    private lateinit var currentPasswordET: EditText
    private lateinit var newPasswordET: EditText
    private lateinit var confirmPasswordET: EditText
    private lateinit var changeBT: Button
    private lateinit var showHideCurrentPasswordIV: ImageView
    private lateinit var showHideNewPasswordIV: ImageView
    private lateinit var showHideConfirmPasswordIV: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_password)
        supportActionBar!!.hide()

        clientNT = OkHttpClient().newBuilder().build()
        token = Helper.getData(this, Storage.TOKEN)!!
        currentPasswordET = findViewById(R.id.CurrentPasswordET)
        newPasswordET = findViewById(R.id.NewPasswordET)
        confirmPasswordET = findViewById(R.id.ConfirmPasswordET)
        changeBT = findViewById(R.id.changeBT)

        showHideCurrentPasswordIV = findViewById(R.id.ShowHideCurrentPasswordIV)
        showHideNewPasswordIV = findViewById(R.id.ShowHideNewPasswordIV)
        showHideConfirmPasswordIV = findViewById(R.id.ShowHideConfirmPasswordIV)

        // Set click listeners for the show/hide password icons
        showHideCurrentPasswordIV.setOnClickListener {
            togglePasswordVisibility(
                currentPasswordET,
                showHideCurrentPasswordIV
            )
        }
        showHideNewPasswordIV.setOnClickListener {
            togglePasswordVisibility(
                newPasswordET,
                showHideNewPasswordIV
            )
        }
        showHideConfirmPasswordIV.setOnClickListener {
            togglePasswordVisibility(
                confirmPasswordET,
                showHideConfirmPasswordIV
            )
        }

        // Set click listener for the Save button
        changeBT.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch { changepassword() }
        }
    }

    private fun changepassword() {
        Handler(Looper.getMainLooper()).post {
            changeBT.isClickable = false
        }

        val data = JSONObject()
        data.put("old_password", currentPasswordET.text)
        data.put("new_password", newPasswordET.text)
        data.put("confirm_password", confirmPasswordET.text)

        try {
            val request = API.post(URL.PASSWORD_CHANGE, data, token)
            val response = clientNT.newCall(request).execute()
            Log.e("Railmaithri", response.code.toString())
            if (response.isSuccessful || response.code == 401) {
                Helper.showToast(this, "Password Changed Successfully")
                finish()
            } else {
                val errorMessage = Helper.getError(response.body!!.string())
                Helper.showToast(this, errorMessage, Toast.LENGTH_LONG)
            }
        } catch (e: Exception) {
            Log.e("Railmaithri", e.toString())
            Helper.showToast(this, "Server unreachable !!")
        } finally {
            Handler(Looper.getMainLooper()).post {
                changeBT.isClickable = true
            }
        }
    }
    private fun togglePasswordVisibility(editText: EditText, imageView: ImageView) {
        // Toggle between password visibility and invisibility
        if (editText.transformationMethod == PasswordTransformationMethod.getInstance()) {
            // Show password
            editText.transformationMethod = null
            imageView.setImageResource(R.drawable.eye) // Set the eye on drawable
        } else {
            // Hide password
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            imageView.setImageResource(R.drawable.invisible) // Set the eye off drawable
        }
    }
}