package com.example.currencyinsights.service;

import com.example.currencyinsights.model.Transaction;
import com.example.currencyinsights.model.TransactionAnalysis;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final ExchangeRateProvider rateProvider;

    // Constructor injection of the provider
    public TransactionService(ExchangeRateProvider rateProvider) {
        this.rateProvider = rateProvider;
    }

    public TransactionAnalysis analyze(List<Transaction> txs, String targetCurrency) {
        if (txs == null || txs.isEmpty()) {
            return new TransactionAnalysis(List.of(), List.of());
        }

        String baseCurrency = txs.get(0).getCurrency().toUpperCase();
        BigDecimal rate = rateProvider.getRate(baseCurrency, targetCurrency.toUpperCase());

        List<Transaction> converted = txs.stream()
                .map(t -> {
                    Transaction c = new Transaction();
                    c.setId(t.getId());
                    c.setAmount(rate.multiply(BigDecimal.valueOf(t.getAmount())).doubleValue());
                    c.setCurrency(targetCurrency.toUpperCase());
                    return c;
                })
                .collect(Collectors.toList());

        List<Transaction> suspicious = converted.stream()
                .filter(t -> t.getAmount() > 1000)
                .collect(Collectors.toList());

        return new TransactionAnalysis(converted, suspicious);
    }
}
