# CBR Form Generator using jCOLIBRI
Stack:
- Spring Boot Application for serving web forms and displaying results
- jCOLIBRI for Case-based Reasoning

# How to build
For `vscode` users first:
1. Clone the repo
2. Run `./gradlew eclipse` to generate the project files. It will trigger gradle to download the dependencies into your local machine.
3. Create an `application.properties` file in the root directory for configurations. Edit the values to match your system.
``` sh
WORDNET_DIR=C:\\Program Files (x86)\\WordNet\\2.1\\dict
HIBERNATE_DRIVER=com.mysql.jdbc.Driver
HIBERNATE_CONNECTION=jdbc:mysql://localhost/travel
HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect
DB_USERNAME=admin
DB_PASSWORD=password
```
4. Run `./gradlew bootRun` to run the application.

# Bugs
1. Cannot hot reload with spring-boot-devtools. Use traditional `ctrl+c` to stop and rebuild the application to reload.

# TODO
1. Finish CBR cycle without any generation process.
2. Generate form + sql + backend + docker container
3. HTML Form preview window
4. Styling the whole app
5. Add new data + improve owl

# Wishlist
1. Refactor database schema
