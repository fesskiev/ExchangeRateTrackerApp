package com.example.exchangeratetrackerapp

import com.example.exchangeratetrackerapp.core.data.local.CurrenciesCache
import com.example.exchangeratetrackerapp.core.data.local.ExchangeRatesCache
import com.example.exchangeratetrackerapp.core.data.local.StoredRatesData
import com.example.exchangeratetrackerapp.core.data.local.mapResponseToStoredRatesData
import com.example.exchangeratetrackerapp.core.data.remote.ExchangeRateApi
import com.example.exchangeratetrackerapp.core.data.remote.ExchangeRateResponse
import com.example.exchangeratetrackerapp.features.home.presentation.rates.ExchangeRatesEvent
import com.example.exchangeratetrackerapp.features.home.presentation.rates.ExchangeRatesState
import com.example.exchangeratetrackerapp.features.home.presentation.rates.ExchangeRatesViewModel
import com.example.exchangeratetrackerapp.features.home.presentation.rates.model.ExchangeRateUi
import com.example.exchangeratetrackerapp.features.home.presentation.rates.model.mapSelectedCurrenciesToUiModel
import io.mockk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class ExchangeRatesViewModelTest {

    private val pathToMapper =
        "com.example.exchangeratetrackerapp.features.home.presentation.rates.model.MapperKt"
    private val pathToStoredRatesData =
        "com.example.exchangeratetrackerapp.core.data.local.StoredRatesDataKt"

    private val api: ExchangeRateApi = mockk()
    private val exchangeRatesCache = mockk<ExchangeRatesCache>().apply {
        coEvery { getLatestRates() } returns null
        coEvery { getPreviousRates() } returns null
    }
    private val currenciesCache = mockk<CurrenciesCache>().apply {
        coEvery { getSelectedCurrencies() } returns emptyList()
    }
    val storedRates = mockk<StoredRatesData>().apply {
        every { updateTime } returns "Just now"
    }

    private lateinit var viewModel: ExchangeRatesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        // Mock the isActive property to break the infinite loop after 2 iterations
        mockkStatic("kotlinx.coroutines.CoroutineScopeKt")
        var callCount = 0
        every { any<CoroutineScope>().isActive } answers {
            callCount++ < 2
        }

        viewModel = ExchangeRatesViewModel(api, exchangeRatesCache, currenciesCache)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `given no selected currencies, when starting periodic updates, then show empty currencies state`() =
        runTest {
            // Given
            coEvery { currenciesCache.getSelectedCurrencies() } returns emptyList()

            // When
            viewModel.onEvent(ExchangeRatesEvent.StartPeriodicUpdates)

            // Then
            val expectedState = ExchangeRatesState(
                isLoading = false,
                rates = emptyList(),
                lastUpdate = null,
                error = null
            )

            assertEquals(expectedState, viewModel.state.value)
        }

    @Test
    fun `given selected currencies, when starting periodic updates, then fetch rates and update state`() =
        runTest {
            mockkStatic(pathToStoredRatesData)
            mockkStatic(pathToMapper)

            // Given
            val selectedCurrencies = listOf("USD", "EUR")
            val ratesResponse = mockk<ExchangeRateResponse>()
            val uiRates = listOf(
                ExchangeRateUi("USD", 1.0, 0.0),
                ExchangeRateUi("EUR", 0.85, -0.01)
            )

            coEvery { currenciesCache.getSelectedCurrencies() } returns selectedCurrencies
            coEvery { api.getLatestExchangeRates() } returns ratesResponse
            every { ratesResponse.mapResponseToStoredRatesData() } returns storedRates
            coEvery { exchangeRatesCache.saveRates(storedRates) } just runs
            coEvery { exchangeRatesCache.getLatestRates() } returns storedRates
            coEvery { exchangeRatesCache.getPreviousRates() } returns mockk()

            every {
                mapSelectedCurrenciesToUiModel(
                    latestRates = storedRates,
                    previousRatesData = any(),
                    selectedCurrencies = selectedCurrencies
                )
            } returns uiRates

            // When
            viewModel.onEvent(ExchangeRatesEvent.StartPeriodicUpdates)

            // Then
            val expectedState = ExchangeRatesState(
                isLoading = false,
                rates = uiRates,
                lastUpdate = "Just now",
                error = null
            )

            assertEquals(expectedState, viewModel.state.value)

            coVerify { api.getLatestExchangeRates() }
            coVerify { exchangeRatesCache.saveRates(storedRates) }

            unmockkStatic(pathToStoredRatesData)
            unmockkStatic(pathToMapper)

        }

    @Test
    fun `given active periodic updates, when stopping periodic updates, then job is cancelled`() =
        runTest {
            // Given
            val selectedCurrencies = listOf("USD", "EUR")
            coEvery { currenciesCache.getSelectedCurrencies() } returns selectedCurrencies

            viewModel.onEvent(ExchangeRatesEvent.StartPeriodicUpdates)

            // When
            viewModel.onEvent(ExchangeRatesEvent.StopPeriodicUpdates)

            coVerify(exactly = 1) { api.getLatestExchangeRates() }
        }

    @Test
    fun `given api error, when fetching rates, then show error state`() = runTest {
        // Given
        val selectedCurrencies = listOf("USD", "EUR")
        val errorMessage = "Network error"

        coEvery { currenciesCache.getSelectedCurrencies() } returns selectedCurrencies
        coEvery { api.getLatestExchangeRates() } throws Exception(errorMessage)

        // When
        viewModel.onEvent(ExchangeRatesEvent.StartPeriodicUpdates)

        // Then
        val expectedState = ExchangeRatesState(
            isLoading = false,
            rates = emptyList(),
            lastUpdate = null,
            error = errorMessage
        )
        assertEquals(expectedState, viewModel.state.value)
    }

    @Test
    fun `given currency code, when removing currency, then update selected currencies`() = runTest {
        // Given
        val currencyToRemove = "USD"
        val remainingCurrencies = listOf("EUR")
        val uiRates = listOf(ExchangeRateUi("EUR", 0.85, -0.01))

        coEvery { currenciesCache.removeCurrency(currencyToRemove) } just runs
        coEvery { currenciesCache.getSelectedCurrencies() } returns remainingCurrencies
        coEvery { exchangeRatesCache.getLatestRates() } returns storedRates
        coEvery { exchangeRatesCache.getPreviousRates() } returns null

        mockkStatic(pathToMapper)
        every {
            mapSelectedCurrenciesToUiModel(
                latestRates = storedRates,
                previousRatesData = any(),
                selectedCurrencies = remainingCurrencies
            )
        } returns uiRates

        // When
        viewModel.onEvent(ExchangeRatesEvent.RemoveCurrency(currencyToRemove))

        // Then
        val expectedState = ExchangeRatesState(
            isLoading = false,
            rates = uiRates,
            lastUpdate = "Just now",
            error = null
        )
        assertEquals(expectedState, viewModel.state.value)

        coVerify { currenciesCache.removeCurrency(currencyToRemove) }
        unmockkStatic(pathToMapper)
    }

    @Test
    fun `given last currency removed, when removing currency, then stop updates and show empty state`() =
        runTest {
            // Given
            val currencyToRemove = "USD"

            coEvery { currenciesCache.removeCurrency(currencyToRemove) } just runs
            coEvery { currenciesCache.getSelectedCurrencies() } returns emptyList()

            // When
            viewModel.onEvent(ExchangeRatesEvent.RemoveCurrency(currencyToRemove))

            // Then
            val expectedState = ExchangeRatesState(
                isLoading = false,
                rates = emptyList(),
                lastUpdate = null,
                error = null
            )
            assertEquals(expectedState, viewModel.state.value)
        }

    @Test
    fun `given error removing currency, when removing currency, then show error state`() = runTest {
        // Given
        val currencyToRemove = "USD"
        val errorMessage = "Database error"

        coEvery { currenciesCache.removeCurrency(currencyToRemove) } throws Exception(errorMessage)

        // When
        viewModel.onEvent(ExchangeRatesEvent.RemoveCurrency(currencyToRemove))

        // Then
        val expectedState = ExchangeRatesState(
            isLoading = false,
            rates = emptyList(),
            lastUpdate = null,
            error = errorMessage
        )
        assertEquals(expectedState, viewModel.state.value)
    }

    @Test
    fun `given valid state, when updating currencies, then fetch and update currency state`() =
        runTest {
            // Given
            val selectedCurrencies = listOf("USD", "EUR")
            val uiRates = listOf(
                ExchangeRateUi("USD", 1.0, 0.0),
                ExchangeRateUi("EUR", 0.85, -0.01)
            )

            coEvery { currenciesCache.getSelectedCurrencies() } returns selectedCurrencies
            coEvery { exchangeRatesCache.getLatestRates() } returns storedRates
            coEvery { exchangeRatesCache.getPreviousRates() } returns null

            mockkStatic(pathToMapper)
            every {
                mapSelectedCurrenciesToUiModel(
                    latestRates = storedRates,
                    previousRatesData = any(),
                    selectedCurrencies = selectedCurrencies
                )
            } returns uiRates

            // When
            viewModel.onEvent(ExchangeRatesEvent.UpdateCurrencies)

            // Then
            val expectedState = ExchangeRatesState(
                isLoading = false,
                rates = uiRates,
                lastUpdate = "Just now",
                error = null
            )
            assertEquals(expectedState, viewModel.state.value)

            unmockkStatic(pathToMapper)
        }

    @Test
    fun `given error updating currencies, when updating currencies, then show error state`() =
        runTest {
            // Given
            val errorMessage = "Failed to update currencies"

            coEvery { currenciesCache.getSelectedCurrencies() } throws Exception(errorMessage)

            // When
            viewModel.onEvent(ExchangeRatesEvent.UpdateCurrencies)

            // Then
            val expectedState = ExchangeRatesState(
                isLoading = false,
                rates = emptyList(),
                lastUpdate = null,
                error = errorMessage
            )
            assertEquals(expectedState, viewModel.state.value)
        }
}