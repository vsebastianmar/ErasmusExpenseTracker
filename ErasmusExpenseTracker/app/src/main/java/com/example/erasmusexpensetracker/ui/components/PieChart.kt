package com.example.erasmusexpensetracker.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.erasmusexpensetracker.data.local.entities.Category
import com.example.erasmusexpensetracker.data.local.entities.Expense
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun PieChartView(
    expenses: List<Expense>,
    categories: List<Category>,
    label: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        factory = {
            PieChart(context).apply {
                this.description.isEnabled = false
                this.setUsePercentValues(true)
                this.setEntryLabelTextSize(12f)
                this.setHoleColor(android.graphics.Color.TRANSPARENT)
                this.setDrawEntryLabels(true)
            }
        },
        update = { pieChart ->
            val summedAmountsByCategoryId = expenses
                .groupBy { it.categoryId }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            val entries = summedAmountsByCategoryId.mapNotNull { (categoryId, totalAmount) ->
                categories.find { it.id == categoryId }?.name?.let { categoryName ->
                    PieEntry(totalAmount.toFloat(), categoryName)
                }
            }

            if (entries.isNotEmpty()) {
                val dataSet = PieDataSet(entries, "$label ")
                dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
                dataSet.valueTextSize = 14f
                dataSet.valueTextColor = android.graphics.Color.BLACK

                pieChart.data = PieData(dataSet)

                // animate on load
                pieChart.animateY(1000, Easing.EaseInOutQuad)
            } else {
                pieChart.clear()
            }

            pieChart.invalidate()
        },
        modifier = modifier
    )
}