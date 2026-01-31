package com.example.erasmusexpensetracker.ui.screens.categories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.erasmusexpensetracker.data.local.entities.Category
import com.example.erasmusexpensetracker.viewmodel.ExpenseViewModel

@Composable
fun AddCategoryScreen(viewModel: ExpenseViewModel, navController: NavController) {
    AddCategoryForm(onAdd = { category ->
        viewModel.addCategory(category)
        navController.popBackStack()
    })
}

@Composable
fun AddCategoryForm(
    initialCategory: Category? = null,
    onAdd: (Category) -> Unit
) {
    var name by remember { mutableStateOf(initialCategory?.name ?: "") }

    val nameError = name.isBlank()
    val formValid = !nameError

    Column(Modifier.padding(16.dp)) {
        Text(
            text  = if (initialCategory != null) "Edit Category" else "Add Category",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value     = name,
            onValueChange = { name = it },
            label     = { Text("Category Name") },
            isError   = nameError,
            modifier  = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        Button(
            onClick  = {
                onAdd(Category(id = initialCategory?.id ?: 0, name = name))
            },
            enabled = formValid,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}
