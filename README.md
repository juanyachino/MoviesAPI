## MOVIESAPI 
# Using JPA with mysql access implementation.
https://spring.io/guides/gs/accessing-data-mysql/

# Run app:

> ./mvnw spring-boot:run

# Database setup:
> mysql> create database movies; -- Creates the new database
> 
> mysql> create user 'springuser'@'%' identified by 'ThePassword'; -- Creates the user
> 
> mysql> grant all on movies.* to 'springuser'@'%'; -- Gives all privileges to the new user on the newly created database
> 
# Many-to-many implementation
https://attacomsian.com/blog/spring-data-jpa-many-to-many-mapping

https://www.baeldung.com/jpa-many-to-man

 # Storing image files on DB
 https://www.baeldung.com/java-db-storing-files
