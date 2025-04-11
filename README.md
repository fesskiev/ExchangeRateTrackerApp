# Currency Exchange Rates App

This application uses the [Open Exchange Rates API](https://openexchangerates.org) to fetch and display currency exchange rates.

## API Setup

This project uses the Open Exchange Rates API to retrieve current exchange rate data. The API key is configured in the root `build.gradle` file.

### API Key Configuration

The API key is stored in the root `build.gradle` file as a BuildConfig field:

```gradle
buildConfigField("String", "APP_ID", "************************************")
```

### Note About API Key

The current API key  may be expired. If you encounter any API-related errors, please:

1. Register for a new API key at [Open Exchange Rates](https://openexchangerates.org/signup)
2. Replace the existing key in the root `build.gradle` file
3. Rebuild the project
