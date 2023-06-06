package com.example.myapplication

import android.graphics.Color
import android.graphics.ColorSpace.Rgb
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import com.db.williamchart.Grid
import org.w3c.dom.Text
import java.util.*

class CalendarFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //variables
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        val currentDate = MainActivity.currentDate.split('-')
        val dateTV = view.findViewById<TextView>(R.id.cal_DateTV)
        val curYear = currentDate[0].toInt()
        val curMonth = currentDate[1].toInt()
        val dbHelper = DataBaseHelper(requireContext())
        val db = dbHelper.writableDatabase
        val gridLayout = view.findViewById<GridLayout>(R.id.cal_Grid)

        //setting calendar type
        var cursor = db.rawQuery("SELECT calendarType FROM preference", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        var calendarType = true
        dateTV.text = currentDate[0]
        if(count == 0){
            dateTV.text = currentDate[1] + "-" + currentDate[0]
            calendarType = false
        }


        //colors table
        val COLORS = listOf(Color.rgb(3, 74, 84), Color.rgb(204, 0, 0), Color.rgb(255, 136, 0), Color.rgb(252, 227, 3),
        Color.rgb(6, 212, 136), Color.rgb(6, 212, 6))
        val cells = calculateCells(calendarType, curMonth, curYear)
        Log.d("HOW MANY CELLS", cells.toString())
        if(cells < 300){
            for(i in 1..cells){
                val cellView = TextView(requireContext())
                cellView.text = i.toString()
                cellView.setTextColor(Color.WHITE)
                cellView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                cellView.textSize = 15f
                cursor = db.rawQuery(generateColorQuery(curMonth, curYear, i), null)
                Log.d("kursor oddaje", "${ cursor.moveToFirst() }")

                if(cursor.moveToFirst()){
                    cellView.setBackgroundColor(COLORS[cursor.getInt(0)])
                }
                else{
                    cellView.setBackgroundColor(COLORS[0])
                }
                val params = GridLayout.LayoutParams()
                params.rowSpec = GridLayout.spec(i/7, 1f)
                params.columnSpec = GridLayout.spec(i%7, 1f)
                params.width = 10
                params.height = 10
                params.setMargins(20, 50, 20, 50)
                cellView.layoutParams = params
                gridLayout.addView(cellView)
            }
        }
        else{
            val monthsNames = listOf<String>("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII")
            for(i in 1..12){
                val daysInMonth = calculateCells(false, i, curYear)
                //Month name
                val firstCell = TextView(requireContext())
                firstCell.setBackgroundColor(Color.LTGRAY)
                firstCell.text = monthsNames[i - 1]
                firstCell.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                firstCell.textSize = 10f
                val firstParams = GridLayout.LayoutParams()
                firstParams.rowSpec = GridLayout.spec(0, 1f)
                firstParams.columnSpec = GridLayout.spec(i, 1f)
                firstParams.height = 1
                firstParams.width = 1
                firstParams.setMargins(5,5,5,5)
                firstCell.layoutParams = firstParams
                gridLayout.addView(firstCell)

                for(k in 1..daysInMonth){
                    val cellView = TextView(requireContext())

                    cursor = db.rawQuery(generateColorQuery(i, curYear, k), null)
                    Log.d("kursor oddaje", "${ cursor.moveToFirst() }")

                    if(cursor.moveToFirst()){
                        cellView.setBackgroundColor(COLORS[cursor.getInt(0)])
                    }
                    else{
                        cellView.setBackgroundColor(COLORS[0])
                    }

                    val params = GridLayout.LayoutParams()
                    params.rowSpec = GridLayout.spec(k, 1f)
                    params.columnSpec = GridLayout.spec(i, 1f)
                    params.width = 1
                    params.height = 1
                    params.setMargins(5,5,5,5)
                    cellView.layoutParams = params
                    gridLayout.addView(cellView)
                }
            }
        }


        return view
    }

    fun calculateCells(calendarType: Boolean, month: Int, year: Int): Int{
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        var days: Int

        if(!calendarType){ //monthly
            days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        else{ //yearly
            days = calendar.getActualMaximum(Calendar.DAY_OF_YEAR)
        }
        return days

    }

    fun generateColorQuery(month: Int, year: Int, day: Int): String{
        return "SELECT rate FROM mood_calendar_data WHERE year = $year AND month = $month AND day = $day;"
    }
}