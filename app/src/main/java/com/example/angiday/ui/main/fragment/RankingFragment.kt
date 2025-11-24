package com.example.angiday.ui.ranking

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.angiday.databinding.FragmentRankingBinding
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.session.SessionManager
import com.example.angiday.model.entity.UserBehaviorEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


data class RankingItem(
    val userId: Int,
    val userName: String,
    val bestStreak: Int
)

object StreakCalculator {

    private val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun calcStreak(data: List<UserBehaviorEntity>): Map<Int, Int> {

        val userMap = mutableMapOf<Int, MutableList<Date>>()

        for (item in data) {
            val date = parseDate(item.timestamp)
            if (date != null) {
                userMap.getOrPut(item.userId.toInt()) { mutableListOf() }.add(date)
            }
        }

        val result = mutableMapOf<Int, Int>()

        for ((userId, dates) in userMap) {
            dates.sort()

            var best = 1
            var current = 1

            for (i in 1 until dates.size) {
                val diff = dayDiff(dates[i - 1], dates[i])

                if (diff == 1L) current++
                else if (diff > 1L) current = 1

                best = maxOf(best, current)
            }

            result[userId] = best
        }

        return result
    }

    private fun parseDate(s: String?): Date? {
        if (s.isNullOrEmpty()) return null

        return try {
            if (s.length > 10)
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(s)
            else
                df.parse(s)
        } catch (e: Exception) { null }
    }

    private fun dayDiff(d1: Date, d2: Date): Long {
        val dd1 = df.parse(df.format(d1))!!.time
        val dd2 = df.parse(df.format(d2))!!.time
        return (dd2 - dd1) / (1000 * 60 * 60 * 24)
    }
}


class RankingViewModel(private val db: AppDatabase) : ViewModel() {

    val ranking = MutableStateFlow<List<RankingItem>>(emptyList())

    fun loadRanking() {
        kotlinx.coroutines.GlobalScope.launch {

            val cooked = db.userBehaviorDao().getAllCooked()

            val streakMap = StreakCalculator.calcStreak(cooked)

            val userDao = db.userDao()

            val list = streakMap.map { (userId, best) ->
                val user = userDao.getById(userId.toLong())
                RankingItem(
                    userId,
                    user?.name ?: "Người dùng $userId",
                    best
                )
            }
                .sortedByDescending { it.bestStreak }
                .take(10)

            ranking.value = list
        }
    }
}

class RankingVMFactory(private val db: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RankingViewModel(db) as T
    }
}

class RankingAdapter : RecyclerView.Adapter<RankingAdapter.VH>() {

    private val items = ArrayList<RankingItem>()

    fun submitList(list: List<RankingItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val rank: TextView = view.findViewById(R.id.tvRank)
        val name: TextView = view.findViewById(R.id.tvName)
        val streak: TextView = view.findViewById(R.id.tvScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ranking, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.rank.text = "#${position + 1}"
        holder.name.text = item.userName
        holder.streak.text = "${item.bestStreak} ngày"
    }

    override fun getItemCount() = items.size
}


class RankingFragment : Fragment() {

    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!

    private val db by lazy { AppDatabase.get(requireContext()) }
    private val vm: RankingViewModel by lazy {
        ViewModelProvider(this, RankingVMFactory(db))[RankingViewModel::class.java]
    }

    private val adapter = RankingAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val session = SessionManager(requireContext())
        val currentUserId = session.getUserId().toInt()

        binding.rvRanking.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRanking.adapter = adapter

        lifecycleScope.launch {
            vm.ranking.collect { list ->

                adapter.submitList(list)

                if (list.size >= 3) {
                    val top1 = list[0]
                    val top2 = list[1]
                    val top3 = list[2]

                    binding.top1.findViewById<TextView>(R.id.tvName).text = top1.userName
                    binding.top1.findViewById<TextView>(R.id.tvScore).text = "${top1.bestStreak} ngày"

                    binding.top2.findViewById<TextView>(R.id.tvName).text = top2.userName
                    binding.top2.findViewById<TextView>(R.id.tvScore).text = "${top2.bestStreak} ngày"

                    binding.top3.findViewById<TextView>(R.id.tvName).text = top3.userName
                    binding.top3.findViewById<TextView>(R.id.tvScore).text = "${top3.bestStreak} ngày"
                }

                // ===== CURRENT USER =====
                val index = list.indexOfFirst { it.userId == currentUserId }

                if (index != -1) {
                    val current = list[index]

                    binding.currentUser.findViewById<TextView>(R.id.tvCurrentName).text =
                        "Bạn: ${current.userName}"

                    binding.currentUser.findViewById<TextView>(R.id.tvCurrentStreak).text =
                        "Chuỗi nấu ăn: ${current.bestStreak} ngày"

                    binding.currentUser.findViewById<TextView>(R.id.tvCurrentRank).text =
                        "Xếp hạng: #${index + 1}"
                } else {
                    binding.currentUser.findViewById<TextView>(R.id.tvCurrentName).text =
                        "Bạn: ${session.getUserName() ?: "User"}"

                    binding.currentUser.findViewById<TextView>(R.id.tvCurrentStreak).text =
                        "Chuỗi nấu ăn: 0 ngày"

                    binding.currentUser.findViewById<TextView>(R.id.tvCurrentRank).text =
                        "Chưa có xếp hạng"
                }
            }
        }

        vm.loadRanking()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
