package com.example.angiday.ui.wheel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.angiday.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WheelBottomSheet(
    private val title: String,
    private val items: List<WheelItem>,
    private val onPicked: (String) -> Unit
) : BottomSheetDialogFragment() {

    override fun getTheme() = R.style.CustomBottomSheetTheme

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.bottomsheet_wheel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val wheel = view.findViewById<SpinWheelView>(R.id.wheelView)
        val btnSpin = view.findViewById<Button>(R.id.btnSpin)
        val tvResult = view.findViewById<TextView>(R.id.tvResult)

        tvTitle.text = title
        wheel.items = items
        wheel.onResult = { _, label ->
            tvResult.text = "Kết quả: $label"
            onPicked(label)
        }

        btnSpin.setOnClickListener { wheel.spin() }
    }
}
