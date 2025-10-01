package com.example.currencyinsights.service;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

@Component
public class WebClientExchangeRateProvider implements ExchangeRateProvider{

    private final WebClient webClient;

    public WebClientExchangeRateProvider(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://open.er-api.com").build();
    }

    // DTO for the API
    public static class ExchangeRateResponse {
        private String result;
        private String base_code;
        private Map<String, BigDecimal> rates;

        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }

        public String getBase_code() { return base_code; }
        public void setBase_code(String base_code) { this.base_code = base_code; }

        public Map<String, BigDecimal> getRates() { return rates; }
        public void setRates(Map<String, BigDecimal> rates) { this.rates = rates; }
    }

    @Override
    public BigDecimal getRate(String baseCurrency, String targetCurrency) {
        ExchangeRateResponse response;
        try {
            response = webClient.get()
                    .uri("/v6/latest/{base}", baseCurrency.toUpperCase())
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
                || response.getRates() == null
                || !response.getRates().containsKey(targetCurrency.toUpperCase())) {
            throw new RuntimeException("Exchange rate not found for " + baseCurrency + " -> " + targetCurrency);
        }

        return response.getRates().get(targetCurrency.toUpperCase());
    }
}
