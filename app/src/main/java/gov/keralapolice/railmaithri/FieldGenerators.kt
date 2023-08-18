package gov.keralapolice.railmaithri

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.icu.util.Calendar
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList


// This exception is thrown when the field is required but empty when calling getData()
class FieldRequiredException(message: String) : Exception(message){
    init{
        Log.e("Railmaithri", message)
    }
}

class FieldEditText(context: Context,
                    isHidden: Boolean = false,
                    isReadOnly: Boolean = false,
                    fieldLabel: String = "",
                    fieldHintText: String = "",
                    fieldMinLines: Int = 1,
                    fieldMaxLines: Int = 1,
                    fieldHeight: Int = 48,
                    fieldName: String = "",
                    fieldType: String = "text",
                    isRequired: Boolean = false) {
    private var _fieldName       = fieldName
    private var _fieldLabel      = fieldLabel
    private var _isRequired      = isRequired
    private var _isHidden        = isHidden

    private val _linearLayout:   LinearLayout
    private val _textView:       TextView
    private val _editText:       EditText

    init {
        _linearLayout = LinearLayout(context)
        _linearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        _linearLayout.orientation = LinearLayout.VERTICAL

        _textView        = TextView(context)
        _textView.text   = fieldName
        _textView.setTypeface(null, Typeface.BOLD)
        val scale               = context.resources.displayMetrics.density
        val padding8dp          = (8 * scale + 0.5f).toInt()
        _textView.setPadding(padding8dp, 0, padding8dp, 0)
        _linearLayout.addView(_textView)

        _editText       = EditText(context)
        when (fieldType) {
            "phone" -> {
                _editText.inputType = InputType.TYPE_CLASS_PHONE
            }
            "email" -> {
                _editText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }
            "number" -> {
                _editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
            "datetime" -> {
                _editText.inputType = InputType.TYPE_CLASS_DATETIME
            }
            "text" -> {
                _editText.inputType = InputType.TYPE_CLASS_TEXT
            }
            "password" -> {
                _editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            "multiline" -> {
                _editText.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
            }
            "date" -> {
                // No specific class is available hence simple text is used
                _editText.inputType = InputType.TYPE_CLASS_TEXT
                _editText.setOnClickListener {
                    val calendar = Calendar.getInstance()
                    val yearNow  = calendar.get(Calendar.YEAR)
                    val monthNow = calendar.get(Calendar.MONTH)
                    val dayNow   = calendar.get(Calendar.DAY_OF_MONTH)
                    val datePickerDialog = DatePickerDialog(context,
                        { _, year, monthOfYear, dayOfMonth ->
                            val date = "$year-${monthOfYear+1}-$dayOfMonth"
                            _editText.setText(date)
                        }, yearNow, monthNow, dayNow)
                    datePickerDialog.show()
                }
            }
            "time" -> {
                // No specific class is available hence simple text is used
                _editText.inputType = InputType.TYPE_CLASS_TEXT
                _editText.setOnClickListener {
                    val timePickerDialog = TimePickerDialog(context,
                        { _, hourOfDay, minutes ->
                            val time = "$hourOfDay:${minutes}"
                            _editText.setText(time)
                        }, 0, 0, false)
                    timePickerDialog.show()
                }
            }
        }
        _editText.hint      = fieldHintText
        _editText.minLines  = fieldMinLines
        _editText.maxLines  = fieldMaxLines

        val adjustedFieldHeight = (fieldHeight * scale + 0.5f).toInt()
        _editText.setPadding(padding8dp, 0, padding8dp, 0)
        _editText.setBackgroundResource(R.drawable.rectangular_boarder)

        _editText.layoutParams  = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            adjustedFieldHeight)
//        _editText.doAfterTextChanged {
//            if(isRequired){
//                if(_editText.text.isBlank()){
//                    _textView.setTextColor(Color.RED)
//                } else{
//                    _textView.setTextColor(Color.GRAY)
//                }
//            }
//        }
        _linearLayout.addView(_editText)

        if(isRequired){
            _textView.setTextColor(Color.RED)
        } else{
            _textView.setTextColor(Color.GRAY)
        }

        if(isReadOnly) {
            _editText.isEnabled     = false
            _editText.isFocusable   = false
        }
        if(isHidden){
            _linearLayout.visibility = View.GONE
        }
    }

    fun importData(jsonObject: JSONObject, filedLabel: String? = null){
        var actualLabel = _fieldLabel
        if (filedLabel != null){
            actualLabel = filedLabel
        }

        val fieldValue  = jsonObject.optString(actualLabel, "")
        _editText.setText(fieldValue)
        if(_isRequired && fieldValue.isBlank()){
            _textView.setTextColor(Color.RED)
        } else{
            _textView.setTextColor(Color.GRAY)
        }
    }

    fun exportData(jsonObject: JSONObject, filedLabel: String? = null, tailPadding: String? = null){
        var actualLabel = _fieldLabel
        if (filedLabel != null){
            actualLabel = filedLabel
        }

        if (_linearLayout.visibility == View. VISIBLE){
            if(_editText.text.isNotEmpty()){
                jsonObject.put(actualLabel, getData(tailPadding))
            } else {
                if(_isRequired) {
                    val errorMessage = "$_fieldName is a required field"
                    throw(FieldRequiredException(errorMessage))
                }
            }
        }
    }

    fun getData(tailPadding: String? = null) : String {
        return if(tailPadding == null){
            _editText.text.toString()
        }else {
            _editText.text.toString() + tailPadding
        }
    }

    fun hide() {
        _linearLayout.visibility = View.GONE
        _isHidden                = true
    }

    fun show() {
        _linearLayout.visibility = View.VISIBLE
        _isHidden                = false
    }

    fun markAsRequired() {
        _isRequired = true
        _textView.setTextColor(Color.RED)
    }

    fun markAsNotRequired() {
        _isRequired = false
        _textView.setTextColor(Color.GRAY)
    }

    fun getLayout(): LinearLayout {
        return  _linearLayout
    }
}

class FieldSpinner(context: Context,
                   fieldData: JSONArray = JSONArray(),
                   fieldLabel: String = "",
                   fieldName: String = "",
                   fieldHeight: Int = 48,
                   addEmptyValue: Boolean = false,
                   isHidden: Boolean = false,
                   isRequired: Boolean = false,

                   isReadOnly: Boolean = false) {
    private var _fieldLabel = fieldLabel
    private var _fieldData  = fieldData
    private var _isHidden   = isHidden
    private var _isRequired = isRequired

    private val _linearLayout:  LinearLayout
    private val _textView:      TextView
    private val _spinner:       Spinner
    private val _arrayAdapter:  ArrayAdapter<String>

    init {
        val valuesList = ArrayList<String>()
        if(addEmptyValue){
            valuesList.add("")
        }
        for (i in 0 until _fieldData.length()) {
            val arrayElement = _fieldData.getJSONObject(i)
            val value        = arrayElement.getString("name")
            valuesList.add(value)
        }
        _linearLayout = LinearLayout(context)
        _linearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        _linearLayout.orientation = LinearLayout.VERTICAL

        _textView = TextView(context)
        _textView.text = fieldName
        _textView.setTypeface(null, Typeface.BOLD)
        val scale               = context.resources.displayMetrics.density
        val padding8dp          = (8 * scale + 0.5f).toInt()
        _textView.setPadding(padding8dp, 0, padding8dp, 0)
        _linearLayout.addView(_textView)
        _spinner = Spinner(context)

        val adjustedFieldHeight = (fieldHeight * scale + 0.5f).toInt()
        _spinner.setPadding(padding8dp, 0, padding8dp, 0)
        _spinner.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            adjustedFieldHeight
        )

        _arrayAdapter = ArrayAdapter(context,
            android.R.layout.simple_spinner_item,
            valuesList)
        _arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        _spinner.adapter = _arrayAdapter
        _linearLayout.addView(_spinner)

        if(isRequired){
            _textView.setTextColor(Color.RED)
        } else{
            _textView.setTextColor(Color.GRAY)
        }

        if (isReadOnly) {
            _spinner.isEnabled   = false
            _spinner.isFocusable = false
        }
        if(isHidden){
            _linearLayout.visibility = View.GONE
        }
    }

    fun importData(jsonObject: JSONObject, filedLabel: String? = null){
        var actualLabel = _fieldLabel
        if (filedLabel != null){
            actualLabel = filedLabel
        }

        val fieldValue  = jsonObject.get(actualLabel)?: ""
        if(fieldValue is String){
            val valuePos  = _arrayAdapter.getPosition(fieldValue)
            _spinner.setSelection(valuePos)
        } else if (fieldValue is Int) {
            for (i in 0 until _fieldData.length()) {
                val arrayElement = _fieldData.getJSONObject(i)
                val id = arrayElement.getInt("id")
                if (id == fieldValue) {
                    val value = arrayElement.getString("name")
                    val valuePos = _arrayAdapter.getPosition(value)
                    _spinner.setSelection(valuePos)
                    break
                }
            }
        }
    }

    fun exportData(jsonObject: JSONObject, filedLabel: String? = null) {
        var actualLabel = _fieldLabel
        if (filedLabel != null){
            actualLabel = filedLabel
        }

        if (_linearLayout.visibility == View.VISIBLE){
            val selectedID = getData()
            if (selectedID != null){
                jsonObject.put(actualLabel, selectedID)
            }
        }
    }

    fun getData(): Any? {
        var selectedID: Any? = null
        for (i in 0 until _fieldData.length()) {
            val arrayElement = _fieldData.getJSONObject(i)
            val value        = arrayElement.getString("name")
            if (value == _spinner.selectedItem){
                selectedID = arrayElement.get("id")
                break
            }
        }
        return selectedID
    }

    fun hide() {
        _linearLayout.visibility = View.GONE
        _isHidden                = true
    }

    fun show() {
        _linearLayout.visibility = View.VISIBLE
        _isHidden                = false
    }

    fun markAsRequired() {
        _isRequired = true
        _textView.setTextColor(Color.RED)
    }

    fun markAsNotRequired() {
        _isRequired = false
        _textView.setTextColor(Color.GRAY)
    }

    fun getLayout(): LinearLayout {
        return _linearLayout
    }

    fun getSpinner(): Spinner {
        return _spinner
    }
}
