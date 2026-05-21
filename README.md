# My Blog — Backend

Бэкенд веб-приложения блога.

## Стек

- **Java** 21
- **Spring Framework** 6.1+
- Сервлет-контейнер: Tomcat 
- Сборка: Maven
- БД: PostgreSQL, H2 (in-memory)

## REST API

Бэкенд получает запросы от фронтенда по REST, обрабатывает их и возвращает ответ.

---

### Посты

#### Получение списка постов

```
GET /api/posts?search=Lalala&pageNumber=1&pageSize=5
```

Параметры (все обязательные):
- `search` — строка поиска
- `pageNumber` — номер текущей страницы
- `pageSize` — число постов на странице

Ответ:

```json
{
  "posts": [
    {
      "id": 1,
      "title": "Название поста 1",
      "text": "Текст поста в формате Markdown...",
      "tags": ["tag_1", "tag_2"],
      "likesCount": 5,
      "commentsCount": 1
    },
    {
      "id": 2,
      "title": "Название поста 2",
      "text": "Текст поста в формате Markdown...",
      "tags": [],
      "likesCount": 1,
      "commentsCount": 5
    }
  ],
  "hasPrev": true,
  "hasNext": false,
  "lastPage": 3
}
```

Поля ответа:
- `posts` — список постов
- `hasPrev` — `true`, если текущая страница не первая
- `hasNext` — `true`, если текущая страница не последняя
- `lastPage` — номер последней страницы
- `id` — идентификатор поста
- `title` — название поста
- `text` — текст поста в формате Markdown; если длиннее 128 символов, обрезается до 128 и добавляется «…»
- `tags` — список тегов
- `likesCount` — число лайков
- `commentsCount` — число комментариев

---

#### Получение поста

```
GET /api/posts/{id}
```

Ответ:

```json
{
  "id": 1,
  "title": "Название поста 1",
  "text": "Текст поста в формате Markdown...",
  "tags": ["tag_1", "tag_2"],
  "likesCount": 5,
  "commentsCount": 1
}
```

Поле `text` не обрезается.

---

#### Добавление поста

```
POST /api/posts
```

Тело запроса:

```json
{
  "title": "Название поста 3",
  "text": "Текст поста в формате Markdown...",
  "tags": ["tag_1", "tag_2"]
}
```

Ответ — созданный пост; `likesCount` и `commentsCount` всегда равны `0`.

```json
{
  "id": 3,
  "title": "Название поста 3",
  "text": "Текст поста в формате Markdown...",
  "tags": ["tag_1", "tag_2"],
  "likesCount": 0,
  "commentsCount": 0
}
```

---

#### Редактирование поста

```
PUT /api/posts/{id}
```

Тело запроса:

```json
{
  "id": 3,
  "title": "Название поста 3",
  "text": "Текст поста в формате Markdown...",
  "tags": ["tag_1", "tag_2"]
}
```

Ответ — обновлённый пост.

```json
{
  "id": 3,
  "title": "Название поста 3",
  "text": "Текст поста в формате Markdown...",
  "tags": ["tag_1", "tag_2"],
  "likesCount": 0,
  "commentsCount": 0
}
```

---

#### Удаление поста

```
DELETE /api/posts/{id}
```

Удаляет пост вместе со всеми его комментариями. Возвращает `200 OK`.

---

#### Лайк поста

```
POST /api/posts/{id}/likes
```

Добавляет `+1` к числу лайков. Возвращает обновлённое число лайков (число в теле ответа).

---

#### Обновление картинки поста

```
PUT /api/posts/{id}/image
```

Фронтенд присылает файл в формате `multipart/form-data`:

```
Content-Disposition: form-data; name="image"; filename="image_name.jpg"
```

Возвращает `200 OK`.

---

#### Получение картинки поста

```
GET /api/posts/{id}/image
```

Возвращает массив байт картинки в теле ответа.

---

### Комментарии

#### Получение комментариев поста

```
GET /api/posts/{postId}/comments
```

Ответ:

```json
[
  {
    "id": 1,
    "text": "Комментарий к посту 1",
    "postId": 1
  },
  {
    "id": 2,
    "text": "Ещё один комментарий к посту 1",
    "postId": 1
  }
]
```

---

#### Получение комментария

```
GET /api/posts/{postId}/comments/{commentId}
```

Ответ:

```json
{
  "id": 2,
  "text": "Ещё один комментарий к посту 1",
  "postId": 1
}
```

---

#### Добавление комментария

```
POST /api/posts/{postId}/comments
```

Тело запроса:

```json
{
  "text": "Комментарий к посту",
  "postId": 1
}
```

Ответ — созданный комментарий с присвоенным `id`.

```json
{
  "id": 2,
  "text": "Ещё один комментарий к посту 1",
  "postId": 1
}
```

---

#### Редактирование комментария

```
PUT /api/posts/{postId}/comments/{commentId}
```

Тело запроса:

```json
{
  "id": 2,
  "text": "Второй комментарий к посту 1",
  "postId": 1
}
```

Ответ — обновлённый комментарий.

```json
{
  "id": 2,
  "text": "Второй комментарий к посту 1",
  "postId": 1
}
```

---

#### Удаление комментария

```
DELETE /api/posts/{postId}/comments/{commentId}
```

Возвращает `200 OK`.
