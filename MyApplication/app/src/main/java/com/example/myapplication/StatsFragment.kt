package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.w3c.dom.Text
import java.text.DecimalFormat


class StatsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stats, container, false)
        //setting date at the top
        val dateTV = view.findViewById<TextView>(R.id.stats_dateTV)


        //Database
        val dbHelper = DataBaseHelper(requireContext())
        val db = dbHelper.writableDatabase
        //query for stressed etc
        val currentDate = MainActivity.currentDate.split('-')
        val curYear = currentDate[0]
        val curMonth = currentDate[1]


        //checking preferences
        var cursor = db.rawQuery("SELECT calendarType FROM preference", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        var calendarType = true
        dateTV.text = currentDate[0]
        if(count == 0){
            dateTV.text = currentDate[0] + "-" + currentDate[1]
            calendarType = false
        }



        val detailsChart = view.findViewById<com.db.williamchart.view.BarChartView>(R.id.stats_detailsChart)

        val details = mutableListOf<Int>(0,0,0,0,0)
        val detailNames = listOf<String>("stressed", "tired", "motivated", "proud", "inspired")

        //Here take out the stressed,tired etc.
        for(i in 0..details.size - 1){
            var sum = 0
            cursor = db.rawQuery(createQuery(detailNames[i], calendarType, curMonth, curYear), null)
            if(cursor.moveToFirst()){
                Log.d("WLAZLEM", ":D")
                while(cursor.moveToNext()) {
                    sum += cursor.getInt(0)
                    Log.d("KURSOR", "${cursor.getInt(0)}")
                }
            }
            details[i] = sum
            Log.d("DETALE", "${details[i]}")
        }
        cursor.close()

        val detailsChartLabels = listOf(
            "stressed" to details[0].toFloat(),
            "tired" to details[1].toFloat(),
            "motivated" to details[2].toFloat(),
            "proud" to details[3].toFloat(),
            "inspired" to details[4].toFloat()
        )

        detailsChart.animation.duration = 1000L
        detailsChart.animate(detailsChartLabels)


        //Avg rate
        val rateTV = view.findViewById<TextView>(R.id.stats_avgRateTV)
        val rateChart = view.findViewById<com.db.williamchart.view.LineChartView>(R.id.stats_rateChart)
        //temp
        var rateChartLabels = mutableListOf<Pair<String, Float>>()
        var fullRate: Double = 0.0
        if(!calendarType){
            cursor = db.rawQuery(getRateQuery(calendarType, curMonth, curYear), null)
            if(cursor.moveToFirst()){
                var counter = 0
                while(cursor.moveToNext()){
                    fullRate += cursor.getInt(0).toDouble()
                    rateChartLabels.add("$counter" to cursor.getInt(0).toFloat())
                    counter++
                }
                fullRate /= counter
                cursor.close()
            }

            rateTV.text = "Average rate: ${DecimalFormat("#.##").format(fullRate)}"

        }
        else{
            var fullCounter = 0
            for(i in 1..12){
                cursor = db.rawQuery(getRateQuery(false, i.toString(), curYear), null)
                if(cursor.moveToFirst()){
                    var counter = 0
                    var monthRate: Double = 0.0
                    while(cursor.moveToNext()){
                        monthRate += cursor.getInt(0).toDouble()
                        counter++
                        Log.d("$i month", "$monthRate / counter: $counter")

                    }
                    monthRate /= counter.toDouble()
                    Log.d("month", "$i : $monthRate")
                    rateChartLabels.add(i.toString() to monthRate.toFloat())

                    fullRate += monthRate
                    fullCounter++

                }
                else{
                    rateChartLabels.add(i.toString() to 1.toFloat())
                }

            }
            fullRate /= fullCounter
            rateTV.text = "Average yearly rate: ${DecimalFormat("#.##").format(fullRate)}"
        }



        //rate chart

        rateChart.animation.duration = 1000L
        rateChart.animate(rateChartLabels)

        // Inflate the layout for this fragment
        return view
    }

    fun createQuery(column: String, yearly: Boolean, currentMonth: String, currentYear: String): String{
        var query: String
        if(!yearly){
            query = "SELECT $column FROM mood_calendar_data" +
                    " WHERE month = $currentMonth AND year = $currentYear;"
        }
        else{
            query = "SELECT $column FROM mood_calendar_data" +
                    " WHERE year = $currentYear;"
        }
        return query
    }

    fun getRateQuery(yearly: Boolean, currentMonth: String, currentYear: String): String{
        var query: String
        if(!yearly){
            query = "SELECT rate FROM mood_calendar_data WHERE month = $currentMonth AND year = $currentYear ORDER BY day ASC;"
        }
        else{
            query = "SELECT rate FROM mood_calendar_data WHERE year = $currentYear ORDER BY month ASC;"
        }

        return query
    }
}