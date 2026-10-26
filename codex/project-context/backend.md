# HLT: контекст backend

Этот файл — краткая карта backend для разработки Android-клиента. Он составлен по состоянию исходного кода 26 октября 2026 года и должен обновляться вместе с изменением контрактов. Репозиторий HLT является внешним по отношению к Android-приложению: не изменять его без отдельного запроса владельца.

## Сервисы и маршрутизация

| Сервис | Стек | Назначение | Состояние для Android |
|---|---|---|---|
| `auth` | Java, Spring WebFlux, PostgreSQL | регистрация, логин, обновление и проверка JWT | можно подключать |
| `chat` | Go, PostgreSQL, WebSocket | создание чата, отправка событий в реальном времени | неполный контракт |
| `user` | Java, Spring | таблица пользователей | HTTP API отсутствует |
| `worker` | Go, gRPC | внутренняя служба | не вызывается Android-клиентом |

Публичный домен: `https://hlt.com`. Ingress публикует `/api/auth` без авторизации; маршруты `/api/chat`, `/api/event`, `/api/user` требуют JWT. Авторизация на ingress передаёт в сервис заголовок `X-User-Id` после проверки access token.

## Контракт auth

Все тела запросов и ответов — JSON.

| Операция | Метод и путь | Запрос | Успех |
|---|---|---|---|
| Регистрация | `POST /api/auth/register` | `username`, `email`, `password` | `201`: `username`, `email` |
| Вход | `POST /api/auth/login` | `email`, `password` | `200`: `accessToken`, `refreshToken` |
| Обновление сессии | `POST /api/auth/refresh` | `refreshToken` | `200`: `accessToken`, `refreshToken` |
| Проверка access token | `GET /api/auth/validate` | `Authorization: Bearer <token>` | `200`, заголовок `X-User-Id` |

Ошибки валидации и бизнес-ошибки сервиса сейчас возвращаются как `400` с телом `{ "errorText": "..." }`. Недействительный access token при `validate` возвращает `401`.

Для Android access token используется в заголовке `Authorization: Bearer <token>`. Refresh token хранится локально в защищённом хранилище и никогда не логируется.

## Chat: текущее состояние

В исходниках есть `POST /api/chats` с телом `{ "member_ids": ["uuid"] }` и `POST /api/messages` с телом `{ "room_id": "uuid", "text": "..." }`; пользователь определяется из `X-User-Id`. WebSocket: `/api/chat/ws`; событие нового сообщения имеет вид:

```json
{
  "type": "new_message",
  "room_id": "uuid",
  "payload": { "text": "..." }
}
```

Эти HTTP-пути пока не совпадают с опубликованным ingress-префиксом `/api/chat`; история и список чатов отсутствуют. Отправка сообщения транслирует его по WebSocket, но не сохраняет в таблицу `messages`. Детальные требования зафиксированы в `../backend-requirements/README.md`.

## Безопасность и ограничения

- Android-клиент не передаёт `X-User-Id` самостоятельно: его должен добавлять ingress после успешной проверки JWT.
- Перед началом интеграции проверяем доступность TLS-адреса `hlt.com` с физического устройства/эмулятора.
- В документации не хранятся секреты, пароли, токены или значения из `.env`/Kubernetes Secret.
