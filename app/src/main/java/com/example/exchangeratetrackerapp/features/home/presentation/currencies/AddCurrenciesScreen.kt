package com.example.exchangeratetrackerapp.features.home.presentation.currencies

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.exchangeratetrackerapp.core.ui.components.ExchangeRateAppBar
import com.example.exchangeratetrackerapp.features.home.presentation.currencies.components.CurrencySearchBar
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.exchangeratetrackerapp.R
import com.example.exchangeratetrackerapp.core.ui.components.ErrorView
import com.example.exchangeratetrackerapp.features.home.presentation.currencies.components.CurrencyItem
import com.example.exchangeratetrackerapp.features.home.presentation.currencies.components.SectionHeader
import com.example.exchangeratetrackerapp.features.home.presentation.currencies.model.CurrencySectionType
import com.example.exchangeratetrackerapp.features.home.presentation.currencies.model.CurrencySectionUi
import com.example.exchangeratetrackerapp.features.home.presentation.currencies.model.CurrencyUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddACurrenciesScreen(
    onBackClick: () -> Unit,
    viewModel: AddCurrenciesViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AddCurrenciesEvent.NavigateBack -> onBackClick()
                else -> {}
            }
        }
    }
    val state by viewModel.state.collectAsState()
    AddACurrenciesContent(
        state = state,
        onEvent = { event ->
            if (event is AddCurrenciesEvent.NavigateBack) {
                onBackClick()
            } else {
                viewModel.onEvent(event)
            }
        }
    )
}

@Composable
fun AddACurrenciesContent(
    state: AddCurrenciesState,
    onEvent: (AddCurrenciesEvent) -> Unit
) {
    Scaffold(
        topBar = {
            Column {
                ExchangeRateAppBar(
                    title = stringResource(R.string.add_asset),
                    showBackButton = true,
                    onBackClick = { onEvent(AddCurrenciesEvent.NavigateBack) },
                    actions = {
                        TextButton(onClick = { onEvent(AddCurrenciesEvent.Done) }) {
                            Text(
                                text = stringResource(R.string.done),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
                CurrencySearchBar(
                    query = state.searchQuery,
                    onQueryChange = { onEvent(AddCurrenciesEvent.SearchQuery(it)) }
                )
            }

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
                        onRetry = { onEvent(AddCurrenciesEvent.Retry) }
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        val sections = state.filteredSections ?: state.sections
                        sections.forEach { section ->
                            item(key = section.type.name) {
                                SectionHeader(
                                    title = getSectionTitle(section)
                                )
                            }
                            items(
                                items = section.currencies,
                                key = { currency -> currency.code }
                            ) { currency ->
                                CurrencyItem(
                                    currency = currency,
                                    isSelected = currency.isSelected,
                                    onToggleCurrencyItem = { onEvent(AddCurrenciesEvent.ToggleCurrencyItem(currency)) }
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
private fun getSectionTitle(section: CurrencySectionUi): String = when (section.type) {
    CurrencySectionType.POPULAR_ASSETS -> stringResource(R.string.popular_assets)
    CurrencySectionType.CRYPTOCURRENCIES -> stringResource(R.string.cryptocurrencies)
    CurrencySectionType.OTHER_CURRENCIES -> stringResource(R.string.other_currencies)
}

@Preview(showBackground = true)
@Composable
fun AddCurrenciesPreview() {
    val sections = listOf(
        CurrencySectionUi(
            type =  CurrencySectionType.POPULAR_ASSETS,
            currencies = listOf(
                CurrencyUi("USD", "US Dollar", "$", true),
                CurrencyUi("EUR", "Euro", "€", false),
                CurrencyUi("GBP", "British Pound", "£", false),
                CurrencyUi("JPY", "Japanese Yen", "¥", false)
            )
        ),
        CurrencySectionUi(
            type =  CurrencySectionType.CRYPTOCURRENCIES,
            currencies = listOf(
                CurrencyUi("BTC", "Bitcoin", "₿", true),
                CurrencyUi("ETH", "Ethereum", "Ξ", false)
            )
        )
    )
    MaterialTheme {
        AddACurrenciesContent(
            state = AddCurrenciesState(
                sections = sections,
                isLoading = false,
                error = null
            ),
            onEvent = {}
        )
    }
}

