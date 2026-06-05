# transactions table
- id -> auto generated
- user id 
- transaction date -> timestamp
- transaction details -> varchar 
- amount -> convert it to cents
- api 
  1. take all transactions based omn user name
  2. insert new transaction -> get an input users name and then you find its id on that

- query
- logs
  1. info 
  2. debug
  3. trace
  4. error
  5. warn
- application configure the logs level
- Builder pattern implementation
- record-class difference
- abstract class and normal class difference
- abstract class interface difference
- Refactor TransactionRequest and UserRequest to remove duplicate fields(inheritance)
- return the time it takes to return a response with milliseconds
- make all the controller log levels info and not debug
- custom exception for 
  1. user related errors 
  2. transaction related errors
- configure global exception handler
- user creation not in the auth controller
- update password
- create user account 
  1. contain the user balance info 
  2. option to withdraw and deposit
  3. one user many acounts
  4. return the users accounts

- implement a cache for maintenance

- currency column
- currency api
- balance history   
- endpoint to clear the cache
- transaction exception that handles bussiness logic errors  
- implement actuator 
  1. liveness 
  2. readiness
- add to the get users endpoints the accounts also not only the users info
  - unique pair  