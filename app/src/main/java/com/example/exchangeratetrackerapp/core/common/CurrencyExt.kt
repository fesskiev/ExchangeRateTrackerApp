package com.example.exchangeratetrackerapp.core.common

fun String.getCurrencySymbol() =
    when (lowercase()) {
        "usd" -> "$"
        "eur" -> "€"
        "gbp" -> "£"
        "jpy" -> "¥"
        "btc" -> "₿"
        "eth" -> "Ξ"
        "chf" -> "Fr"
        else -> first().toString()
    }

fun String.getCurrencyName() =
    when (lowercase()) {
        "usd" -> "US Dollar"
        "eur" -> "Euro"
        "gbp" -> "British Pound"
        "jpy" -> "Japanese Yen"
        "btc" -> "Bitcoin"
        "eth" -> "Ethereum"
        "chf" -> "Swiss Franc"
        "aud" -> "Australian Dollar"
        "cad" -> "Canadian Dollar"
        "cny" -> "Chinese Yuan"
        "usdt" -> "Tether"
        "bnb" -> "Binance Coin"
        "xrp" -> "Ripple"
        else -> this
    }