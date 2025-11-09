package com.example.angiday.ui.community

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.model.entity.FoodEntity
import com.example.angiday.model.entity.UserBehaviorEntity
import com.example.angiday.session.SessionManager
import com.example.angiday.ui.main.fragment.FoodDetailFragment
import kotlinx.coroutines.launch
import java.io.File

class CommunityFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_community, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.rvCommunity)
        rv.layoutManager = LinearLayoutManager(requireContext())

        // 🔹 Load danh sách bài chia sẻ + tên người đăng
        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.get(requireContext())
            val behaviors = db.userBehaviorDao().getAllSharedBehavior() // behaviorType = 'shared'
            val posts = mutableListOf<Pair<FoodEntity, String>>()

            for (b in behaviors) {
                val food = db.foodDao().getFoodById(b.foodId.toLong())
                val user = db.userDao().getUserById(b.userId)
                if (food != null && user != null) {
                    posts.add(food to user.name)
                }
            }

            rv.adapter = CommunityAdapter(posts)
        }

        return view
    }

    // 🧩 Adapter nằm TRONG Fragment này
    inner class CommunityAdapter(private val posts: List<Pair<FoodEntity, String>>) :
        RecyclerView.Adapter<CommunityAdapter.PostViewHolder>() {

        inner class PostViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val img: ImageView = v.findViewById(R.id.imgFood)
            val title: TextView = v.findViewById(R.id.tvTitle)
            val desc: TextView = v.findViewById(R.id.tvDesc)
            val tvUser: TextView = v.findViewById(R.id.tvUserName)
            val btnFavorite: ImageButton = v.findViewById(R.id.btnFavorite)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
            val v = layoutInflater.inflate(R.layout.item_community_post, parent, false)
            return PostViewHolder(v)
        }

        override fun getItemCount() = posts.size

        override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
            val (food, userName) = posts[position]

            holder.title.text = food.title
            holder.desc.text = food.desc ?: "Không có mô tả"
            holder.tvUser.text = userName // ✅ tên người đăng

            // Load ảnh
            val path = food.imageRes
            if (!path.isNullOrEmpty()) {
                val file = File(path)
                if (file.exists()) holder.img.setImageURI(Uri.fromFile(file))
                else {
                    val resId = resources.getIdentifier(path, "drawable", requireContext().packageName)
                    holder.img.setImageResource(if (resId != 0) resId else R.drawable.ic_food_placeholder)
                }
            } else {
                holder.img.setImageResource(R.drawable.ic_food_placeholder)
            }

            // ❤️ Nút yêu thích
            var isFavorite = false
            holder.btnFavorite.setOnClickListener {
                val session = SessionManager(requireContext())
                val userId = session.getUserId()
                if (userId == -1L) {
                    Toast.makeText(requireContext(), "Vui lòng đăng nhập trước!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val dao = AppDatabase.get(requireContext()).userBehaviorDao()
                isFavorite = !isFavorite
                viewLifecycleOwner.lifecycleScope.launch {
                    if (isFavorite) {
                        holder.btnFavorite.setImageResource(R.drawable.ic_favorite_filled)
                        holder.btnFavorite.setColorFilter(Color.RED)
                        dao.insert(
                            UserBehaviorEntity(
                                userId = userId,
                                foodId = food.id,
                                behaviorType = "favorite"
                            )
                        )
                    } else {
                        holder.btnFavorite.setImageResource(R.drawable.ic_favorite_border)
                        holder.btnFavorite.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                        )
                        dao.deleteBehavior(userId = userId, foodId = food.id, type = "favorite")
                    }
                }
            }

            // 👆 Click mở chi tiết món ăn
            holder.itemView.setOnClickListener { openFoodDetail(food.id) }
        }
    }

    // Hàm mở chi tiết món ăn
    private fun openFoodDetail(foodId: Long) {
        val fragment = FoodDetailFragment.newInstance(foodId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
