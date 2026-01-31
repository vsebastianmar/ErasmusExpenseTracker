package com.example.erasmusexpensetracker.ui.screens.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.erasmusexpensetracker.ui.navigation.Screen
import com.example.erasmusexpensetracker.viewmodel.ExpenseViewModel

@Composable
fun CategoryListScreen(viewModel: ExpenseViewModel, navController: NavController) {
    CategoryList(viewModel = viewModel, navController = navController)
}

// categories list
@Composable
fun CategoryList(viewModel: ExpenseViewModel, navController: NavController) {
    val categories by viewModel.categories.collectAsState(emptyList())

    LazyColumn{
        items(categories, key = { it.id }) { category ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = category.name)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = {
                            navController.navigate(Screen.EditCategory.passId(category.id))

                        }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Category"
                            )
                        }
                        IconButton(onClick = {
                            viewModel.deleteCategory(category)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete Category"
                            )
                        }
                    }
                }
            }
        }

    }
}