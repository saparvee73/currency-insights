package com.example.currencyinsights.service;

import java.math.BigDecimal;

public interface ExchangeRateProvider {
    /**
     * Return the exchange rate from baseCurrency -> targetCurrency.
     * Example: getRate("USD","EUR") returns BigDecimal(0.92)
     *
     * Implementations should throw runtime exceptions for unrecoverable errors.
     */
    BigDecimal getRate(String baseCurrency, String targetCurrency);
}
