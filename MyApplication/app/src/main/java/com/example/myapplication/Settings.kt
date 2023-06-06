package com.example.myapplication

import android.content.ContentValues
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.app.DatePickerDialog
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import org.w3c.dom.Text
import java.util.*

class Settings : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        //database init
        val dbHelper = DataBaseHelper(requireContext())
        val db = dbHelper.writableDatabase

        var calType = 0

        //Buttons
        val monthly = view.findViewById<Button>(R.id.monthlyBtn)
        val yearly = view.findViewById<Button>(R.id.yearlyBtn)
        val set = view.findViewById<Button>(R.id.settingsSetBtn)
        val dateBtn = view.findViewById<Button>(R.id.datePickBtn)

        val dateTV = view.findViewById<TextView>(R.id.settingsDateTV)

        dateTV.setText(MainActivity.currentDate)

        monthly.setOnClickListener { calType = 0 }
        yearly.setOnClickListener { calType = 1 }

        set.setOnClickListener {
            val values = ContentValues().apply{
                put("calendarType", calType)
            }

            db.update("preference", values, "id = ?", arrayOf("0")) //updates preferences

        }
        //Datepicker
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        dateBtn.setOnClickListener {
            val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{view, mYear, mMonth, mDay ->
                MainActivity.currentDate = "$mYear-${mMonth+1}-$mDay"
                dateTV.setText(MainActivity.currentDate)
            }, year, month, day)

            dpd.show()
        }

        return view
    }
}

private fun showDatePicker(){


}