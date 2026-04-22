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