package com.example.currencyinsights.controller;

import com.example.currencyinsights.model.Transaction;
import com.example.currencyinsights.model.TransactionAnalysis;
import com.example.currencyinsights.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping("/analyze")
    public TransactionAnalysis analyze(@RequestBody List<Transaction> transactions,
                                       @RequestParam(defaultValue = "EUR") String currency) {
        return service.analyze(transactions, currency);
    }
}