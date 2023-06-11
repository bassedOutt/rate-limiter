# Обмеження швикдості HTTP-запитів
Процедура встановлення включає наступні кроки:
1. Встановити Maven - [детальна інструкція по встановленні](https://maven.apache.org/install.html)
2. Встановити JDK - [детальна інструкція по встановленні](https://docs.oracle.com/en/java/javase/11/install/installation-jdk-microsoft-windows-platforms.html#GUID-61460339-5500-40CC-9006-D4FC3FBCFC0D)
3. Склонувати проєкт за допомогою команди `git pull` або завантажити вихідний код у форматі zip
4. Встановити MongoDb - завантажити з [офіційного сайту](https://www.mongodb.com/try/download/community)
5. Встановити Redis - [детальна інструкція по встановленні](https://redis.io/download/)
6. Перевірити налаштування підключення до Redis та Mongodb у файлі `application.yml` підпроєкта `rate-limiter-example`
7. Запустити Redis командою `redis server`
8. Переконатися, що Mongodb запущена
9. Скомпілювати проєкт командою `mvn clean install`
10. Запустити сервер за допомогою команди `mvn spring-boot:run -pl rate-limiter-example`

```xml
<dependency>
    <groupId>com.ratelimiter</groupId>
    <artifactId>rate-limiter-core</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```
Для того, щоб використати бібліотеку у вашому проєкті, вам необхідно створити об'єкт класу `ReactiveRateLimiter`. Для цього можна скористатися фабриками - `RedisRateLimiterFactory` або `RedisClusterRateLimiterFactory`.

У проєкті `rate-limiter-example` наведені 2 методи налаштувань - за допомогою `application.yml` та анотації `@Ratelimited`. Ви можете скористатися одним із цих 2-ох методів або реалізувати власний.
