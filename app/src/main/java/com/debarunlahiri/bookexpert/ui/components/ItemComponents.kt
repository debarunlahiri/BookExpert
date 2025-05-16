package com.debarunlahiri.bookexpert.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.debarunlahiri.bookexpert.HomeUpdateItemDialog
import com.debarunlahiri.bookexpert.data.model.Item

/**
 * A card component to display an item in the ItemScreen
 */
@Composable
fun ItemCard(item: Item, onUpdateItem: (Item) -> Unit) {
    var showUpdateDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category indicator
                val itemType = determineItemType(item)
                val (indicatorColor, categoryName) = getCategoryInfo(itemType)
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(indicatorColor)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Edit button
                IconButton(
                    onClick = { showUpdateDialog = true },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Item",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Item name
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )
            
            // Item ID in a subtle way
            Text(
                text = "ID: ${item.id}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp)
            )
            
            // Show key property summaries even in collapsed state
            val keyProperties = getKeyProperties(item)
            if (keyProperties.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    keyProperties.forEach { (key, value) ->
                        val chipColor = when {
                            key.equals("price", ignoreCase = true) -> 
                                MaterialTheme.colorScheme.primaryContainer
                            key.equals("color", ignoreCase = true) ->
                                MaterialTheme.colorScheme.secondaryContainer  
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        
                        val textColor = when {
                            key.equals("price", ignoreCase = true) -> 
                                MaterialTheme.colorScheme.onPrimaryContainer
                            key.equals("color", ignoreCase = true) ->
                                MaterialTheme.colorScheme.onSecondaryContainer
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        
                        Surface(
                            color = chipColor,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = formatValue(key, value),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = textColor
                                )
                            }
                        }
                    }
                }
            }
            
            // Expandable detail section
            if (expanded && item.data != null && item.data.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "All Properties",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        item.data.entries.forEachIndexed { index, entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = entry.key,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                Text(
                                    text = formatValue(entry.key, entry.value),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            
                            if (index < item.data.size - 1) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }
            
            // Expand/collapse hint
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
    
    if (showUpdateDialog) {
        UpdateItemDialog(
            item = item,
            onDismiss = { showUpdateDialog = false },
            onUpdate = { updatedItem ->
                onUpdateItem(updatedItem)
                showUpdateDialog = false
            }
        )
    }
}

/**
 * A row component to display an item in the HomeScreen
 */
@Composable
fun ItemRow(item: Item, onDelete: (Item) -> Unit, onUpdate: (Item) -> Unit) {
    var showUpdateDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with product type indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category indicator
                val itemType = determineItemType(item)
                val (indicatorColor, categoryName) = getCategoryInfo(itemType)
                
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(indicatorColor)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Action buttons with better styling
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showUpdateDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Update", style = MaterialTheme.typography.labelMedium)
                    }
                    
                    Button(
                        onClick = { onDelete(item) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Delete", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Item name
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )
            
            // Item ID in a subtle way
            Text(
                text = "ID: ${item.id}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )

            // Key properties as chips
            val keyProperties = getKeyProperties(item)
            if (keyProperties.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    keyProperties.forEach { (key, value) ->
                        val chipColor = when {
                            key.equals("price", ignoreCase = true) -> 
                                MaterialTheme.colorScheme.primaryContainer
                            key.equals("color", ignoreCase = true) ->
                                MaterialTheme.colorScheme.secondaryContainer  
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        
                        val textColor = when {
                            key.equals("price", ignoreCase = true) -> 
                                MaterialTheme.colorScheme.onPrimaryContainer
                            key.equals("color", ignoreCase = true) ->
                                MaterialTheme.colorScheme.onSecondaryContainer
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        
                        Surface(
                            color = chipColor,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = formatValue(key, value),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = textColor
                                )
                            }
                        }
                    }
                }
            }
            
            // Expandable details section
            if (expanded && item.data != null && item.data.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "All Properties",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        item.data.entries.forEachIndexed { index, entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = entry.key,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                Text(
                                    text = formatValue(entry.key, entry.value),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            
                            if (index < item.data.size - 1) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }
            
            // Expand/collapse hint
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
    
    if (showUpdateDialog) {
        HomeUpdateItemDialog(
            item = item,
            onDismiss = { showUpdateDialog = false },
            onUpdate = { updatedItem ->
                onUpdate(updatedItem)
                showUpdateDialog = false
            }
        )
    }
}

/**
 * A dialog component to update an item
 */
@Composable
fun UpdateItemDialog(
    item: Item,
    onDismiss: () -> Unit,
    onUpdate: (Item) -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    
    // Create a mutable map to hold all the data fields and their values
    val dataFields = remember { 
        mutableStateMapOf<String, String>().apply {
            // Initialize with existing data
            item.data?.forEach { (key, value) ->
                this[key] = value.toString()
            }
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Update Item",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Item name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Dynamic fields based on the item's data
                if (item.data != null && item.data.isNotEmpty()) {
                    Text(
                        text = "Properties",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Create a field for each data property
                    item.data.keys.forEach { key ->
                        val value = dataFields[key] ?: ""
                        val keyboardType = when {
                            // Determine appropriate keyboard type based on the original value type or key name
                            item.data[key] is Number || 
                            key.equals("price", ignoreCase = true) ||
                            key.contains("size", ignoreCase = true) ||
                            key.contains("capacity", ignoreCase = true) -> KeyboardType.Decimal
                            else -> KeyboardType.Text
                        }
                        
                        OutlinedTextField(
                            value = value,
                            onValueChange = { dataFields[key] = it },
                            label = { Text(key) },
                            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            val updatedData = mutableMapOf<String, Any>()
                            
                            // Convert values back to appropriate types
                            dataFields.forEach { (key, stringValue) ->
                                val originalValue = item.data?.get(key)
                                
                                // Try to maintain the original type if possible
                                val convertedValue: Any = when (originalValue) {
                                    is Int -> stringValue.toIntOrNull() ?: stringValue
                                    is Long -> stringValue.toLongOrNull() ?: stringValue
                                    is Double -> stringValue.toDoubleOrNull() ?: stringValue
                                    is Float -> stringValue.toFloatOrNull() ?: stringValue
                                    is Boolean -> stringValue.toBoolean()
                                    else -> stringValue
                                }
                                
                                updatedData[key] = convertedValue
                            }
                            
                            val updatedItem = Item(
                                id = item.id,
                                name = name,
                                data = updatedData
                            )
                            
                            onUpdate(updatedItem)
                        }
                    ) {
                        Text("Update")
                    }
                }
            }
        }
    }
}

/**
 * A reusable navigation button component
 */
@Composable
fun NavigationButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
        ),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
        modifier = modifier.height(48.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Helper function to get 2-3 key properties to show in collapsed view
private fun getKeyProperties(item: Item): Map<String, Any> {
    if (item.data.isNullOrEmpty()) return emptyMap()
    
    val result = mutableMapOf<String, Any>()
    
    // First try to get price if it exists
    item.data["price"]?.let { 
        result["price"] = it 
    }
    
    // Then try to get color if it exists
    item.data["color"]?.let { 
        result["color"] = it 
    }
    
    // If we don't have enough properties yet, add capacity or any other notable ones
    if (result.size < 2) {
        item.data.entries.find { 
            it.key.contains("capacity", ignoreCase = true) || 
            it.key.contains("size", ignoreCase = true) 
        }?.let {
            result[it.key] = it.value
        }
    }
    
    // If we still need more, add any property
    if (result.isEmpty() && item.data.isNotEmpty()) {
        val entry = item.data.entries.firstOrNull()
        entry?.let { result[it.key] = it.value }
    }
    
    return result
}

// Helper function to determine item type
private fun determineItemType(item: Item): String {
    val name = item.name.lowercase()
    return when {
        name.contains("iphone") -> "Apple iPhone"
        name.contains("macbook") -> "Apple Mac"
        name.contains("ipad") -> "Apple iPad"
        name.contains("watch") -> "Apple Watch"
        name.contains("airpods") -> "Apple Audio"
        name.contains("pixel") -> "Google"
        name.contains("samsung") -> "Samsung" 
        name.contains("galaxy") -> "Samsung"
        name.contains("beats") -> "Audio"
        else -> "Other"
    }
}

// Helper function to get category indicator color and name
private fun getCategoryInfo(itemType: String): Pair<Color, String> {
    return when (itemType) {
        "Apple iPhone" -> Color(0xFF007AFF) to "iPhone"
        "Apple Mac" -> Color(0xFF5AC8FA) to "Mac"
        "Apple iPad" -> Color(0xFF34C759) to "iPad"
        "Apple Watch" -> Color(0xFFFF2D55) to "Watch"  
        "Apple Audio" -> Color(0xFF5856D6) to "Audio"
        "Google" -> Color(0xFF4285F4) to "Google"
        "Samsung" -> Color(0xFF1428A0) to "Samsung"
        "Audio" -> Color(0xFFFF3B30) to "Audio"
        else -> Color(0xFF8E8E93) to "Other"
    }
}

// Helper function to format values based on key and type
fun formatValue(key: String, value: Any?): String {
    if (value == null) return "N/A"
    
    return when {
        // Format price values
        key.equals("price", ignoreCase = true) && value is Number -> {
            "$${String.format("%.2f", value.toDouble())}"
        }
        // Format capacity values
        key.contains("capacity", ignoreCase = true) || 
        key.contains("size", ignoreCase = true) && value is Number -> {
            "${value} GB"
        }
        // Format other values
        else -> value.toString()
    }
} 