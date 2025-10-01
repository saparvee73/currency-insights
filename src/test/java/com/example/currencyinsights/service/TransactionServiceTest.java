package com.example.currencyinsights.service;

import com.example.currencyinsights.model.Transaction;
import com.example.currencyinsights.model.TransactionAnalysis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private ExchangeRateProvider mockRateProvider;

    private TransactionService transactionService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        transactionService = new TransactionService(mockRateProvider);
    }

    @Test
    void analyze_shouldConvertAndDetectSuspiciousCorrectly() {

        when(mockRateProvider.getRate("USD", "EUR")).thenReturn(BigDecimal.valueOf(0.5));

        List<Transaction> txs = List.of(
                new Transaction("1", 200, "USD"),
                new Transaction("2", 1500, "USD")
        );


        TransactionAnalysis analysis = transactionService.analyze(txs, "EUR");

        // Assert
        assertEquals(2, analysis.getConverted().size());
        assertEquals("EUR", analysis.getConverted().get(0).getCurrency());
        // 200 * 0.5 = 100
        assertEquals(100.0, analysis.getConverted().get(0).getAmount(), 1e-6);
        // 1500 * 0.5 = 750 -> not suspicious
        assertTrue(analysis.getSuspicious().isEmpty());

        verify(mockRateProvider, times(1)).getRate("USD", "EUR");
    }

    @Test
    void analyze_shouldMarkSuspiciousIfAboveThreshold() {
        when(mockRateProvider.getRate("USD", "EUR")).thenReturn(BigDecimal.ONE);

        List<Transaction> txs = List.of(new Transaction("99", 2000, "USD"));

        TransactionAnalysis analysis = transactionService.analyze(txs, "EUR");

        assertEquals(1, analysis.getConverted().size());
        assertEquals(1, analysis.getSuspicious().size());
        assertEquals("99", analysis.getSuspicious().get(0).getId());

        verify(mockRateProvider).getRate("USD", "EUR");
    }
}
