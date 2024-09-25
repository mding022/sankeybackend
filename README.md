# Backend for my user-friendly Sankey Diagram generator.

# Setup
Install dependencies:
```sh
npm install
```

Run Spring Boot Application:
```sh
mvn spring-boot:run
```

# Usage
Install dependencies:
```sh
POST localhost:8080/build
```
Use Form-Data with the following data:
Install dependencies:
```sh
incomes | 10000,50000,500
ilabels | Investments,Paystub,Lottery
outputs | 15000,10000,900,3000,10000
olabels | Rent,Taxes,Laptop,Shopping,Restaurants
```