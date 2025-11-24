package com.example.angiday.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.angiday.db.AppDatabase
import com.example.angiday.repository.UserBehaviorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class StreakData(
    val current: Int,
    val total: Int,
    val best: Int
)

class CookingHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: UserBehaviorRepository =
        UserBehaviorRepository(AppDatabase.get(application.applicationContext))

    private val _streak = MutableStateFlow(StreakData(0, 0, 0))
    val streak: StateFlow<StreakData> = _streak

    fun loadStreak(userId: Int) {
        viewModelScope.launch {

            // danh sách ngày dạng ["2025-11-20", "2025-11-19", ...]
            val days = repo.getCookedDays(userId)
                .filter { it.isNotBlank() }

            val total = days.size
            val current = calcCurrent(days)
            val best = calcBest(days)

            _streak.value = StreakData(
                current = current,
                total = total,
                best = best
            )
        }
    }





    private fun calcCurrent(days: List<String>): Int {
        if (days.isEmpty()) return 0

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val today = format.parse(format.format(Date()))!!
        val parsedDays = days.map { format.parse(it) }.sortedDescending()

        var streak = 0

        for (i in parsedDays.indices) {
            val diff = (today.time - parsedDays[i].time) / (1000 * 60 * 60 * 24)
            if (diff == i.toLong()) {
                streak++
            } else {
                break
            }
        }

        return streak
    }


    private fun calcBest(days: List<String>): Int {
        if (days.isEmpty()) return 0

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsedDays = days.map { format.parse(it) }.sorted()

        var best = 1
        var streak = 1

        for (i in 1 until parsedDays.size) {
            val diff = (parsedDays[i].time - parsedDays[i - 1].time) / (1000 * 60 * 60 * 24)

            if (diff == 1L) {
                streak++
                if (streak > best) best = streak
            } else {
                streak = 1
            }
        }

        return best
    }
}
