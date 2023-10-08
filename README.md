# Stage 2/6: Authentication
## Description
Enterprise applications like anti-fraud systems are used by different types of users with various access levels. Different users should have different rights to access various system parts. Let's set up the authentication procedure for our system. Of course, you can elaborate it yourself, but it is considered good practice to use an already tested and reliable implementation. Fortunately, Spring includes the Spring Security module that contains the right methods.

In this stage, you need to provide the HTTP Basic authentication for our `REST` service with the `JDBC` implementations of `UserDetailService` for appUser management. You will require an endpoint for registering users at `POST /api/auth/appUser`.

>To run the tests, the `application.properties` file should contain the following line: `spring.datasource.url=jdbc:h2:file:../service_db`.

You will also need some security dependencies in Gradle:
```groovy
dependencies {
   ... other dependencies ...
   implementation 'org.springframework.boot:spring-boot-starter-security'
}
```

Make sure that the <a href="https://owasp.org/www-community/attacks/csrf">CSRF</a> is disabled to facilitate testing. We do not recommend disabling it in real-world projects. We suggest the following configuration:
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
            .httpBasic(Customizer.withDefaults())
            .csrf(CsrfConfigurer::disable)                           // For modifying requests via Postman
            .exceptionHandling(handing -> handing
                    .authenticationEntryPoint(restAuthenticationEntryPoint) // Handles auth error
            )
            .headers(headers -> headers.frameOptions().disable())           // for Postman, the H2 console
            .authorizeHttpRequests(requests -> requests                     // manage access
                    .requestMatchers(HttpMethod.POST, "/api/auth/appUser").permitAll()
                    .requestMatchers("/actuator/shutdown").permitAll()      // needs to run test
                    // other matchers
            )
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
            )
            // other configurations
            .build();
    }
```

<b>Please note</b> that `/actuator/shutdown` endpoint should be available to unauthorized users for testing purposes.

`RestAuthenticationEntryPoint` is an instance of the class that implements the `AuthenticationEntryPoint` interface. This endpoint handles authentication errors. For example:
```java
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}
```

## Objectives
- Add the Spring security to your project and configure the HTTP basic authentication;
- For storing users and passwords, add a JDBC implementation of `UserDetailsService` with an H2 database. Usernames must be <b>case insensitive</b>;
- Add the `POST /api/auth/appUser` endpoint. In this stage, It must be available to unauthorized users for registration and accept data in the JSON format:
```json
{
   "name": "<String value, not empty>",
   "username": "<String value, not empty>",
   "password": "<String value, not empty>"
}
```
- If a appUser has been successfully added, the endpoint must respond with the `HTTP CREATED` status (`201`) and the following body:
```json
{
   "id": <Long value, not empty>,
   "name": "<String value, not empty>",
   "username": "<String value, not empty>"
}
```
- If an attempt to register an existing appUser was a failure, the endpoint must respond with the `HTTP CONFLICT` status (`409`);
- If a request contains wrong data, the endpoint must respond with the `BAD REQUEST` status (`400`);
- Add the `GET /api/auth/list` endpoint. It must be available to all authorized users;
- The endpoint must respond with the `HTTP OK` status (`200`) and the body with an array of objects representing the users sorted by ID in <b>ascending order</b>. Return an empty JSON array if there's no information:
```json
[
    {
        "id": <user1 id>,
        "name": "<user1 name>",
        "username": "<user1 username>"
    },
     ...
    {
        "id": <userN id>,
        "name": "<userN name>",
        "username": "<userN username>"
    }
]
```
- Add the `DELETE /api/auth/appUser/{username}` endpoint, where `{username}` specifies the appUser that should be deleted. The endpoint must be available to all authorized users. The endpoint must delete the appUser and respond with the `HTTP OK` status (`200`) and the following body:
```json
{
   "username": "<username>",
   "status": "Deleted successfully!"
}
```
- If a appUser is not found, respond with the `HTTP Not Found` status (`404`);
- Change the `POST /api/antifraud/transaction` endpoint; it must be available only to all authorized users.

## Examples
<b>Example 1:</b> <i>a POST request for /api/auth/appUser with the correct appUser information</i>

<i>Request body:</i>
```json
{
   "name": "John Doe",
   "username": "JohnDoe",
   "password": "secret"
}
```
<i>Response:</i> `201 CREATED`

<i>Response body:</i>
```json
{
   "id": 1,
   "name": "John Doe",
   "username": "JohnDoe"
}
```
<b>Example 2:</b> <i>a POST request for /api/auth/appUser with the occupied email</i>

<i>Request body:</i>
```json
{
   "name": "John Doe",
   "username": "JohnDoe",
   "password": "secret"
}
```

<i>Response:</i> `409 CONFLICT`

<b>Example 3:</b> <i>a POST request for /api/auth/appUser with the wrong format of the appUser JSON</i>

<i>Request body:</i>
```json
{
   "name": "John Doe",
   "password": "secret"
}
```

<i>Response:</i> `400 BAD REQUEST`

<b>Example 4:</b> <i>a GET request for /api/auth/list</i>

<i>Response:</i> `200 OK`
```json
[
  {
    "name":"John Doe",
    "username":"JohnDoe",
  },
  {
    "name":"JohnDoe2",
    "username":"JohnDoe2",
  }
]
```

<b>Example 5:</b> <i>a DELETE request for /api/auth/appUser/johndoe</i>

<i>Response:</i> `200 OK`
```json
{
   "username": "JohnDoe",
   "status": "Deleted successfully!"
}
```
