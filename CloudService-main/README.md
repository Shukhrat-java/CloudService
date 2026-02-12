?? Cloud Storage Service
? О проекте
Cloud Storage Service — это полнофункциональное веб-приложение для облачного хранения файлов, разработанное в рамках дипломного проекта. Сервис предоставляет REST API для интеграции с фронтендом и позволяет пользователям безопасно хранить, скачивать, удалять и управлять своими файлами в облаке.

? Функциональность
? Регистрация и авторизация с использованием JWT токенов

? Загрузка файлов на сервер

? Скачивание файлов из облака

? Удаление файлов

? Переименование файлов

? Просмотр списка файлов с пагинацией

? Разграничение доступа — каждый пользователь видит только свои файлы

? Безопасное хранение паролей (BCrypt)

? Docker-контейнеризация всех компонентов

? Технологический стек
Бэкенд:

Java 17

Spring Boot 3.3.0

Spring Security + JWT

Spring Data JPA

PostgreSQL 15

Maven

Docker

Фронтенд:

Vue.js 3

TypeScript

Vuex

Axios

Vue Router

? Быстрый старт
? Предварительные требования
Установленный Docker Desktop

Установленный Java 17+

Установленный Maven

Установленный Node.js 18+ (для локального запуска фронтенда)

Git

? Пошаговая инструкция по запуску
1. Клонирование репозитория
   bash
   git clone <url-репозитория>
   cd CloudService-main
2. Запуск бэкенда (Docker)
   bash
# Сборка проекта
mvn clean package -DskipTests

# Запуск контейнеров (PostgreSQL + Spring Boot)
docker-compose up --build -d

# Проверка логов
docker logs cloudapp -f
Ожидаемый результат:

text
Started CloudApplication in X.XXX seconds
Tomcat started on port 8081 (http) with context path '/'
3. Создание тестового пользователя
   bash
   curl -X POST http://localhost:8081/test/create-test-user
   Ответ:

json
{
"username": "testuser",
"message": "Test user created successfully",
"role": "ROLE_USER"
}
4. Запуск фронтенда
   Вариант А: Локальный запуск (рекомендуется)
   bash
   cd C:\Users\<ваш-пользователь>\Downloads\netology-diplom-frontend

# Создание .env файла
echo "VUE_APP_BASE_URL=http://localhost:8081/" > .env

# Установка зависимостей
npm install

# Запуск
npm run serve
Фронтенд будет доступен: http://localhost:8080

Вариант Б: Запуск в Docker
bash
docker-compose -f docker-compose.frontend.yml up -d
? Использование
Вход в систему
Логин: testuser
Пароль: password123

Или создайте собственного пользователя через тестовый эндпоинт.

Тестирование API через curl
1. Получение токена:

bash
curl -X POST http://localhost:8081/login \
-H "Content-Type: application/json" \
-d '{"login":"testuser","password":"password123"}'
2. Загрузка файла:

bash
curl -X POST http://localhost:8081/file?filename=test.txt \
-H "auth-token: Bearer <ваш-токен>" \
-H "Content-Type: multipart/form-data" \
-F "file=@/path/to/file.txt"
3. Получение списка файлов:

bash
curl -X GET http://localhost:8081/list?limit=10 \
-H "auth-token: Bearer <ваш-токен>"
4. Скачивание файла:

bash
curl -X GET http://localhost:8081/file?filename=test.txt \
-H "auth-token: Bearer <ваш-токен>" \
--output downloaded.txt
5. Удаление файла:

bash
curl -X DELETE http://localhost:8081/file?filename=test.txt \
-H "auth-token: Bearer <ваш-токен>"
? Docker-команды
Управление контейнерами
bash
# Запуск всех сервисов
docker-compose up -d

# Просмотр логов
docker logs cloudapp -f
docker logs postgres-dev2

# Остановка с удалением данных
docker-compose down -v

# Пересборка с нуля
docker-compose down -v
mvn clean package -DskipTests
docker-compose up --build -d
Доступ к базе данных
bash
docker exec -it postgres-dev2 psql -U postgres -d clouddb
? Диагностика проблем
Бэкенд не запускается
bash
# Проверка логов
docker logs cloudapp

# Проверка, что порт свободен
netstat -ano | findstr :8081
Фронтенд не подключается
Проверьте .env файл:

bash
cat .env
# Должно быть: VUE_APP_BASE_URL=http://localhost:8081
Проверьте CORS-настройки в браузере (F12 ? Console)

401 Unauthorized при логине
bash
# Проверьте, существует ли пользователь
curl http://localhost:8081/test/users

# Создайте нового пользователя
curl -X POST http://localhost:8081/test/create-test-user
? Структура проекта
text
CloudService-main/
??? src/
?   ??? main/
?   ?   ??? java/ru/netology/
?   ?   ?   ??? controller/     # REST-контроллеры
?   ?   ?   ??? service/        # Бизнес-логика
?   ?   ?   ??? repository/     # Работа с БД
?   ?   ?   ??? entity/         # JPA-сущности
?   ?   ?   ??? dto/           # Data Transfer Objects
?   ?   ?   ??? security/      # JWT и Security
?   ?   ?   ??? exception/     # Обработка ошибок
?   ?   ??? resources/
?   ?       ??? application.yml
?   ?       ??? data.sql
??? Dockerfile
??? docker-compose.yml
??? pom.xml
? Тестовые учетные записи
Логин	Пароль	Роль
testuser	password123	ROLE_USER
admin	admin123	ROLE_ADMIN
user	user123	ROLE_USER
? API Endpoints
Метод	URL	Описание
POST	/login	Авторизация
POST	/logout	Выход
GET	/list?limit={n}	Список файлов
POST	/file?filename={name}	Загрузка файла
GET	/file?filename={name}	Скачивание файла
PUT	/file?filename={name}	Переименование
DELETE	/file?filename={name}	Удаление файла
POST	/test/create-test-user	Создать тестового пользователя
? Решенные проблемы
В ходе разработки были решены следующие сложные задачи:

? Настройка CORS для взаимодействия фронтенда и бэкенда

? Конфигурация JWT с безопасным ключом шифрования

? Устранение конфликта двух UserDetailsService в Spring Security

? Проблема с URL (двойной / в запросах)

? StackOverflowError в AuthenticationManager

? WeakKeyException в JWT (ключ увеличен до 256 бит)

? Docker-контейнеризация с томом для PostgreSQL

??? Автор
Студент: С. Абдуллаев
Курс: Java-разработчик
Проект: Дипломная работа "Облачное хранилище"

? Лицензия
Проект выполнен в учебных целях в рамках дипломной работы.
