# JavaRestApi
Example JSON statistics service with REST-like API

## Build and test
maven is needed to build and run test.
```bash
mvn test
```
Will download all dependencies, build the project and execute test.

To run debug version of the service:

```bash
mvn assembly:single
java -jar target/*-jar-with-dependencies.js
```
can be used. Service will start on http://localhost:9999/
Service supports two handlers:
```
GET /statistics
```
```
POST /transaction
{
"amount": 1.0,
"timestampt": 15500000000
}
```
