package com.example.currencyinsights.model;

import java.util.List;

public class TransactionAnalysis {
    private List<Transaction> converted;
    private List<Transaction> suspicious;

    public TransactionAnalysis(List<Transaction> converted, List<Transaction> suspicious) {
        this.converted = converted;
        this.suspicious = suspicious;
    }

    public List<Transaction> getConverted() { return converted; }
    public void setConverted(List<Transaction> converted) { this.converted = converted; }
    public List<Transaction> getSuspicious() { return suspicious; }
    public void setSuspicious(List<Transaction> suspicious) { this.suspicious = suspicious; }
}