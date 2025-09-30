package com.example.currencyinsights.service;

import com.example.currencyinsights.model.Transaction;
import com.example.currencyinsights.model.TransactionAnalysis;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final WebClient webClient;

    public TransactionService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://open.er-api.com").build();
    }

    // DTO to map the API response
    public static class ExchangeRateResponse {
        private String result;
        private String base_code;
        private java.util.Map<String, BigDecimal> rates;

        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }

        public String getBase_code() { return base_code; }
        public void setBase_code(String base_code) { this.base_code = base_code; }

        public java.util.Map<String, BigDecimal> getRates() { return rates; }
        public void setRates(java.util.Map<String, BigDecimal> rates) { this.rates = rates; }
    }

    public TransactionAnalysis analyze(List<Transaction> txs, String targetCurrency) {
        if (txs.isEmpty()) {
            return new TransactionAnalysis(List.of(), List.of());
        }

        // Use the currency of the first transaction as base
        String baseCurrency = txs.get(0).getCurrency().toUpperCase();

        ExchangeRateResponse response;
        try {
            response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v6/latest/{base}")
                            .build(baseCurrency))
                    .retrieve()
                    .bodyToMono(ExchangeRateResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to fetch exchange rate: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch exchange rate: " + e.getMessage(), e);
        }

        if (response == null || !"success".equalsIgnoreCase(response.getResult())
                || response.getRates() == null || !response.getRates().containsKey(targetCurrency.toUpperCase())) {
            throw new RuntimeException("Exchange rate not found for target currency: " + targetCurrency +
                    " (base: " + baseCurrency + ")");
        }

        BigDecimal rate = response.getRates().get(targetCurrency.toUpperCase());
        System.out.println("Exchange rate " + baseCurrency + " -> " + targetCurrency + ": " + rate);

        // Convert transactions
        List<Transaction> converted = txs.stream()
                .map(t -> {
                    Transaction c = new Transaction();
                    c.setId(t.getId());
                    c.setAmount(rate.multiply(BigDecimal.valueOf(t.getAmount())).doubleValue());
                    c.setCurrency(targetCurrency.toUpperCase());
                    return c;
                })
                .collect(Collectors.toList());

        // Detect suspicious transactions (>1000 in target currency)
        List<Transaction> suspicious = converted.stream()
                .filter(t -> t.getAmount() > 1000)
                .collect(Collectors.toList());

        return new TransactionAnalysis(converted, suspicious);
    }
}
