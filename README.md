zamrad-api 
----------

1. Clone repository and run `mvn clean package docker:build` to build the latest image of the application and the mysql container
2. Run `docker-compose up` to start the application and mysql containers
3. Run integration tests from within your IDE as you would run any other test or from the terminal using `mvn verify`

NOTE: To keep ITs seperate from unit tests, they live in a directory called it under `src`
 
