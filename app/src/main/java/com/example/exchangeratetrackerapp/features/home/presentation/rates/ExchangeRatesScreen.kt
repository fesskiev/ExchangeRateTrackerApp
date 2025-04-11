package com.example.exchangeratetrackerapp.features.home.presentation.rates

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.exchangeratetrackerapp.core.ui.components.ExchangeRateAppBar
import com.example.exchangeratetrackerapp.features.home.presentation.rates.model.ExchangeRateUi
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.exchangeratetrackerapp.R
import com.example.exchangeratetrackerapp.core.ui.components.ErrorView
import com.example.exchangeratetrackerapp.features.home.presentation.rates.components.EmptyCurrency
import com.example.exchangeratetrackerapp.features.home.presentation.rates.components.LastUpdatedText
import com.example.exchangeratetrackerapp.features.home.presentation.rates.components.SwipeableCurrencyRateItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeRatesScreen(
    onAddCurrenciesClick: () -> Unit,
    viewModel: ExchangeRatesViewModel = koinViewModel()
) {
    DisposableEffect(Unit) {
        viewModel.onEvent(ExchangeRatesEvent.UpdateCurrencies)
        viewModel.onEvent(ExchangeRatesEvent.StartPeriodicUpdates)

        onDispose {
            viewModel.onEvent(ExchangeRatesEvent.StopPeriodicUpdates)
        }
    }

    val state by viewModel.state.collectAsState()
    ExchangeRatesContent(
        state = state,
        onEvent = { event ->
            if (event is ExchangeRatesEvent.AddCurrencies) {
                onAddCurrenciesClick()
            } else {
                viewModel.onEvent(event)
            }
        }
    )
}

@Composable
fun ExchangeRatesContent(
    state: ExchangeRatesState,
    onEvent: (ExchangeRatesEvent) -> Unit
) {
    Scaffold(
        topBar = {
            ExchangeRateAppBar(
                title = stringResource(R.string.exchange_rates),
                actions = {
                    IconButton(onClick = { onEvent(ExchangeRatesEvent.AddCurrencies) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_asset)
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
                        onRetry = { onEvent(ExchangeRatesEvent.StartPeriodicUpdates) }
                    )
                }

                state.rates.isEmpty() -> EmptyCurrency()
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp)
                    ) {
                        val lastUpdate = state.lastUpdate
                        if (lastUpdate != null) {
                            LastUpdatedText(lastUpdate)
                        }
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(
                                items = state.rates,
                                key = { rate -> rate.code }
                            ) { rate ->
                                SwipeableCurrencyRateItem(
                                    exchangeRate = rate,
                                    onRemoveClick = { onEvent(ExchangeRatesEvent.RemoveCurrency(rate.code)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExchangeRatesPreview() {
    val sampleRates = listOf(
        ExchangeRateUi("USD", 1.0000, 0.02),
        ExchangeRateUi("EUR", 0.8934, -0.14),
        ExchangeRateUi("BTC", 42356.89, 1.23)
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