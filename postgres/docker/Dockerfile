FROM library/postgres
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=password
ENV POSTGRES_DB=prueba
COPY schema.sql docker-entrypoint-initdb.d/schema.sql