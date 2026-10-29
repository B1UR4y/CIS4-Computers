# Компьютеры (Spring JDBC, PostgreSQL, консольное приложение)

Консольное Java-приложение для работы с таблицей `computers` в базе данных PostgreSQL.
Построено на Spring Framework (без Spring Boot): внедрение зависимостей через аннотации и
класс-конфигурацию, доступ к БД через `JdbcTemplate` (модуль `spring-jdbc`).

Возможности:

- добавление компьютера (поля вводятся с клавиатуры);
- вывод всех записей таблицы;
- редактирование записи по id;
- удаление записи по id;
- поиск по цене (диапазон "от" и "до").

## Сущность

Сущность `Computer` описывает компьютер и соответствует таблице `computers`.

- `id` (столбец `id`, BIGINT, первичный ключ) - идентификатор, выдается базой данных;
- `brand` (столбец `brand`, VARCHAR(50)) - производитель, текст;
- `model` (столбец `model`, VARCHAR(100)) - модель, текст;
- `cpu` (столбец `cpu`, VARCHAR(100)) - процессор, текст;
- `type` (столбец `type`, VARCHAR(30)) - тип: laptop, desktop, all-in-one, текст;
- `ramGb` (столбец `ram_gb`, INTEGER) - объем оперативной памяти в гигабайтах, число;
- `price` (столбец `price`, NUMERIC(12,2)) - цена, число.

## Структура проекта

```
CIS4/
  pom.xml                          сборка Maven (Spring, spring-jdbc, драйвер PostgreSQL)
  README.md                        эта инструкция
  db.properties.example            образец файла с параметрами подключения к БД
  db.properties                    ваш файл с параметрами подключения (создается в шаге 4, не попадает в git)
  sql/
    schema.sql                     создание таблицы computers
    data.sql                       необязательные тестовые данные
  src/main/java/com/example/computer/
    Main.java                      точка входа, запуск контекста Spring
    AppConfig.java                 класс-конфигурация: DataSource, JdbcTemplate, сканирование пакета
    Computer.java                  сущность
    ComputerDao.java               интерфейс доступа к данным
    JdbcComputerDao.java           реализация на JdbcTemplate (SQL-запросы)
    ComputerService.java           проверка данных, бизнес-логика
    ConsoleMenu.java               консольный интерфейс
```

Слои приложения: `ConsoleMenu` -> `ComputerService` -> `ComputerDao` (`JdbcComputerDao`) -> PostgreSQL.
Все объекты создает и связывает Spring (`@Component`, `@Service`, `@Repository`, `@Autowired`).

## Что нужно установить

- JDK 17 или новее (проект собран и проверен на JDK 23);
- Apache Maven 3.9 или новее;
- PostgreSQL (проверено на версии 18; утилита `psql` должна быть доступна из командной строки);
- доступ в интернет при первой сборке: Maven скачает Spring и драйвер PostgreSQL.

Ссылки на загрузку:

- JDK: https://www.oracle.com/java/technologies/downloads/
- Maven: https://maven.apache.org/download.cgi
- PostgreSQL: https://www.postgresql.org/download/

Проверка установленных программ (в командной строке):

```cmd
java -version
mvn -version
psql --version
```

Если `psql` не найден в Windows, добавьте в переменную среды `PATH` каталог `bin` установленного
PostgreSQL, например `C:\Program Files\PostgreSQL\18\bin`, и откройте новое окно командной строки.
В Linux и macOS клиент `psql` обычно ставится вместе с пакетом PostgreSQL (или отдельным пакетом
`postgresql-client`) и сразу доступен в `PATH`.

## Развертывание

Все команды выполняются в командной строке. Команды, которые отличаются в разных системах, даны
в двух вариантах: для Windows (cmd) и для Linux / macOS (bash). Выберите свой и скопируйте целиком.
Остальные команды одинаковы везде.

### Шаг 1. Создать базу данных

Служба PostgreSQL должна быть запущена. Команда попросит пароль пользователя `postgres`
(он задавался при установке PostgreSQL):

```cmd
psql -U postgres -h localhost -p 5432 -c "CREATE DATABASE computers_db"
```

Ожидаемый ответ: `CREATE DATABASE`.

### Шаг 2. Создать таблицу

Выполнить из корня проекта (каталог `CIS4`).

Windows:

```cmd
psql -U postgres -h localhost -p 5432 -d computers_db -f sql\schema.sql
```

Linux / macOS:

```bash
psql -U postgres -h localhost -p 5432 -d computers_db -f sql/schema.sql
```

Ожидаемый ответ: `CREATE TABLE`.

### Шаг 3. Загрузить тестовые данные (необязательно)

Windows:

```cmd
psql -U postgres -h localhost -p 5432 -d computers_db -f sql\data.sql
```

Linux / macOS:

```bash
psql -U postgres -h localhost -p 5432 -d computers_db -f sql/data.sql
```

Ожидаемый ответ: `INSERT 0 5`.

Проверить содержимое таблицы можно так:

```cmd
psql -U postgres -h localhost -p 5432 -d computers_db -c "SELECT * FROM computers"
```

### Шаг 4. Указать параметры подключения

Параметры подключения к базе данных программа читает из файла `db.properties` в **текущем каталоге**,
то есть в том, из которого запускается `java -jar` (в шаге 6 это корень проекта). В репозитории лежит
только образец `db.properties.example`, а ваш файл с паролем в репозиторий не попадает
(`db.properties` указан в `.gitignore`).

Создайте файл из образца и впишите в него свой пароль.

Windows:

```cmd
copy db.properties.example db.properties
notepad db.properties
```

Linux / macOS:

```bash
cp db.properties.example db.properties
nano db.properties
```

Содержимое файла:

```properties
db.url=jdbc:postgresql://localhost:5432/computers_db
db.username=postgres
db.password=your_password_here
```

Замените `your_password_here` на пароль пользователя `postgres`. Хост, порт, имя базы и пользователя
менять нужно, только если вы создавали базу с другими параметрами.

Файл читается при каждом запуске, поэтому после смены настроек пересобирать проект не нужно.

### Шаг 5. Собрать проект

Выполнить из корня проекта:

```cmd
mvn clean package
```

В конце должно появиться `BUILD SUCCESS`, а в каталоге `target` - файл `CIS4-1.0-SNAPSHOT.jar`
(один jar со всеми зависимостями).

В проекте указана версия Java 23 (как в предыдущих работах). Если у вас установлена другая версия JDK
(не ниже 17), укажите ее при сборке:

```cmd
mvn clean package -Dmaven.compiler.release=17
```

### Шаг 6. Запустить приложение

Запускать нужно из корня проекта, где лежит `db.properties`.

Windows:

```cmd
java -jar target\CIS4-1.0-SNAPSHOT.jar
```

Linux / macOS:

```bash
java -jar target/CIS4-1.0-SNAPSHOT.jar
```

При успешном подключении программа напишет `Connected to the database. Records in table: N`
и покажет меню.

## Использование

Меню:

```
===== Computers =====
1. Add a computer
2. Show all computers
3. Edit a computer by id
4. Delete a computer by id
5. Search by price
0. Exit
```

Нужный пункт выбирается вводом цифры и нажатием Enter. Интерфейс программы на английском языке,
потому что консоль Windows (cmd) по умолчанию не использует UTF-8. Значения полей (бренд, модель
и т.д.) тоже рекомендуется вводить латиницей.

Ниже показаны примеры работы на тестовых данных из `sql/data.sql`. Введенные пользователем
значения отмечены знаком `<-` справа (в самой программе этого знака нет).

### Вывод всех записей (пункт 2)

```
Your choice: 2
--- All computers ---
ID    BRAND        MODEL                CPU                      TYPE          RAM GB          PRICE
1     Lenovo       ThinkPad T14         Intel Core i5-1235U      laptop            16       89990.00
2     Apple        iMac 24              Apple M3                 all-in-one        16      159990.00
3     HP           ProDesk 400 G9       Intel Core i3-12100      desktop            8       45500.00
4     ASUS         ROG Strix G16        Intel Core i7-13650HX    laptop            32      139900.50
5     Dell         OptiPlex 7010        Intel Core i5-13500      desktop           16       72300.00
```

### Поиск по цене (пункт 5)

Нужно ввести нижнюю и верхнюю границы цены (границы включаются в результат):

```
Your choice: 5
--- Search by price ---
Minimum price: 50000            <- ввод
Maximum price: 100000           <- ввод
Found: 2
ID    BRAND        MODEL                CPU                      TYPE          RAM GB          PRICE
5     Dell         OptiPlex 7010        Intel Core i5-13500      desktop           16       72300.00
1     Lenovo       ThinkPad T14         Intel Core i5-1235U      laptop            16       89990.00
```

Если максимум меньше минимума, программа сообщит об этом и повторит вопрос про максимум
(введенный минимум сохраняется).

### Добавление записи (пункт 1)

Дробная часть цены может отделяться и точкой, и запятой. Неверный ввод описан в разделе
"Обработка неверного ввода" ниже: вопрос повторяется, а уже введенные поля не теряются.

```
Your choice: 1
--- Add a computer ---
Brand: MSI                                            <- ввод
Model: Modern 15                                      <- ввод
CPU: Intel Core i5-1235U                              <- ввод
Type: laptop                                          <- ввод
RAM, GB: abc                                          <- ввод
Enter a whole number.
RAM, GB: 16                                           <- ввод
Price: 54990,90                                       <- ввод
Added. New id = 6
```

### Редактирование записи по id (пункт 3)

Программа показывает текущую запись и по очереди спрашивает новые значения полей. Текущее значение
показано в квадратных скобках; чтобы оставить его без изменений, достаточно нажать Enter.

```
Your choice: 3
--- Edit a computer ---
Id of the computer to edit (Enter to cancel): 6       <- ввод
Current record:
ID    BRAND        MODEL                CPU                      TYPE          RAM GB          PRICE
6     MSI          Modern 15            Intel Core i5-1235U      laptop            16       54990.90
Press Enter to keep the current value.
Brand [MSI]:                                          <- Enter
Model [Modern 15]:                                    <- Enter
CPU [Intel Core i5-1235U]:                            <- Enter
Type [laptop]:                                        <- Enter
RAM, GB [16]: 32                                      <- ввод
Price [54990.90]:                                     <- Enter
Updated.
```

Если записи с таким id нет, будет выведено `No computer with id N. Try again.` и id запросят
снова. Пустой ввод (Enter) отменяет операцию и возвращает в меню.

### Удаление записи по id (пункт 4)

```
Your choice: 4
--- Delete a computer ---
Id of the computer to delete (Enter to cancel): 2     <- ввод
Deleted.
```

Если записи с таким id нет, будет выведено `No computer with id N. Try again.` и id запросят
снова. Пустой ввод (Enter) отменяет операцию и возвращает в меню.


## Как начать заново

Удалить таблицу и создать ее снова (все данные будут потеряны).

Windows:

```cmd
psql -U postgres -h localhost -p 5432 -d computers_db -c "DROP TABLE computers"
psql -U postgres -h localhost -p 5432 -d computers_db -f sql\schema.sql
```

Linux / macOS:

```bash
psql -U postgres -h localhost -p 5432 -d computers_db -c "DROP TABLE computers"
psql -U postgres -h localhost -p 5432 -d computers_db -f sql/schema.sql
```

Удалить всю базу данных:

```cmd
psql -U postgres -h localhost -p 5432 -c "DROP DATABASE computers_db"
```
