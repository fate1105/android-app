package com.example.angiday.ui.main.fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.angiday.R

class ProfileFragment : Fragment() {

    private lateinit var imgAvatar: ImageView
    private lateinit var btnEditProfile: Button
    private lateinit var btnFavorites: Button
    private lateinit var btnHistory: Button
    private lateinit var btnLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgAvatar = view.findViewById(R.id.imgAvatar)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnFavorites = view.findViewById(R.id.btnFavorites)
        btnHistory = view.findViewById(R.id.btnHistory)
        btnLogout = view.findViewById(R.id.btnLogout)

        // --- Click events ---
        btnEditProfile.setOnClickListener { Log.d("ProfileFragment", "EditProfile Click") }
        btnFavorites.setOnClickListener { Log.d("ProfileFragment", "Favorites Click") }
        btnHistory.setOnClickListener { Log.d("ProfileFragment", "History Click") }
        btnLogout.setOnClickListener { Log.d("ProfileFragment", "Logout Click") }

        // --- LongClick events ---
        btnEditProfile.setOnLongClickListener {
            Log.d("ProfileFragment", "EditProfile LongClick")
            true
        }
        btnFavorites.setOnLongClickListener {
            Log.d("ProfileFragment", "Favorites LongClick")
            true
        }

        // --- Touch events (ví dụ cho avatar) ---
        imgAvatar.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> Log.d("ProfileFragment", "Avatar Touch Down")
                MotionEvent.ACTION_UP -> Log.d("ProfileFragment", "Avatar Touch Up")
                MotionEvent.ACTION_MOVE -> Log.d("ProfileFragment", "Avatar Touch Move")
            }
            false // false để không chặn click sau touch
        }

        // --- FocusChange events ---
        btnLogout.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Log.d("ProfileFragment", "Logout Button Focused")
            } else {
                Log.d("ProfileFragment", "Logout Button Lost Focus")
            }
        }
    }
}
