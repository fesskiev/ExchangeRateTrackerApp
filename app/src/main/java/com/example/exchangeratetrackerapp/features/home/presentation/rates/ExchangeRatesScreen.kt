package com.example.exchangeratetrackerapp.features.home.presentation.rates

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.exchangeratetrackerapp.core.common.getCurrencyName
import com.example.exchangeratetrackerapp.core.common.getCurrencySymbol
import com.example.exchangeratetrackerapp.core.ui.components.AssetIcon
import com.example.exchangeratetrackerapp.core.ui.components.ExchangeRateAppBar
import com.example.exchangeratetrackerapp.core.ui.theme.NegativeRed
import com.example.exchangeratetrackerapp.core.ui.theme.PositiveGreen
import com.example.exchangeratetrackerapp.features.home.presentation.rates.model.ExchangeRateUi
import org.koin.compose.viewmodel.koinViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeRatesScreen(
    onAddAssetClick: () -> Unit,
    viewModel: ExchangeRatesViewModel = koinViewModel()
) {
    LaunchedEffect(viewModel) {

    }
    val state by viewModel.state.collectAsState()
    ExchangeRatesContent(state = state, onEvent = { })
}

@Composable
fun ExchangeRatesContent(
    state: ExchangeRatesState,
    onEvent: (ExchangeRatesEvent) -> Unit
) {
    Scaffold(
        topBar = {
            ExchangeRateAppBar(
                title = "Exchange Rates",
                actions = {
                    IconButton(onClick = { onEvent(ExchangeRatesEvent.AddAssets) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Asset"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp),
                        strokeWidth = 4.dp
                    )
                }
                state.error != null -> {
                    ErrorView(
                        message = state.error,
                        onRetry = { onEvent(ExchangeRatesEvent.Retry) }
                    )
                }
                state.rates.isEmpty() -> EmptyAssets()
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp)
                    ) {
                        LastUpdatedText(timestamp = "Just now")
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(
                                items = state.rates,
                                key = { rate -> rate.code }
                            ) { rate ->
                                SwipeableAssetRateItem(
                                    exchangeRate = rate,
                                    onRemoveClick = { onEvent(ExchangeRatesEvent.RemoveAsset) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Error: $message")
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}


@Composable
fun EmptyAssets(message: String = "No assets added yet") {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

enum class DragAnchors {
    Start,
    END
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeableAssetRateItem(
    exchangeRate: ExchangeRateUi,
    onRemoveClick: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }

    val animatedRate by animateFloatAsState(
        targetValue = exchangeRate.rate.toFloat(),
        label = "rate"
    )

    var cardWidth by remember { mutableIntStateOf(0) }
    val deleteButtonWidth = 80.dp
    val deleteButtonWidthPx = with(LocalDensity.current) { deleteButtonWidth.toPx() }

    val decayAnimationSpec = splineBasedDecay<Float>(LocalDensity.current)

    val state = remember {
        AnchoredDraggableState(
            initialValue = DragAnchors.Start,
            positionalThreshold = { distance: Float -> distance * 0.3f },
            velocityThreshold = { 100f },
            snapAnimationSpec = spring<Float>(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            decayAnimationSpec = decayAnimationSpec,
            confirmValueChange = { true }
        )
    }

    val anchors = remember(deleteButtonWidthPx) {
        DraggableAnchors {
            DragAnchors.Start at 0f
            DragAnchors.END at -deleteButtonWidthPx
        }
    }

    SideEffect {
        if (deleteButtonWidthPx > 0) {
            state.updateAnchors(anchors)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(Color(0xFF3C4045)),
            contentAlignment = Alignment.CenterEnd
        ) {
            IconButton(
                onClick = onRemoveClick,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = Color.White
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { cardWidth = it.width }
                .offset { IntOffset(state.offset.roundToInt(), 0) }
                .anchoredDraggable(
                    state = state,
                    orientation = Orientation.Horizontal,
                    enabled = true
                ),
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp,
                draggedElevation = 0.dp,
                disabledElevation = 0.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssetIcon(
                    symbol = exchangeRate.code.getCurrencySymbol()
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text = exchangeRate.code.uppercase(),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = exchangeRate.code.getCurrencyName(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$" + currencyFormat.format(animatedRate).drop(1),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    val percentChange = exchangeRate.percentChange
                    val changeColor = if (percentChange >= 0) PositiveGreen else NegativeRed
                    val changePrefix = if (percentChange >= 0) "+" else ""

                    Text(
                        text = "$changePrefix${String.format("%.2f", percentChange)}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = changeColor
                    )
                }
            }
        }
    }
}

@Composable
fun LastUpdatedText(timestamp: String) {
    Text(
        text = "Last updated: $timestamp",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun ExchangeRatesPreview() {
    val sampleRates = listOf(
        ExchangeRateUi("usd", 1.0000, System.currentTimeMillis(), 0.02),
        ExchangeRateUi("eur", 0.8934, System.currentTimeMillis(), -0.14),
        ExchangeRateUi("btc", 42356.89, System.currentTimeMillis(), 1.23)
    )
    MaterialTheme {
        ExchangeRatesContent(
            state = ExchangeRatesState(
                rates = sampleRates,
                isLoading = false,
                error = null
            ),
            onEvent = {}
        )
    }
}