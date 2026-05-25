**Запуск инфраструктуры для локальной разработки:** <br>

```shell
docker-compose up -d
```

**Запуск миграций на локальной БД:**

```shell
cd ./migrations 
mvn process-resources liquibase:update@migrate-backend "-Ddb.url=jdbc:postgresql://localhost:5432/safe_road?currentSchema=safe_road" "-Ddb.username=safe_road" "-Ddb.password=safe_road" "-DskipTests=true"
```

**Удаление инфраструктуры для локальной разработки:** <br>

```shell
docker-compose down
```

**При локальном запуске**: 
- swagger: http://localhost:8080/swagger-ui/index.html
