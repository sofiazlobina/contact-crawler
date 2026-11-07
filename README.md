# Contact Crawler — Многопоточный парсер контактных данных

## Описание проекта
Веб-приложение на **Spring Boot** для автоматизированного сбора контактных данных (телефоны, email, названия компаний) с веб-сайтов. Приложение реализует параллельную обработку URL с использованием пула потоков, блокирующих очередей, семафоров и выделенного демон-потока для асинхронного логирования.

---

## Требования к системе
Для запуска проекта на любом компьютере вам потребуется:
1. **Java Development Kit (JDK)** версии 17 или выше.
2. **Git** (опционально, для клонирования репозитория).
3. *Maven устанавливать не нужно*, проект содержит встроенный Maven Wrapper.

---

## Быстрый запуск

### Шаг 1: Скачайте или клонируйте проект
```bash
git clone <ссылка-на-ваш-репозиторий>
cd contact-crawler
```

### Шаг 2: Убедитесь, что Java установлена
Откройте терминал и введите:
```bash
java -version
```

### Настройка Java (для Windows)

Чтобы проект запустился, нужно установить Java и указать системе путь к ней.

**1. Установите JDK 17**
Скачайте и установите JDK с сайта [adoptium.net](https://adoptium.net/). 
*(При установке обязательно поставьте галочку **"Add to PATH"**).*

**2. Настройте переменную JAVA_HOME**
1. Нажмите `Win + R`, введите `sysdm.cpl` и нажмите **Enter**.
2. Вкладка **Дополнительно** - кнопка **Переменные среды...**
3. В нижнем окне (Системные переменные) нажмите **Создать...**
4. Заполните поля:
   * Имя переменной: `JAVA_HOME`
   * Значение переменной: Путь к папке JDK (например, `C:\Program Files\Eclipse Adoptium\jdk-17...`)
5. Нажмите **ОК** во всех окнах.

**3. Проверьте и запустите**
**Важно:** Закройте все открытые терминалы и откройте новый!

Проверьте, что всё работает:
```powershell
echo %JAVA_HOME%
```

### Шаг 3: Запустите приложение
Используйте встроенный Maven Wrapper. Команда зависит от вашей операционной системы:

Для Windows (Command Prompt / PowerShell):
```powershell
.\mvnw.cmd spring-boot:run
```

Для macOS / Linux:
```bash
./mvnw spring-boot:run
```

##  Использование API
Откройте в браузере или выполните запрос:
```
curl http://localhost:8080/api/health
Ответ: Service is running!
```

## Запуск парсинга (POST запрос)
Отправьте список URL для обработки.

Пример тела запроса (JSON):
```json
{
  "urls": [
    "https://www.google.com/contact/",
    "https://example.com"
  ],
  "maxDepth": 5
}
```

Пример запуска в PowerShell (Windows):
```powershell
$body = '{"urls": ["https://www.google.com/contact/"], "maxDepth": 3}'
Invoke-RestMethod -Uri "http://localhost:8080/api/start" -Method Post -Body $body -ContentType "application/json"
```

Пример запуска в Bash (macOS/Linux):
```bash
curl -X POST http://localhost:8080/api/start \
     -H "Content-Type: application/json" \
     -d '{"urls": ["https://www.google.com/contact/"], "maxDepth": 3}'
```

## Получение результатов (GET запрос)
Получите сохраненные контакты с поддержкой пагинации, сортировки и фильтрации.

Примеры запросов:
```bash
Получить все записи: GET http://localhost:8080/api/answer
Найти по email и отсортировать по дате: GET http://localhost:8080/api/answer?email=google&sortBy=createdAt&sortDir=desc
```

## Просмотр базы данных (H2 Console)
Приложение использует встроенную базу данных H2. Чтобы посмотреть сохраненные данные:
1. Откройте в браузере: http://localhost:8080/h2-console
2. Введите параметры подключения:
   - JDBC URL: jdbc:h2:file:./data/contactdb
   - Username: sa
   - Password: (оставьте поле пустым)
   - Нажмите кнопку Connect.
   - Выполните SQL-запрос: SELECT * FROM contacts;


## Запуск тестов
Проект покрыт Unit-тестами. Чтобы их запустить, остановите приложение (Ctrl + C в терминале) и выполните:

Windows:
```powershell
.\mvnw.cmd clean test
```

macOS / Linux:
```bash
./mvnw clean test
```

**Ожидаемый результат в конце вывода:**
```text
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
...
[INFO] BUILD SUCCESS