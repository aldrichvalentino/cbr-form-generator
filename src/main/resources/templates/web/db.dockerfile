FROM mysql:5

# Use database
ENV MYSQL_DATABASE application

# Build tables
COPY ./database.sql /docker-entrypoint-initdb.d/