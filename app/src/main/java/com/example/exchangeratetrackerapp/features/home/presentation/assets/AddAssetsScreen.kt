package com.example.exchangeratetrackerapp.features.home.presentation.assets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.exchangeratetrackerapp.core.common.getCurrencySymbol
import com.example.exchangeratetrackerapp.core.ui.components.AssetIcon
import com.example.exchangeratetrackerapp.core.ui.components.ExchangeRateAppBar
import com.example.exchangeratetrackerapp.features.home.presentation.assets.model.AssetUi
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssetsScreen(
    onBackClick: () -> Unit,
    viewModel: AddAssetsViewModel = koinViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column {
                ExchangeRateAppBar(
                    title = "Add Asset",
                    showBackButton = true,
                    onBackClick = onBackClick,
                    actions = {
                        TextButton(onClick = { }) {
                            Text(
                                text = "Done",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
                AssetSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }

        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SectionHeader(title = "POPULAR ASSETS")

            val popularAssets = listOf(
                AssetUi("usd", "US Dollar", "USD", true),
                AssetUi("eur", "Euro", "EUR", false),
                AssetUi("gbp", "British Pound", "GBP", false),
                AssetUi("jpy", "Japanese Yen", "JPY", false)
            )

            Column {
                popularAssets.forEach { asset ->
                    AvailableAssetItem(
                        asset = asset,
                        isSelected = asset.isSelected,
                        onToggleSelection = { }
                    )
                }
            }

            SectionHeader(title = "CRYPTOCURRENCIES")

            val cryptoAssets = listOf(
                AssetUi("btc", "Bitcoin", "BTC", true),
                AssetUi("eth", "Ethereum", "ETH", false)
            )

            Column {
                cryptoAssets.forEach { asset ->
                    AvailableAssetItem(
                        asset = asset,
                        isSelected = asset.isSelected,
                        onToggleSelection = { }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp)),
        placeholder = { Text("Search assets") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun AvailableAssetItem(
    asset: AssetUi,
    isSelected: Boolean,
    onToggleSelection: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AssetIcon(
                symbol = asset.code.getCurrencySymbol(),
                modifier = Modifier.padding(end = 16.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = asset.symbol.uppercase(),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = asset.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            RadioButton(
                selected = isSelected,
                onClick = onToggleSelection
            )
        }
    }
}