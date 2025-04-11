package com.example.exchangeratetrackerapp.core.common

val popularCurrencyCodes = setOf(
    "USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "HKD", "SGD"
)

val cryptoCurrencyCodes = setOf(
    "BTC", "ETH", "USDT", "BNB", "XRP"
)

fun String.getCurrencySymbol() =
    when (lowercase()) {
        "usd" -> "$"
        "eur" -> "€"
        "gbp" -> "£"
        "jpy", "cny" -> "¥"
        "krw" -> "₩"
        "inr" -> "₹"
        "btc" -> "₿"
        "eth" -> "Ξ"
        "chf" -> "Fr"
        "brl" -> "R$"
        "ils" -> "₪"
        "try" -> "₺"
        "thb" -> "฿"
        "idr" -> "Rp"
        "sar" -> "﷼"
        "aed" -> "د.إ"
        "php" -> "₱"
        "zar" -> "R"
        "sek", "nok", "dkk" -> "kr"
        "pln" -> "zł"
        "huf" -> "Ft"
        "czk" -> "Kč"
        else -> first().toString()
    }

fun String.getCurrencyName() =
    when (lowercase()) {
        // Popular fiat currencies
        "usd" -> "United States Dollar"
        "eur" -> "Euro"
        "gbp" -> "British Pound Sterling"
        "jpy" -> "Japanese Yen"
        "cad" -> "Canadian Dollar"
        "aud" -> "Australian Dollar"
        "chf" -> "Swiss Franc"
        "cny" -> "Chinese Yuan"
        "nzd" -> "New Zealand Dollar"
        "inr" -> "Indian Rupee"
        "hkd" -> "Hong Kong Dollar"
        "sgd" -> "Singapore Dollar"
        "krw" -> "South Korean Won"
        "mxn" -> "Mexican Peso"
        "brl" -> "Brazilian Real"
        "zar" -> "South African Rand"
        "sek" -> "Swedish Krona"
        "nok" -> "Norwegian Krone"
        "dkk" -> "Danish Krone"
        "pln" -> "Polish Zloty"
        "try" -> "Turkish Lira"
        "ils" -> "Israeli New Sheqel"
        "thb" -> "Thai Baht"
        "myr" -> "Malaysian Ringgit"
        "idr" -> "Indonesian Rupiah"
        "php" -> "Philippine Peso"
        "aed" -> "United Arab Emirates Dirham"
        "sar" -> "Saudi Riyal"
        "twd" -> "New Taiwan Dollar"

        // Cryptocurrencies
        "btc" -> "Bitcoin"
        "eth" -> "Ethereum"
        "usdt" -> "Tether"
        "bnb" -> "Binance Coin"
        "xrp" -> "Ripple"
        "sol" -> "Solana"
        "ada" -> "Cardano"
        "doge" -> "Dogecoin"
        "dot" -> "Polkadot"
        "matic" -> "Polygon"
        "ltc" -> "Litecoin"

        else -> this
    }