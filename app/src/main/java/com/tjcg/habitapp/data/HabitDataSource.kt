package com.tjcg.habitapp.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tjcg.habitapp.LoginActivity
import com.tjcg.habitapp.MainActivity
import com.tjcg.habitapp.R
import com.tjcg.habitapp.remote.ApiService
import com.tjcg.habitapp.remote.PresetResponse
import com.tjcg.habitapp.viewmodel.HabitViewModel
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.reflect.Type
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.exitProcess

@Singleton
class HabitDataSource @Inject constructor(private val habitDao: HabitDao, private val
    calendarDao: HabitCalendarDao) : HabitDatabaseInterface,
    HabitCalendarInterface {

    private val mainScope = CoroutineScope(Dispatchers.Main)
    private lateinit var ctx: Context
    private lateinit var viewModel : HabitViewModel
    private lateinit var storageDir : File
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var gson: Gson
    private val notificationSounds = ArrayList<SoundEffect>()

    private val mainHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    fun setupViewModel(ctx: Context) {
        this.ctx = ctx
        viewModel = ViewModelProvider(ctx as MainActivity)[HabitViewModel::class.java]
        storageDir = ctx.getExternalFilesDir("Data")!!
        sharedPreferences = ctx.getSharedPreferences(Constant.PREFS_APP, Context.MODE_PRIVATE)
        gson = Gson()
    }

    fun provideViewModel() = viewModel

    private val exeService = Executors.newFixedThreadPool(4)

    override fun addHabit(habit: Habit) {
        exeService.execute {
            habitDao.insertNewHabits(habit)
            updateHabitList()
        }
    }

    override suspend fun getAllHabitsAsync(): Deferred<List<Habit>> =
        coroutineScope {
            async(Dispatchers.IO) {
                return@async habitDao.getAllHabits()
            }
        }


    override suspend fun getHabitByIdAsync(id: Int) : Deferred<Habit?> =
        coroutineScope {
            async(Dispatchers.IO) {
                return@async habitDao.getHabitById(id)
            }
        }

    override suspend fun updateHabitAsync(habit: Habit): Unit =
        coroutineScope {
            async(Dispatchers.IO) {
                habitDao.updateHabit(habit)
                Log.d("HabitUpdate", "${habit.title} updated")
            }
        }


    private fun updateHabitList() {
        mainScope.launch {
            val habits = getAllHabitsAsync().await()
            viewModel.allHabitList.value = habits
        }
    }

    override fun addInCalendar(habitCalendar: HabitCalendar) {
        exeService.execute {
            calendarDao.insertInCalendar(habitCalendar)
        }
    }

    override suspend fun getByCalendarAsync(dateStr: String): Deferred<HabitCalendar?> =
        coroutineScope {
            async(Dispatchers.IO) {
                return@async calendarDao.getHabitsByDate(dateStr)
            }
        }

    override fun updateHabitsInCalendar(habitCalendar: HabitCalendar) {
        exeService.execute {
            calendarDao.updateHabitsInCalendar(habitCalendar)
        }
    }

    override suspend fun getFullCalendarAsync() : Deferred<List<HabitCalendar>?> =
        coroutineScope {
            async(Dispatchers.IO) {
                return@async calendarDao.getFullCalendar()
            }
        }

    override fun updateCompletedInCalendar(dateStr: String, completed: Int, callback: (Int) -> Unit) {
        exeService.execute {
            val completed1 = calendarDao.updateCompletionRate(dateStr, completed)
            mainHandler.post { callback(completed1) }
        }
    }

    fun changeHabitStatus(date: String, id: Int, status : Boolean) {
        exeService.execute {
            val habitCalendar = calendarDao.getHabitsByDate(date)
            val listHabits = habitCalendar?.habitsInADay
            val habitToUpdate = listHabits?.find { it.habitId == id }
            habitToUpdate?.isFinished = status
            calendarDao.updateHabitsInCalendar(habitCalendar!!)
        }
    }

    fun changeHabitGoalCount(count : Int, dateStr: String, id: Int) {
        exeService.execute {
            val thatDayCalendar = calendarDao.getHabitsByDate(dateStr)
            val thatAllHabits = thatDayCalendar?.habitsInADay
            val thatHabit = thatAllHabits?.find { it.habitId == id }
            if (thatHabit != null) {
                val thatHabitData = habitDao.getHabitById(id)
                if (count == -1) {
                    thatHabit.goalTimesCount = (thatHabitData?.repetitionGoalCount ?: 99)
                    thatHabit.isFinished = true
                } else if (count == 0){
                    thatHabit.goalTimesCount = 0
                    thatHabit.isFinished = false
                }else {
                    thatHabit.goalTimesCount = count
                    if (thatHabit.goalTimesCount >= (thatHabitData?.repetitionGoalCount ?: 99)) {
                        thatHabit.goalTimesCount = (thatHabitData?.repetitionGoalCount ?: 99)
                        thatHabit.isFinished = true
                    }
                }
                thatDayCalendar.habitsInADay = thatAllHabits
                calendarDao.updateHabitsInCalendar(thatDayCalendar)
            } else {
                Log.e("GoalCountFailure", "Habit with id $id not found")
            }
        }
    }

    fun changeHabitDurationCount(count: Int, dateStr: String, id: Int) {
        exeService.execute {
            val thatDayCalendar = calendarDao.getHabitsByDate(dateStr)
            val thatAllHabits = thatDayCalendar?.habitsInADay
            val thatHabit = thatAllHabits?.find { it.habitId == id }
            if (thatHabit != null) {
                val thatHabitData = habitDao.getHabitById(id)
                if (count == -1) {
                    thatHabit.goalDurationCount = (thatHabitData?.repetitionGoalDuration ?: 99)
                    thatHabit.isFinished = true
                } else if (count == 0){
                    thatHabit.goalDurationCount = 0
                    thatHabit.isFinished = false
                }else {
                    thatHabit.goalDurationCount = count
                    if (thatHabit.goalDurationCount >= (thatHabitData?.repetitionGoalDuration ?: 99)) {
                        thatHabit.goalDurationCount = (thatHabitData?.repetitionGoalDuration ?: 99)
                        thatHabit.isFinished = true
                    }
                }
                thatDayCalendar.habitsInADay = thatAllHabits
                calendarDao.updateHabitsInCalendar(thatDayCalendar)
            } else {
                Log.e("GoalCountFailure", "Habit with id $id not found")
            }
        }
    }

    fun updateHabitDayCount(count: Int, habitID: Int) {
        exeService.execute {
            val thatHabit = habitDao.getHabitById(habitID)
            if (thatHabit != null) {
                thatHabit.repetitionDaysCompleted =  count
                habitDao.updateHabit(thatHabit)
                Log.d("updateHabitCount", "changed")
            } else {
                Log.e("updateHabitCount", "Habit with id $habitID not found")
            }
        }
    }

    fun saveNotificationData(notificationData: NotificationData) {
        val typeT = object : TypeToken<NotificationData>() { }
        val dataInJson = gson.toJson(notificationData, typeT.type)
        val outputFile = File(storageDir, Constant.notificationDataFile)
        outputFile.writeText(dataInJson)
        Log.d("NotificationData","Written to $outputFile")
    }

    fun getNotificationData() : NotificationData? {
        val typeT = object : TypeToken<NotificationData>() { }
        return try {
            val inputFile = File(storageDir, Constant.notificationDataFile)
            val jsonData = inputFile.readText()
            gson.fromJson(jsonData, typeT.type)
        } catch (e: IOException) {
            null
        }
    }

    fun saveTimePeriodData(timePeriodData: TimePeriodData) {
        val typeT = object : TypeToken<TimePeriodData>() { }
        val dataInJson = gson.toJson(timePeriodData, typeT.type)
        val outputFile = File(storageDir, Constant.timePeriodDataFile)
        outputFile.writeText(dataInJson)
        Log.d("TimePeriodData","Written to $outputFile")
    }

    fun getTimePeriodData() : TimePeriodData? {
        val typeT = object : TypeToken<TimePeriodData>() { }
        return try {
            val inputFile = File(storageDir, Constant.timePeriodDataFile)
            val jsonData = inputFile.readText()
            gson.fromJson(jsonData, typeT.type)
        } catch (e: IOException) {
            return null
        }
    }

    suspend fun generateBackupDataAsync() : Deferred<String> =
        coroutineScope {
            async(Dispatchers.IO) {
                val allHabits = habitDao.getAllHabits()
                val gson = Gson()
                val typeT = object : TypeToken<List<Habit>>() { }
                val backStr = gson.toJson(allHabits, typeT.type)
                Log.d("habitJson", backStr)

                val calendarData: List<HabitCalendar>? = calendarDao.getFullCalendar()
                val typeT2 = object : TypeToken<List<HabitCalendar>>() { }
                val calendarStr = gson.toJson(calendarData, typeT2.type)
                Log.d("habitCalendar", calendarStr)

                val jsonObject = JSONObject()
                jsonObject.put(Constant.HABIT_BACKUP_JSON, backStr)
                jsonObject.put(Constant.CALENDAR_BACKUP_JSON, calendarStr)
                Log.d("Final Backup", jsonObject.toString())
                return@async jsonObject.toString()
            }
        }

    suspend fun restoreHabitsAsync(restoreJson : String) : Deferred<Boolean> =
        coroutineScope {
            async(Dispatchers.IO) {
                try {
                    val restoreObject = JSONObject(restoreJson)
                    val habitsJson = restoreObject.get(Constant.HABIT_BACKUP_JSON)
                    val calendarJson = restoreObject.get(Constant.CALENDAR_BACKUP_JSON)
                    val gson = Gson()
                    val typeT1 = object : TypeToken<List<Habit>>() { }
                    val typeT2 = object : TypeToken<List<HabitCalendar>>() { }
                    val allHabits = gson.fromJson<List<Habit>>(habitsJson.toString(), typeT1.type)
                    val allCalendar = gson.fromJson<List<HabitCalendar>>(calendarJson.toString(), typeT2.type)
                    Log.d("RestoreHabits", "${allHabits.size}")
                    Log.d("RestoreCalendar", "${allCalendar.size}")

                    habitDao.deleteAllHabits()
                    habitDao.insertAllHabits(allHabits)

                    calendarDao.deleteAllCalendar()
                    calendarDao.insertAllCalendar(allCalendar)
                    return@async true
                } catch (e: Exception) {
                    Log.e("RestoreError", "$e")
                    return@async false
                }
            }
        }

    private fun prepareSoundEffects() {
        notificationSounds.add(SoundEffect("Analog Tom", R.raw.analog_tom))
        notificationSounds.add(SoundEffect("Announcement", R.raw.announcement))
        notificationSounds.add(SoundEffect("Boeing", R.raw.boing))
        notificationSounds.add(SoundEffect("Ding Idea", R.raw.ding_idea))
        notificationSounds.add(SoundEffect("Fail fare", R.raw.failfare))
        notificationSounds.add(SoundEffect("Flourish", R.raw.flourish))
        notificationSounds.add(SoundEffect("Fragment", R.raw.fragment_retrievewav))
        notificationSounds.add(SoundEffect("Glad Piano", R.raw.glad_piano))
        notificationSounds.add(SoundEffect("Happy", R.raw.happy))
        notificationSounds.add(SoundEffect("Hip Hop", R.raw.hip_hop_beat))
        notificationSounds.add(SoundEffect("Knocking", R.raw.knocking))
        notificationSounds.add(SoundEffect("Microwave Timer", R.raw.microwave_timer))
        notificationSounds.add(SoundEffect("Pop", R.raw.pop))
        notificationSounds.add(SoundEffect("Positive", R.raw.positive))
        notificationSounds.add(SoundEffect("Punch", R.raw.punch))
        notificationSounds.add(SoundEffect("Rising", R.raw.rising))
        notificationSounds.add(SoundEffect("Short Choir", R.raw.short_choir))
        notificationSounds.add(SoundEffect("Soft Alert", R.raw.soft_alert))
        notificationSounds.add(SoundEffect("Success", R.raw.success))
        notificationSounds.add(SoundEffect("Tada Fanfare", R.raw.tada_fanfare))
        notificationSounds.add(SoundEffect("Trumpet", R.raw.trumpet))
        notificationSounds.add(SoundEffect("Simple Sound", R.raw.ukulele_simple_sound))
        notificationSounds.add(SoundEffect("Wood Destroy", R.raw.wood_crate_destory))
        notificationSounds.add(SoundEffect("Wrong Answer", R.raw.wrong_answer))
    }

    fun getNotificationSounds() : ArrayList<SoundEffect> {
        return if (notificationSounds.isEmpty()) {
            prepareSoundEffects()
            notificationSounds
        } else {
            notificationSounds
        }
    }

    fun getSoundResource(name: String) : Int {
        return when(name) {
            "Analog Tom" -> R.raw.analog_tom
            "Announcement" -> R.raw.announcement
            "Boeing" -> R.raw.boing
            "Ding Idea" -> R.raw.ding_idea
            "Fail fare"-> R.raw.failfare
            "Flourish" -> R.raw.flourish
            "Fragment" -> R.raw.fragment_retrievewav
            "Glad Piano" -> R.raw.glad_piano
            "Happy" -> R.raw.happy
            "Hip Hop" -> R.raw.hip_hop_beat
            "Knocking" -> R.raw.knocking
            "Microwave Timer" -> R.raw.microwave_timer
            "Pop" -> R.raw.pop
            "Positive" -> R.raw.positive
            "Punch" -> R.raw.punch
            "Rising" -> R.raw.rising
            "Short Choir" -> R.raw.short_choir
            "Soft Alert" -> R.raw.soft_alert
            "Success" -> R.raw.success
            "Tada Fanfare" -> R.raw.tada_fanfare
            "Trumpet" -> R.raw.trumpet
            "Simple Sound" -> R.raw.ukulele_simple_sound
            "Wood Destroy" -> R.raw.wood_crate_destory
            "Wrong Answer" -> R.raw.wrong_answer
            else -> R.raw.analog_tom
        }
    }

    suspend fun changeHabitTimerNotificationAsync(habitId: Int, notificationActive: Boolean, sound: String) =
        coroutineScope {
            async(Dispatchers.IO) {
                val habit = habitDao.getHabitById(habitId)
                if (habit != null) {
                    habit.timerNotificationActive = notificationActive
                    habit.timerNotificationSound = sound
                    habitDao.updateHabit(habit)
                }
            }
        }
}