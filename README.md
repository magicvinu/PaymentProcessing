# PaymentProcessing

To standup the service, Please checkout the code and run below command.

gradlew bootRun

Below are the endpoint 

1. To check existing orders for a customer use below endpoint.

http://localhost:8080/orderDetails/{client_id}

Example:

http://localhost:8080/orderDetails/6

2. To process pending payment for a client use below endpoint.

http://localhost:8080/processPayments/{client_id}

Example:

http://localhost:8080/processPayments/10

Test document can be found in file
PaymentProcess.pdf
