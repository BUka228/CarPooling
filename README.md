# Приложение для совместных поездок на автомобилях

Это приложение позволяет пользователям организовывать совместные поездки на автомобилях. Оно предоставляет функциональность для регистрации пользователей, создания поездок, бронирования мест, оценки поездок и управления данными.

## Основные функции

- **Регистрация и авторизация пользователей**
- **Создание и управление поездками**
- **Бронирование мест в поездках**
- **Оценка поездок**
- **Управление маршрутами**
- **Поддержка различных типов хранилищ данных (XML, CSV, MongoDB, PostgreSQL)**

## Команды CLI

### 1. Регистрация пользователя

*register -n <имя> -e <email> -p <пароль> -g <пол> -ph <телефон> -b <дата_рождения> -a <адрес> [-pr <предпочтения>]*

**Описание:** Регистрирует нового пользователя.


#### Параметры: ####

-n, --name: Имя пользователя.

-e, --email: Email пользователя.

-p, --password: Пароль пользователя.

-g, --gender: Пол пользователя.

-ph, --phone: Телефон пользователя.

-b, --birthDate: Дата рождения пользователя (гггг-ММ-дд).

-a, --address: Адрес пользователя.

-pr, --preferences: Предпочтения пользователя (опционально).

### 2. Авторизация пользователя

*login -e <email> -p <пароль>*

**Описание:** Авторизует пользователя.


#### Параметры: ####

-e, --email: Email пользователя.

-p, --password: Пароль пользователя.

### 3. Создание поездки

*createTrip -d <время_отправления> -m <макс_пассажиров> -s <начальная_точка> -e <конечная_точка>*

**Описание:** Создает новую поездку.


#### Параметры: ####

-d, --departureTime: Время отправления (гггг-ММ-дд ЧЧ:мм:сс).

-m, --maxPassengers: Максимальное количество пассажиров.

-s, --startPoint: Начальная точка маршрута.

-e, --endPoint: Конечная точка маршрута.

### 4. Бронирование места

*bookSeat -t <id_поездки> -s <количество_мест> -p <номер_паспорта> -e <срок_паспорта>*

**Описание:** Бронирует место в поездке.


#### Параметры: ####

-t, --tripId: ID поездки.

-s, --seatCount: Количество мест.

-p, --passportNumber: Номер паспорта.

-e, --passportExpiryDate: Дата окончания срока паспорта (гггг-ММ-дд).

### 5. Оценка поездки

*rateTrip -t <id_поездки> -r <рейтинг> [-c <комментарий>]*

**Описание:** Оценивает поездку.


#### Параметры: #### 

-t, --tripId: ID поездки.

-r, --rating: Рейтинг (от 1 до 5).

-c, --comment: Комментарий (опционально).

### 6. Выбор типа хранилища

*setStorage -t <тип_хранилища>*

**Описание:** Устанавливает тип хранилища данных.


#### Параметры: ####

*-t, --type: Тип хранилища (XML, CSV, MONGO, POSTGRES).*

## Диаграммы: 

### Диаграмма классов

![Диаграмма классов](https://www.plantuml.com/plantuml/png/SoWkIImgAStDuG8oIb8Lb1qioIZAJ2ejICmB2VKl1QWM0000)

![Диаграмма классов]([//www.plantuml.com/plantuml/png/TLJ1RjD04BtxArOvKhM5gm-eGlm1Ns2e1PPSHuhD0I6as444gKWLdAk6yWMMoLB7sEONPl-8jxCcRDPnSeXtzisRcVSclaYpO96zlOgzLt6Gfe8srDDlzOKUy5jHgGgI-YpUUmAVa-XI-29ACdEYcbfrWyYBR14bhqqml0gYs8dH7r0j3VNu2c4dQCgIL1znsRaPM-whfE-u_8MJf8vgxkDva8K3gAGjAagLZ-hfCB9GLwrLOMKFi0-UBOQa1u0RAYSag9WECj0tTJsWIxiLQj5Bet8MQuNJ1ktdE9TelcPLAb-yV3V0zzFTuuB9J7yM8x8B3L8hbJkwyMXShz3SQiMzNTFE5y_cypWC4d5-BgXYxfRrwwZNX336oURE_fMtLBdLNghFcbqL4wfqmAofr7xpymtzq7ApZeuVZROOHgrTu8JjjhQ0Vc83-ttwXllUwgLYVOS5_Z-cBz8tyqLc3VILKxh3aKbEZDOmTOs2KXEobhVcMLW-djWkMQFO5hk5NQkDaZsCaa3mtl3QBdNsSSexeoUBpsHqp-JGSqp0PQ33Neax8-VqklEAxFHw0FLpf4kBB_9vy2Kmk-CUMgKdNpYdfq_5xF7PsQEPyp_q3ey6-8I1KquUJvavg-oYh0viORoBCBd46_Cl](https://www.plantuml.com/plantuml/png/TLJ1RjD04BtxArOvKhM5gm-eGlm1Ns2e1PPSHuhD0I6as444gKWLdAk6yWMMoLB7sEONPl-8jxCcRDPnSeXtzisRcVSclaYpO96zlOgzLt6Gfe8srDDlzOKUy5jHgGgI-YpUUmAVa-XI-29ACdEYcbfrWyYBR14bhqqml0gYs8dH7r0j3VNu2c4dQCgIL1znsRaPM-whfE-u_8MJf8vgxkDva8K3gAGjAagLZ-hfCB9GLwrLOMKFi0-UBOQa1u0RAYSag9WECj0tTJsWIxiLQj5Bet8MQuNJ1ktdE9TelcPLAb-yV3V0zzFTuuB9J7yM8x8B3L8hbJkwyMXShz3SQiMzNTFE5y_cypWC4d5-BgXYxfRrwwZNX336oURE_fMtLBdLNghFcbqL4wfqmAofr7xpymtzq7ApZeuVZROOHgrTu8JjjhQ0Vc83-ttwXllUwgLYVOS5_Z-cBz8tyqLc3VILKxh3aKbEZDOmTOs2KXEobhVcMLW-djWkMQFO5hk5NQkDaZsCaa3mtl3QBdNsSSexeoUBpsHqp-JGSqp0PQ33Neax8-VqklEAxFHw0FLpf4kBB_9vy2Kmk-CUMgKdNpYdfq_5xF7PsQEPyp_q3ey6-8I1KquUJvavg-oYh0viORoBCBd46_Cl))

### Диаграмма вариантов использования

@startuml
left to right direction

actor Пользователь
actor Система

Пользователь --> (Поиск попутчика)
Пользователь --> (Создание поездки)
(Создание поездки) .left.> (Отправка и получение сообщений) : Include

Пользователь --> (Бронирование места)
(Бронирование места) .up.> (Отмена брони) : Include
(Бронирование места) <.. (Оплата поездки) : Extend
(Бронирование места) .right.> (Отправка и получение сообщений) : Include
(Бронирование места) <|-- (Бронирование одного места)
(Бронирование места) <|-- (Бронирование нескольких мест)

Пользователь --> (Оценка поездки)
Пользователь --> (Просмотр истории поездок)
Пользователь --> (Управление профилем)

Пользователь --> (Управление поездкой)
(Управление поездкой) ..> (Удаление поездки) : Include
(Управление поездкой) ..> (Редактирование поездки) : Include

(Поиск попутчика) <-- Система
(Создание поездки) <-- Система
(Бронирование места) <-- Система
(Оценка поездки) <-- Система
(Просмотр истории поездок) <-- Система
(Управление профилем) <-- Система
(Управление поездкой) <-- Система
@enduml

## Установка и запуск
Убедитесь, что у вас установлена Java (версия 17 или выше).

Скачайте или клонируйте репозиторий.

Соберите проект с помощью Maven:

*mvn clean install*

Запустите приложение:

*java -jar target/carpooling-cli.jar*
## Лицензия
Этот проект распространяется под лицензией MIT. Подробности см. в файле LICENSE.

## Контакты
Если у вас есть вопросы или предложения, свяжитесь с автором:

**Email:** manachinsky88@gmail.com

**GitHub:** Buka228

---

### Итог

Этот `README.md` файл содержит:
1. **Описание проекта** и его основные функции.
2. **Команды CLI** с подробным описанием и примерами использования.
3. **Диаграммы** (классов, вариантов использования и базы данных) для наглядного представления структуры проекта.
4. **Инструкции по установке и запуску**.
5. **Лицензию** и контактную информацию.
