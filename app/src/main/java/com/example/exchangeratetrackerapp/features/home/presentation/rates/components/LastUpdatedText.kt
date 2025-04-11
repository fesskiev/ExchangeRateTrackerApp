package com.example.exchangeratetrackerapp.features.home.presentation.rates.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.exchangeratetrackerapp.R

@Composable
fun LastUpdatedText(lastUpdate: String) {
    Text(
        text = stringResource(R.string.last_updated, lastUpdate),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}