package com.example.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.time.LocalDate
import java.util.Calendar

class DataBaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object{
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "MoodTrackerDatabase.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS mood_calendar_data (id INTEGER PRIMARY KEY," +
                " day INTEGER, month INTEGER, year INTEGER, rate INTEGER, stressed INTEGER, tired INTEGER, motivated INTEGER, proud INTEGER, inspired INTEGER, note TEXT)")
        db?.execSQL("CREATE TABLE IF NOT EXISTS preference (id INTEGER PRIMARY KEY, calendarType INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS mood_calendar_data")
        db?.execSQL("DROP TABLE IF EXISTS preference")
    }
}


class MainActivity : AppCompatActivity() {
    companion object{
        var currentDate: String = "${LocalDate.now().year}-${LocalDate.now().monthValue}-${LocalDate.now().dayOfMonth}"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //variables
        val homeScreenFragment = HomeFragment()
        val homeScreenBtn = findViewById<Button>(R.id.homebtn)
        val settingsBtn = findViewById<Button>(R.id.setbtn)
        val calendarBtn = findViewById<Button>(R.id.calbtn)
        val statsBtn = findViewById<Button>(R.id.statsbtn)

        homeScreenBtn.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentFrame, homeScreenFragment).commit()
        }

        settingsBtn.setOnClickListener{
            val SettingsFragment = Settings()
            supportFragmentManager.beginTransaction().replace(R.id.fragmentFrame, SettingsFragment).commit()
        }

        statsBtn.setOnClickListener {
            val StatsFragment = StatsFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragmentFrame, StatsFragment).commit()
        }

        calendarBtn.setOnClickListener {
            val CalendarFragment = CalendarFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragmentFrame, CalendarFragment).commit()
        }

        supportFragmentManager.beginTransaction().add(R.id.fragmentFrame, homeScreenFragment).commit()
    }
}