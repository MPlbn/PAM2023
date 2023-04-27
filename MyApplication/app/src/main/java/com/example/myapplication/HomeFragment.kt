package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.content.ContentValues
import android.widget.CheckBox

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        //Database
        val dbHelper = DataBaseHelper(requireContext())
        val db = dbHelper.writableDatabase

        //Check if preference table is empty, if yes, fill it with basic preferences
        val cursor = db.rawQuery("SELECT COUNT(*) FROM preference", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)

        if(count == 0){
            val prefVals = ContentValues().apply {
                put("id", 0)
                put("calendarType", 0)
            }
            db.insert("preference", null, prefVals)
        }

        //Setting date
        val currentDate = MainActivity.currentDate

        //Elements
        val dateTV = view.findViewById<TextView>(R.id.dateTV)
        val rate1Btn = view.findViewById<Button>(R.id.rate1Btn)
        val rate2Btn = view.findViewById<Button>(R.id.rate2Btn)
        val rate3Btn = view.findViewById<Button>(R.id.rate3Btn)
        val rate4Btn = view.findViewById<Button>(R.id.rate4Btn)
        val rate5Btn = view.findViewById<Button>(R.id.rate5Btn)
        val notes = view.findViewById<EditText>(R.id.notes)
        val setBtn = view.findViewById<Button>(R.id.setDayBtn)
        //Checkboxes
        val stressedCB = view.findViewById<CheckBox>(R.id.stressedCB)
        val tiredCB = view.findViewById<CheckBox>(R.id.tiredCB)
        val motivatedCB = view.findViewById<CheckBox>(R.id.motivatedCB)
        val inspiredCB = view.findViewById<CheckBox>(R.id.inspiredCB)
        val proudCB = view.findViewById<CheckBox>(R.id.proudCB)


        dateTV.text = currentDate
        //Variables
        var currentRate = 3
        var stressed = 0
        var tired = 0
        var motivated = 0
        var proud = 0
        var inspired = 0
        val note: String = notes.text?.toString() ?: ""

        //Checkbox checks, surely this could be done better
        if(stressedCB.isChecked){ stressed = 1 }
        else { stressed = 0 }

        if(tiredCB.isChecked) { tired = 1 }
        else { tired = 0 }

        if(motivatedCB.isChecked) { motivated = 1 }
        else { motivated = 0 }

        if(proudCB.isChecked) { proud = 1 }
        else { proud = 0 }

        if(inspiredCB.isChecked) { inspired = 1 }
        else { inspired = 0 }

        //Onclicks
        rate1Btn.setOnClickListener { currentRate = 1 }
        rate2Btn.setOnClickListener { currentRate = 2 }
        rate3Btn.setOnClickListener { currentRate = 3 }
        rate4Btn.setOnClickListener { currentRate = 4 }
        rate5Btn.setOnClickListener { currentRate = 5 }

        setBtn.setOnClickListener {
            //insert to database
            val values = ContentValues().apply {
                put("date", currentDate)
                put("rate", currentRate)
                put("note", note)
                put("stressed", stressed)
                put("tired", tired)
                put("motivated", motivated)
                put("proud", proud)
                put("inspired", inspired)
            }


            val cursor = db.query("mood_calendar_data", null, "date = ?", arrayOf(currentDate), null, null, null)
            val rowExists = cursor.moveToFirst()
            if(rowExists){
                val idColumnIndex = cursor.getColumnIndex("id")
                if(idColumnIndex >= 0){
                    val id = cursor.getInt(idColumnIndex)
                    db.update("mood_calendar_data", values, "id = ?", arrayOf(id.toString()))
                }
            }
            else{
                db.insert("mood_calendar_data", null, values)
            }
        }


        return view
    }

}