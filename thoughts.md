- finding a currency api
    ### options:

        1. https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@{date}/{apiVersion}/{endpoint}
            "at https://github.com/fawazahmed0/exchange-api"
        
        2. https://app.currencyapi.com/dashboard


- test the apis
- if no api works ask AI
- if AI gives no result  ask google again
- thing about a way to represent the amount in account in a certain currency 
    ### options:
    
        1. currency as enum [EUR, USD, JEN, LEK, KR]
        2. currency as string "less limited"

- if a user creates a account he most also add the currency of the account 
- from then on that account uses that currency

## transaction table

- here we add two columns "from and to"
- account currency and requested currency
- maybe we make a util to do the calculations 
- change it from two columns to four 
- we hold the account currency and also the amount we moving from the account
- the requested currency and the amount requested in that currency
- just to test the workflow we pass a json object of the ammount requested and the currency
- then we do the conversion to the currency of the account
- and then we choose if we want to deposit or withdraw the money

    ### columns
    `private String accountCurr;`
    `private BigDecimal accountTransfer;`
    `private String transferCurr;`
    `private BigDecimal transferAmmount;`