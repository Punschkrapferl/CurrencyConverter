package com.example.CurrencyConverter;




import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

    public class ExchangeRateDatabase {
        // Exchange rates to EURO - price for 1 Euro
        private final static ExchangeRate[] RATES = {
                new ExchangeRate("EUR", "Bruxelles", 1.0),
                new ExchangeRate("USD", "Washington", 1.0845),
                new ExchangeRate("JPY", "Tokyo", 130.02),
                new ExchangeRate("BGN", "Sofia", 1.9558),
                new ExchangeRate("CZK", "Prague", 27.473),
                new ExchangeRate("DKK", "Copenhagen", 7.4690),
                new ExchangeRate("GBP", "London", 0.73280),
                new ExchangeRate("HUF", "Budapest", 299.83),
                new ExchangeRate("PLN", "Warsaw", 4.0938),
                new ExchangeRate("RON", "Bucharest", 4.4050),
                new ExchangeRate("SEK", "Stockholm", 9.3207),
                new ExchangeRate("CHF", "Bern", 1.0439),
                new ExchangeRate("NOK", "Oslo", 8.6545),
                new ExchangeRate("HRK", "Zagreb", 7.6448),
                new ExchangeRate("RUB", "Moscow", 62.5595),
                new ExchangeRate("TRY", "Ankara", 2.8265),
                new ExchangeRate("AUD", "Canberra", 1.4158),
                new ExchangeRate("BRL", "Brasilia", 3.5616),
                new ExchangeRate("CAD", "Ottawa", 1.3709),
                new ExchangeRate("CNY", "Beijing", 6.7324),
                new ExchangeRate("HKD", "Hong Kong", 8.4100),
                new ExchangeRate("IDR", "Jakarta", 14172.71),
                new ExchangeRate("ILS", "Jerusalem", 4.3019),
                new ExchangeRate("INR", "New Delhi", 67.9180),
                new ExchangeRate("KRW", "Seoul", 1201.04),
                new ExchangeRate("MXN", "Mexico City", 16.5321),
                new ExchangeRate("MYR", "Kuala Lumpur", 4.0246),
                new ExchangeRate("NZD", "Wellington", 1.4417),
                new ExchangeRate("PHP", "Manila", 48.527),
                new ExchangeRate("SGD", "Singapore", 1.4898),
                new ExchangeRate("THB", "Bangkok", 35.328),
                new ExchangeRate("ZAR", "Cape Town", 13.1446)
        };

        private final static Map<String, ExchangeRate> CURRENCIES_MAP = new HashMap<>();

        private final static String[] CURRENCIES_LIST;

        static {
            for (ExchangeRate r : RATES) {
                CURRENCIES_MAP.put(r.getCurrencyName(), r);
            }
            CURRENCIES_LIST = new String[CURRENCIES_MAP.size()];

            CURRENCIES_MAP.keySet().toArray(CURRENCIES_LIST);
            Arrays.sort(CURRENCIES_LIST);

        }

        /**
         * Returns list of currency names
         */

        public String[] getCurrencies() {
            return CURRENCIES_LIST;
        }

        /**
         * Gets exchange rate for currency (equivalent for one Euro)
         */

        synchronized public double getExchangeRate(String currency) {
            return CURRENCIES_MAP.get(currency).getRateForOneEuro();
        }


        synchronized public void setExchangeRate(String currency, double rate) {
            if (CURRENCIES_MAP.get(currency) != null) {
                CURRENCIES_MAP.get(currency).setRateForOneEuro(rate);
            }
        }

        public String getCapital(String currency) {
            return CURRENCIES_MAP.get(currency).getCapital();
        }


        /**
         * Converts value of one currency to another
         * @return converted value
         */
        public double convert(double value, String currencyFrom, String currencyTo) {
            return value / getExchangeRate(currencyFrom) * getExchangeRate(currencyTo);
        }
    }

