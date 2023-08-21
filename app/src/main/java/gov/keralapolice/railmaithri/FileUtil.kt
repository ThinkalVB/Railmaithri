package gov.keralapolice.railmaithri

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONObject


class FileUtil(_activity: AppCompatActivity, _locationLY: ConstraintLayout, _fieldLabel: String) {
    private var file:         ByteArray?        = null
    private var fileName:     String?           = null
    private var fieldLabel:   String?           = null
    private var uuid:         String?           = null
    private var layout:       ConstraintLayout? = null
    private var urlLink:      String            = "null"
    private var isHidden:     Boolean           = false

    private var selectFileBT: Button
    private var deleteFileBT: Button
    private var fileNameTV:   TextView

    init {
        layout       = _locationLY
        fieldLabel   = _fieldLabel
        selectFileBT = _locationLY.findViewById(R.id.select_file)
        deleteFileBT = _locationLY.findViewById(R.id.delete_file)
        fileNameTV   = _locationLY.findViewById(R.id.file_name)

        val selectionResult = _activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val inputStream = _activity.contentResolver.openInputStream(uri)
                    file = inputStream.use { it?.readBytes() }!!
                    inputStream?.close()

                    val cursor    = _activity.contentResolver.query(uri, null, null, null, null)
                    val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor?.moveToFirst()
                    fileName = nameIndex?.let { cursor.getString(it) }.toString()
                    cursor?.close()

                    deleteFileBT.isClickable = true
                    fileNameTV.text          = fileName
                }
            }
        }
        selectFileBT.setOnClickListener {
            if (urlLink == "null") {
                val intent = Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT)
                selectionResult.launch(Intent.createChooser(intent, "Select a file"))
            } else {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlLink))
                _activity.startActivity(intent)
            }
        }
        deleteFileBT.setOnClickListener {
            deleteFileBT.isClickable = false
            fileNameTV.text          = "No file selected"
            file                     = null
        }
    }

    fun haveFile(): Boolean {
        return !(file == null || fileName == null)
    }

    fun enableUpdate () {
        deleteFileBT.isClickable = true
        selectFileBT.isClickable = true
    }

    fun disableUpdate() {
        deleteFileBT.isClickable = false
        selectFileBT.isClickable = false
    }

    fun loadFile(context: Context, _uuid: String, _fileName: String) {
        file            = Helper.loadFile(context, _uuid)
        fileName        = _fileName
        fileNameTV.text = fileName
    }

    fun removeFile(context: Context) {
        Helper.purgeFile(context, uuid!!)
    }

    fun saveFile(context: Context, _uuid: String) {
        uuid = _uuid
        Helper.saveFile(context, file!!, uuid!!)
    }

    fun getFile(): ByteArray? {
        return file
    }

    fun getFileName(): String? {
        return fileName
    }

    fun getFieldLabel(): String? {
        return fieldLabel
    }

    fun hide() {
        layout?.visibility = View.GONE
        isHidden           = true
    }

    fun show() {
        layout?.visibility = View.VISIBLE
        isHidden           = false
    }

    fun registerLink(formData: JSONObject) {
        urlLink = formData.optString(fieldLabel, "null")
        deleteFileBT.isClickable = false
        if(urlLink == "null"){
            selectFileBT.isClickable = false
            fileNameTV.text = "No files"
        } else {
            fileNameTV.text = "Click to open the file"
        }
    }
}