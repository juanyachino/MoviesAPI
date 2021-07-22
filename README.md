## MOVIESAPI 
# Using JPA with mysql access implementation.
https://spring.io/guides/gs/accessing-data-mysql/

# Run app:

> ./mvnw spring-boot:run

# application.properties file

> spring.jpa.hibernate.ddl-auto=update   -> Updates current schema if there are
> any changes, it keeps the DB info also.
> 
> spring.jpa.hibernate.ddl-auto=create-drop  -> Drops the database schema every time the server starts
> .May throw DDL exceptions.
> 
> Rename application.properties.example to application.properties
> 
> Rename application-test.properties.example to application-test.properties
> 
> Replace the username and password values with your own values in both files.


# Database setup:
> mysql> create database movies; -- Creates the new production database
> 
> mysql> create database movies_dev; -- Creates the new test database
> 
> mysql> grant all on movies.* to '<YOURUSER>'@'%'; -- Gives all privileges to the user on the newly created database
> 
> mysql> grant all on movies_dev.* to '<YOURPASSWORD>'@'%'; -- Gives all privileges to the user on the newly created database
> 

## Useful links and resources:
# Many-to-many implementation
> https://attacomsian.com/blog/spring-data-jpa-many-to-many-mapping
>
> https://www.baeldung.com/jpa-many-to-man

 # Storing image files on DB:
 > https://www.baeldung.com/java-db-storing-files
> 
> # Send files in a post request with Postman: 
> - When creating/editing a character/genre/movie:
> - go to Body tab -> form-data
 > - enter key : multipartImage 
> - choose file from the dropdown
 > - choose any file under the Value column.

# Spring security: logging in and obtaining the JSON web token:
> - register an acoount :
> 
> - Log in
> 
> - Copy the bearer token from the response body.
> 
> - Paste it under Authorization tab -> Bearer token on your next requests.

# Send emails with SendGrid setup:
> https://dzone.com/articles/integrate-sendgrid-with-a-spring-boot-and-java-app
>
> https://app.sendgrid.com/guide/integrate/langs/java
> 
> Create a file sendgrid.env with your sendgrid API key or paste it on
> 
> src/main/java/com/moviesAPI/moviesAPI/config/WebSecurityConfig.java
