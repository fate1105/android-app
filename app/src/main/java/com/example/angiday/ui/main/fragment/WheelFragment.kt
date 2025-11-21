package com.example.angiday.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.angiday.R
import com.example.angiday.databinding.FragmentWheelBinding
import com.example.angiday.ui.wheel.SpinWheelActivity

class WheelFragment : Fragment() {

    private var _binding: FragmentWheelBinding? = null
    private val binding get() = _binding!!

    private fun color(id: Int) = ContextCompat.getColor(requireContext(), id)
    private val spinWheelLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {

                val foodId = result.data?.getLongExtra("foodId", -1) ?: -1
                if (foodId == -1L) return@registerForActivityResult

                // Mở FoodDetailFragment
                openFoodDetail(foodId)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWheelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.cardRandom.setOnClickListener {
            val intent = Intent(requireContext(), SpinWheelActivity::class.java)
            intent.putExtra("type", "random")
            spinWheelLauncher.launch(intent)
        }

        binding.cardFavorites.setOnClickListener {
            val intent = Intent(requireContext(), SpinWheelActivity::class.java)
            intent.putExtra("type", "favorite")
            spinWheelLauncher.launch(intent)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun openFoodDetail(foodId: Long) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, FoodDetailFragment.newInstance(foodId))
            .addToBackStack(null)
            .commit()
    }

}
