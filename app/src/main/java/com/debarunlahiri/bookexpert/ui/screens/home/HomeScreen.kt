package com.debarunlahiri.bookexpert.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.debarunlahiri.bookexpert.data.model.Item
import com.debarunlahiri.bookexpert.ui.navigation.Routes
import com.debarunlahiri.bookexpert.ui.components.ItemRow
import com.debarunlahiri.bookexpert.ui.components.NavigationButton
import com.debarunlahiri.bookexpert.viewmodel.ItemViewModel

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: ItemViewModel = viewModel(factory = ItemViewModel.Factory)
    val items by viewModel.items.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        NavigationButtons(navController)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No items available",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(items) { item ->
                    ItemRow(
                        item = item,
                        onDelete = { viewModel.deleteItem(it) },
                        onUpdate = { viewModel.updateItem(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationButtons(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NavigationButton(
            text = "View PDF",
            onClick = { navController.navigate(Routes.PDF_VIEWER) },
            modifier = Modifier.weight(1f)
        )
        
        NavigationButton(
            text = "Handle Images",
            onClick = { navController.navigate(Routes.IMAGE_HANDLER) },
            modifier = Modifier.weight(1f)
        )
        
        NavigationButton(
            text = "Settings",
            onClick = { navController.navigate(Routes.SETTINGS) },
            modifier = Modifier.weight(1f)
        )
    }
} 