# Currency Insights

A Spring Boot demo application that analyzes financial transactions with real-time foreign exchange rates from the [ExchangeRate API](https://open.er-api.com/).

This project was built to demonstrate backend engineering skills:

* Java + Spring Boot
* Integration with a 3rd-party API
* RESTful endpoint with business logic
* JSON-based request/response

---

## Features

* Accepts a list of transactions in different currencies
* Converts all amounts into a **target currency** using live exchange rates
* Identifies **suspicious transactions** (amounts > 1000 in target currency)
* Exposes a REST API for testing

---

## Tech Stack

* **Java 17+**
* **Spring Boot 3.4.5**
* **Maven**
* **WebClient (Spring Reactive)** for external API calls

---

## Run Locally

### Prerequisites

* JDK 17+
* Maven 3.9+

### Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/currency-insights.git
cd currency-insights
```

### Build and run

```bash
./mvnw clean spring-boot:run
```

The app will start at `http://localhost:8080`.

---

## API Usage

### Endpoint

`POST /transactions/analyze?currency={TARGET}`

* **{TARGET}** = target currency code (e.g., `EUR`, `GBP`)

### Example Request

```bash
curl -X POST "http://localhost:8080/transactions/analyze?currency=EUR" \
  -H "Content-Type: application/json" \
  -d '[{"id":"1","amount":200,"currency":"USD"},
       {"id":"2","amount":1500,"currency":"USD"}]'
```

### Example Response

```json
{
  "converted": [
    {"id":"1","amount":184.68,"currency":"EUR"},
    {"id":"2","amount":1385.10,"currency":"EUR"}
  ],
  "suspicious": [
    {"id":"2","amount":1385.10,"currency":"EUR"}
  ]
}
```

---

## External API

This project uses [open.er-api.com](https://open.er-api.com/v6/latest/USD), a free exchange rate API that requires no authentication.

---

## Future Enhancements

* Add database persistence
* Support multiple base currencies in one request
* Implement JWT-based authentication

---

## License

MIT License. Free to use and modify.
