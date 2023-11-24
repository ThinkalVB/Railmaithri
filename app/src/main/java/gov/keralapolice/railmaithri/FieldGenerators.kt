package gov.keralapolice.railmaithri

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.icu.util.Calendar
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList


// This exception is thrown when the field is required but empty when calling getData()
class FieldRequiredException(message: String) : Exception(message){

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
    private var _fieldType       = fieldType

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
                _editText.isVerticalScrollBarEnabled = true
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
        _editText.gravity = Gravity.TOP

        _editText.layoutParams  = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            adjustedFieldHeight)
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

        var fieldValue  = jsonObject.optString(actualLabel, "")
        if(fieldValue != "null"){
            if(_fieldType == "date"){
                var indexOfTime = fieldValue.indexOf("T")
                if(indexOfTime != -1){
                    fieldValue = fieldValue.substring(0, indexOfTime)
                }
            }
            _editText.setText(fieldValue)
        }
    }

    fun exportData(jsonObject: JSONObject, filedLabel: String? = null, tailPadding: String? = null){
        var actualLabel = _fieldLabel
        if (filedLabel != null){
            actualLabel = filedLabel
        }

        if (_editText.text.isEmpty()){
            jsonObject.remove(actualLabel)
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
                   isReadOnly: Boolean = false
) {
    private var _fieldLabel = fieldLabel
    private var _fieldData = fieldData
    private var _isHidden = isHidden
    private var _isRequired = isRequired

    private val _linearLayout: LinearLayout
    private val _textView: TextView
    private val _spinner: Spinner
    private val _arrayAdapter: ArrayAdapter<String>
    private val _searchEditText: EditText
    private val _popup: PopupWindow
    private var _isSelectionMade: Boolean = false


    init {
        val valuesList = ArrayList<String>()
        if (addEmptyValue) {
            valuesList.add("")
        }
        for (i in 0 until _fieldData.length()) {
            val arrayElement = _fieldData.getJSONObject(i)
            val value = arrayElement.getString("name")
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
        val scale = context.resources.displayMetrics.density
        val padding8dp = (8 * scale + 0.5f).toInt()
        _textView.setPadding(padding8dp, 0, padding8dp, 0)
        _linearLayout.addView(_textView)
        _spinner = Spinner(context)

        val adjustedFieldHeight = (fieldHeight * scale + 0.5f).toInt()
        _spinner.setPadding(padding8dp, 0, padding8dp, 0)
        _spinner.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            adjustedFieldHeight
        )

        _arrayAdapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            valuesList
        )
        _arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        _spinner.adapter = _arrayAdapter
        _linearLayout.addView(_spinner)

        if (isRequired) {
            _textView.setTextColor(Color.RED)
        } else {
            _textView.setTextColor(Color.GRAY)
        }

        if (isReadOnly) {
            _spinner.isEnabled = false
            _spinner.isFocusable = false
        }
        if (isHidden) {
            _linearLayout.visibility = View.GONE
        }
        _popup = PopupWindow(context)
        val popupView = LayoutInflater.from(context).inflate(R.layout.spinner_dropdown, null)

        _searchEditText = popupView.findViewById(R.id.searchEditText)
        val listView = popupView.findViewById<ListView>(R.id.listView)

        _popup.contentView = popupView
        _popup.width = LinearLayout.LayoutParams.WRAP_CONTENT
        _popup.height = LinearLayout.LayoutParams.WRAP_CONTENT
        _popup.isFocusable = true
        _popup.isOutsideTouchable = false

        // Create a custom adapter for the spinner dropdown
        val customAdapter =
            ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, valuesList)
        listView.adapter = customAdapter

        // Set a TextChangedListener to the search EditText
        _searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Filter the values in the adapter based on the search text
                // Log.d("Change","Text Changed $s")
                customAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Set the custom adapter for the spinner dropdown
        _spinner.adapter = customAdapter

        // Set an OnItemClickListener to handle item selection in the dropdown
        listView.setOnItemClickListener { _, _, position, _ ->
            if (position != AdapterView.INVALID_POSITION) {
                // Log.d("Debug", "Dropdown position $position")
                val selectedValue = customAdapter.getItem(position)
                //Log.d("Debug", "Dropdown position value $selectedValue")
                val selectedPosition = valuesList.indexOf(selectedValue)
                //Log.d("Debug", "ValueList index $selectedPosition")
                _spinner.adapter = ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_spinner_dropdown_item,
                    valuesList
                )
                _spinner.setSelection(selectedPosition)
                _searchEditText.text.clear() // Clear the search text
                _isSelectionMade = true
                _popup.dismiss()

            } else {
                //Log.d("Error","I shouldn't be here")
            }
        }

        // Set a dismiss listener to handle dismissal only when a selection has been made
        _popup.setOnDismissListener {
            if (!_isSelectionMade) {
                // If no selection has been made, reset the search field
                _searchEditText.text.clear()
            }
        }

        // Set an OnTouchListener to show the popup on spinner click
        _spinner.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                _popup.showAsDropDown(_spinner)
                _isSelectionMade = false
                true
            } else {
                false
            }
        }
    }


    fun importData(jsonObject: JSONObject, filedLabel: String? = null) {
        var actualLabel = _fieldLabel
        if (filedLabel != null) {
            actualLabel = filedLabel
        }

        var fieldValue: Any? = jsonObject.opt(actualLabel) // Use opt to handle null
        if (fieldValue != null) {
            // Handle different types accordingly
            when (fieldValue) {
                is Boolean -> {
                    // Handle Boolean type
                    fieldValue = if (fieldValue == false) "No" else "Yes"
                }

                is String -> {
                    // Handle String type
                    if (fieldValue == "true") {
                        fieldValue = "Yes"
                    } else if (fieldValue == "false") {
                        fieldValue = "No"
                    }
                }
            }
            val valuePos = _arrayAdapter.getPosition(fieldValue.toString())
            if (valuePos != -1) {
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
    }

        fun exportData(jsonObject: JSONObject, filedLabel: String? = null) {
            var actualLabel = _fieldLabel
            if (filedLabel != null) {
                actualLabel = filedLabel
            }

            if (_linearLayout.visibility == View.VISIBLE) {
                val selectedID = getData()
                if (selectedID != null) {
                    jsonObject.put(actualLabel, selectedID)
                }
            }
        }

        fun getData(): Any? {
            var selectedID: Any? = null
            for (i in 0 until _fieldData.length()) {
                val arrayElement = _fieldData.getJSONObject(i)
                val value = arrayElement.getString("name")
                if (value == _spinner.selectedItem) {
                    selectedID = arrayElement.get("id")
                    break
                }
            }
            return selectedID
        }

        fun hide() {
            _linearLayout.visibility = View.GONE
            _isHidden = true
        }

        fun show() {
            _linearLayout.visibility = View.VISIBLE
            _isHidden = false
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

        fun isSelectionMade(): Boolean {
            return _isSelectionMade
        }

        fun setSelectedItem(item: String) {
            val position = _arrayAdapter.getPosition(item)
            if (position != -1) {
                _spinner.setSelection(position)
            }
        }
    }
