package com.example.angiday.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.angiday.R
import com.example.angiday.databinding.FragmentExploreBinding
import com.example.angiday.ui.community.CommunityFragment
import com.example.angiday.ui.main.fragment.WheelFragment

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.cardWheel.setOnClickListener {
            openFragment(WheelFragment())
        }

        binding.cardShare.setOnClickListener {
            openFragment(CommunityFragment())
        }

        binding.cardRanking.setOnClickListener {
//            openFragment(RankingFragment())
        }
    }

    private fun openFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
