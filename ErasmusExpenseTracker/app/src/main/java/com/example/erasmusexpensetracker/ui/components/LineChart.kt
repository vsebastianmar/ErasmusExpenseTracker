package com.example.erasmusexpensetracker.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.erasmusexpensetracker.data.local.entities.Expense
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun LineChartView(expenses: List<Expense>) {
    val context = LocalContext.current

    AndroidView(factory = {
        LineChart(context).apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            setVisibleXRangeMaximum(7f) // Show 7 days at a time
            moveViewToX(21f) // Start from the end
        }
    }, update = { lineChart ->

        val now = Calendar.getInstance()
        val last28Days = (0..27).map { offset ->
            Calendar.getInstance().apply {
                timeInMillis = now.timeInMillis
                add(Calendar.DAY_OF_YEAR, -offset)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }.reversed()

        val netByDay = last28Days.map { cal ->
            val startMillis = cal.timeInMillis
            cal.add(Calendar.DAY_OF_YEAR, 1)
            val endMillis = cal.timeInMillis - 1
            cal.add(Calendar.DAY_OF_YEAR, -1)

            val dailyIncome = expenses.filter {
                it.date in startMillis..endMillis && it.income
            }.sumOf { it.amount }

            val dailyExpense = expenses.filter {
                it.date in startMillis..endMillis && !it.income
            }.sumOf { it.amount }

            dailyIncome - dailyExpense
        }

        val cumulativeNet = netByDay.runningFold(0f) { acc, net -> acc + net.toFloat() }.drop(1)

        val entries = cumulativeNet.mapIndexed { index, net ->
            Entry(index.toFloat(), net)
        }

        val dataSet = LineDataSet(entries, "Cumulative Net Balance").apply {
            color = ColorTemplate.getHoloBlue()
            setCircleColor(ColorTemplate.getHoloBlue())
            lineWidth = 2f
            circleRadius = 3f
            setDrawValues(false)
            setDrawCircles(true)
            setDrawHighlightIndicators(true)
            setDrawFilled(true)
            fillAlpha = 50
        }

        val data = LineData(dataSet)
        lineChart.data = data

        val xAxis = lineChart.xAxis
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        val labels = last28Days.map {
            SimpleDateFormat("MM/dd", Locale.getDefault()).format(it.time)
        }
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        lineChart.axisRight.isEnabled = false
        lineChart.invalidate()
    }, modifier = Modifier
        .fillMaxWidth()
        .height(300.dp))
}