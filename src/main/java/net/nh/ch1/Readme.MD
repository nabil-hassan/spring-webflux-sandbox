### Chapter 1: Basic Enddpoints

This chapter illustrates how to create basic RESTful endpoints in both traditional imperative REST controller style, and also the new functional controller style introduced as part of WebFlux.

  * The [Imperative Controllers](basicendpoints/ImperativeController.java) illustrates a variety of imperative endpoints.

### Testing

#### Manual Testing

You can test the endpoints manually using the commands below

  * Imperative GET greeting endpoint:

```shell
# call endpoint without optional request param
curl -v http://localhost:8080/imperative

# call endpoint supplying request param
curl -v http://localhost:8080/imperative?name=nabil
```