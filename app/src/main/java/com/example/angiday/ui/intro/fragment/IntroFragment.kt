package com.example.angiday.ui.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.angiday.R

class IntroFragment : Fragment() {

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_SUBTITLE = "arg_subtitle"
        private const val ARG_IMAGE = "arg_image"

        fun newInstance(title: String, subtitle: String, imageRes: Int) = IntroFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_SUBTITLE, subtitle)
                putInt(ARG_IMAGE, imageRes)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_intro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val title = requireArguments().getString(ARG_TITLE).orEmpty()
        val subtitle = requireArguments().getString(ARG_SUBTITLE).orEmpty()
        val imageRes = requireArguments().getInt(ARG_IMAGE)

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvSubtitle = view.findViewById<TextView>(R.id.tvSubtitle)
        val imgIntro = view.findViewById<ImageView>(R.id.imgIntro)

        tvTitle.text = title
        tvSubtitle.text = subtitle
        imgIntro.setImageResource(imageRes)

    }
}
