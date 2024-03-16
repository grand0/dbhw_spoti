```
 /) /)
( • ༝•)  bnuuy
```
Приложение создано для ДЗ№4 по БД. Развернуто и доступно по ссылке https://db.bnuuy.tech/.

Далеко не лучшее по архитектуре, но целью было не это.

## Стек
- язык [Kotlin](https://kotlinlang.org)
- бэкэнд-фреймворк [Ktor](https://ktor.io)
- ORM-фреймворк [Exposed](https://github.com/JetBrains/Exposed)
- шаблонизатор [Kotlin HTML DSL](https://github.com/kotlin/kotlinx.html)
- БД [PostgreSQL](https://www.postgresql.org/)
- [Spotify Web API](https://developer.spotify.com/documentation/web-api)
  - библиотека-враппер https://github.com/spotify-web-api-java/spotify-web-api-java
- [Docker](https://www.docker.com/)

## Функционал
- Просмотр всех сохраненных в БД треков (>12000 треков), реализована пагинация.
- Поиск треков среди всех сохраненных в БД.
- OAuth авторизация в Spotify, что открывает следующий функционал:
  - просмотр плейлистов по их id (даже приватных пользователя), автоматическое добавление всех треков из плейлиста в БД;
  - просмотр сохраненных треков пользователя, автоматическое добавление всех треков в БД.

## Скриншоты
### Главная страница (просмотр треков сохранённых в БД)
![image](https://github.com/grand0/spotify_db/assets/53438383/273e3206-8935-4081-bfc9-1cb09ba4e557)

### Поиск по трекам, сохранённым в БД
![image](https://github.com/grand0/spotify_db/assets/53438383/e98e63bd-f67c-4370-9d2d-37e79a20ecb1)

### Просмотр плейлиста
![image](https://github.com/grand0/spotify_db/assets/53438383/111636ae-9746-4a86-a99d-5d985263ce9b)

### Просмотр сохраненных треков
![image](https://github.com/grand0/spotify_db/assets/53438383/58cc6cb0-2ef9-44e5-a726-0455ff1f3f38)
