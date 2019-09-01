FROM mysql:5

# Use database
ENV MYSQL_DATABASE forms

# Build tables
COPY ./src/main/resources/database/migration/tables.sql /docker-entrypoint-initdb.d/