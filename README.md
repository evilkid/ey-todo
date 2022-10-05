## Environment:

- Java version: 17
- Maven version: 3.*
- Spring Boot version: 2.7.4

### Building the service

``` shell
$ ./gradlew clean build
```

### Running the tests

``` shell
$ ./gradlew clean test
```

### Running the service locally

``` shell
$ ./gradlew bootRun
```

## Description

This service exposes a set of endpoints that allows a user to manage a set of a todo list.

This project also comes with a [Postman collection](Todo Collection.postman_collection.json), that contains all the
endpoints and their respective request bodies.

### Endpoints descriptions

This service exposes 6 endpoints that manages the Todo objects:

- List all Todo objects
- Get a Todo object
- Create a Todo object
- Update a Todo status
- Update a Todo information
- Delete a Todo object

### Technical choices

This project is fairly simple, thus all the technical choices was selected accordingly.
The chosen project structure is a `Layer` one (`contollers`, `dto`, `entites`, `repositories`, `services`)

The DTO was only used as request object, and we instead return the entity as a response, for simplicity given that all
the returned fields do not need to be hidden from the consumer, or mapped.

When a service class returns a nullable object, we assume that the entity was not found in the database, thus no custom
exceptions were used, given that the service have no complicated business logic.

Same as the previous statement, the lack of business logic made it useless to create unit tests, instead this project
focuses on integration tests, which tests all the application components (controller, service and repository).
