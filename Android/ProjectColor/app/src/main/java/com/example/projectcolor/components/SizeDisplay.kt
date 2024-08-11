package com.example.projectcolor.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SizeDisplay() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "16x16", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.weight(1f))
    }
}