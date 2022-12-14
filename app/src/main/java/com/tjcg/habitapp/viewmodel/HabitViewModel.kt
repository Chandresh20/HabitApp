package com.tjcg.habitapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tjcg.habitapp.data.Constant
import com.tjcg.habitapp.data.Habit
import java.sql.Array

class HabitViewModel : ViewModel() {

    private val _habitName  = MutableLiveData("Drinking Water")
    val habitName : LiveData<String> = _habitName

    fun setHabitName(name: String) {
        _habitName.value = name
    }

    val dateSelectedOnTodayFragment = MutableLiveData(Constant.dateToday)

    val habitIcon = MutableLiveData("calendar-check")

    var allHabitList = MutableLiveData<List<Habit>>()

    var selectedWeekCalendarDate = MutableLiveData<kotlin.Array<Int>>()

    val selectedAppPage = MutableLiveData(Constant.PAGE_1)
}