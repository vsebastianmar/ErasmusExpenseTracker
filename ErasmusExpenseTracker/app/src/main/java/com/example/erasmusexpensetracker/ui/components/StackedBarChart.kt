package com.example.erasmusexpensetracker.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.erasmusexpensetracker.data.local.entities.Expense
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun StackedBarChartView(expenses: List<Expense>, selectedYear: Int) {
    val context = LocalContext.current
    val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())

    if (expenses.isEmpty()) {
        return
    }

    key(expenses) {
        AndroidView(factory = {
            BarChart(context).apply {
                val calendar = Calendar.getInstance()
                val monthlyValues = Array(12) { FloatArray(2) } // [income, expense]

                expenses.forEach { expense ->
                    calendar.timeInMillis = expense.date
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)


                    if (year == selectedYear) {
                        if (expense.income) {
                            monthlyValues[month][0] += expense.amount.toFloat()
                        } else {
                            monthlyValues[month][1] += expense.amount.toFloat()
                        }
                    }
                }

                val entries = monthlyValues.mapIndexed { index, values ->
                    BarEntry(index.toFloat(), values)
                }

                val dataSet = BarDataSet(entries, "Income & Expenses").apply {
                    setColors(android.graphics.Color.GREEN, android.graphics.Color.RED)
                    stackLabels = arrayOf("Income", "Expenses")
                    valueTextSize = 12f
                }

                val barData = BarData(dataSet)
                barData.barWidth = 0.9f

                this.data = barData
                this.setFitBars(true)
                this.description.isEnabled = false

                val xAxis = this.xAxis
                xAxis.valueFormatter = IndexAxisValueFormatter(
                    (0..11).map {
                        monthFormat.format(Calendar.getInstance().apply { set(Calendar.MONTH, it) }.time)
                    }
                )
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.setDrawGridLines(false)
                xAxis.labelCount = 12

                this.axisRight.isEnabled = false
                this.invalidate()
            }
        }, modifier = Modifier
            .fillMaxWidth()
            .height(320.dp))
    }
}

@Composable
fun BarChartWithYearSelector(expenses: List<Expense>) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = remember(expenses) {
        expenses.map {
            Calendar.getInstance().apply { timeInMillis = it.date }.get(Calendar.YEAR)
        }.distinct().sortedDescending()
    }

    var selectedYear by remember { mutableIntStateOf(currentYear.coerceAtLeast(years.firstOrNull() ?: currentYear)) }

    Column {
        // Dropdown for year selection
        Row(modifier = Modifier.padding(16.dp)) {
            Text("Select Year: ", modifier = Modifier.alignByBaseline())
            var expanded by remember { mutableStateOf(false) }
            Box {
                TextButton(onClick = { expanded = true }) {
                    Text(selectedYear.toString())
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    years.forEach { year ->
                        DropdownMenuItem(
                            text = { Text(year.toString()) },
                            onClick = {
                                selectedYear = year
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        // Chart
        StackedBarChartView(expenses = expenses, selectedYear = selectedYear)
    }
}

