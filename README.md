# URL Shortener

Pet project: сокращатель ссылок на Spring Boot с JWT-авторизацией, аналитикой кликов и Docker-окружением.

## Стек

- **Java 21**, Spring Boot 3.3
- Spring Security + JWT (jjwt)
- Spring Data JPA + Hibernate
- PostgreSQL 16 + Flyway (миграции)
- springdoc-openapi (Swagger UI)
- JUnit 5, Mockito, Testcontainers
- Docker + Docker Compose
- Lombok

## Возможности

- Регистрация и логин (JWT)
- Создание коротких ссылок (рандомный или кастомный код)
- Срок жизни ссылки (`expiresAt`)
- Публичный редирект `GET /{code}` с HTTP 302
- Асинхронная запись кликов (не тормозит редирект)
- Статистика: всего кликов, уникальные IP, топ referer'ов, клики по дням
- Пагинация списка ссылок
- Swagger UI для ручного тестирования

## API

### Auth

| Метод | Путь | Описание |
|-------|------|----------|
| POST | `/api/auth/register` | Регистрация |
| POST | `/api/auth/login` | Логин |

### Links (требуется JWT)

| Метод | Путь | Описание |
|-------|------|----------|
| POST | `/api/links` | Создать ссылку |
| GET | `/api/links?page=0&size=20` | Список своих ссылок |
| GET | `/api/links/{id}` | Получить ссылку |
| DELETE | `/api/links/{id}` | Удалить |
| GET | `/api/links/{id}/stats` | Статистика |

### Redirect (публично)

| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/{code}` | 302 redirect на оригинальный URL |

Swagger: http://localhost:8080/swagger-ui.html

## Как запустить

### Через Docker Compose (рекомендуется)

```bash
docker compose up --build