package com.example.angiday.ui.main.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.angiday.R
import com.example.angiday.ui.wheels.WheelBottomSheet
import com.example.angiday.ui.wheels.WheelItem
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar

class WheelFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_wheel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val cardFav = view.findViewById<MaterialCardView>(R.id.cardFavorites)
        val cardRandom = view.findViewById<MaterialCardView>(R.id.cardRandom)

        cardFav.setOnClickListener {
            showWheel(
                title = "Vòng quay — Món yêu thích",
                items = favoriteItems()
            )
        }

        cardRandom.setOnClickListener {
            showWheel(
                title = "Vòng quay — Ngẫu nhiên",
                items = randomItems()
            )
        }
    }

    private fun showWheel(title: String, items: List<WheelItem>) {
        val sheet = WheelBottomSheet(title, items) { picked ->
            view?.let {
                Snackbar.make(it, "Bạn nhận được: $picked", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorAccentGreen
                        )
                    )
                    .setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorOnAccentGreen
                        )
                    )
                    .show()
            }
        }
        sheet.show(parentFragmentManager, "wheel")
    }

    // TODO: thay bằng dữ liệu từ SQLite/Room của bạn (bảng món yêu thích)
    private fun favoriteItems(): List<WheelItem> = paletteWheel(
        listOf("Bún bò", "Cơm tấm", "Phở", "Bánh mì", "Hủ tiếu", "Bún chả")
    )

    // TODO: lấy từ toàn bộ menu/đề xuất của bạn
    private fun randomItems(): List<WheelItem> = paletteWheel(
        listOf("Mì Quảng", "Bún đậu", "Bánh xèo", "Lẩu thái", "Cơm gà", "Sushi", "Pizza", "Gà rán")
    )

    /**
     * Áp bảng màu bạn đưa để tạo lát cắt đẹp và tương phản chữ:
     * Ưu tiên: Primary, Secondary, AccentGreen, PrimaryVariant, SecondaryVariant, đỏ,...
     */
    private fun paletteWheel(labels: List<String>): List<WheelItem> {
        val ctx = requireContext()
        val colors = listOf(
            ContextCompat.getColor(ctx, R.color.colorPrimary),
            ContextCompat.getColor(ctx, R.color.colorSecondary),
            ContextCompat.getColor(ctx, R.color.colorAccentGreen),
            ContextCompat.getColor(ctx, R.color.colorPrimaryVariant),
            ContextCompat.getColor(ctx, R.color.colorSecondaryVariant),
            ContextCompat.getColor(ctx, R.color.red),
            Color.parseColor("#FFF8F1"), // surface nhạt
            ContextCompat.getColor(ctx, R.color.colorMuted)
        )
        return labels.mapIndexed { i, s -> WheelItem(s, colors[i % colors.size]) }
    }
}
