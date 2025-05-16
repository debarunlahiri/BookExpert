package com.debarunlahiri.bookexpert.ui.screens.items

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.debarunlahiri.bookexpert.data.model.Item
import com.debarunlahiri.bookexpert.ui.components.ItemCard
import com.debarunlahiri.bookexpert.viewmodel.ItemViewModel

@Composable
fun ItemScreen() {
    val viewModel: ItemViewModel = viewModel(factory = ItemViewModel.Factory)
    val items by viewModel.items.collectAsState(initial = emptyList())

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            ItemCard(
                item = item,
                onUpdateItem = { viewModel.updateItem(it) }
            )
        }
    }
} 