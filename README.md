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

```bash 
register -n <имя> -e <email> -p <пароль> -g <пол> -ph <телефон> -b <дата_рождения> -a <адрес> [-pr <предпочтения>]
```

**Описание:** Регистрирует нового пользователя.

**Параметры:**

- `-n, --name` — Имя пользователя.
- `-e, --email` — Email пользователя.
- `-p, --password` — Пароль пользователя.
- `-g, --gender` — Пол пользователя.
- `-ph, --phone` — Телефон пользователя.
- `-b, --birthDate` — Дата рождения пользователя (гггг-ММ-дд).
- `-a, --address` — Адрес пользователя.
- `-pr, --preferences` — Предпочтения пользователя (опционально).

### 2. Авторизация пользователя

```bash
login -e <email> -p <пароль>
```

**Описание:** Авторизует пользователя.

**Параметры:**

- `-e, --email` — Email пользователя.
- `-p, --password` — Пароль пользователя.

### 3. Создание поездки

```bash
createTrip -d <дата_отправления> -t <время_отправления> -m <макс_пассажиров> -s <начальная_точка> -e <конечная_точка> 
```

**Описание:** Создает новую поездку.

**Параметры:**

- `-d, --departureDate` — Дата отправления (гггг-ММ-дд).
- `-t, --departureTime` — Время отправления (ЧЧ:мм:сс).
- `-m, --maxPassengers` — Максимальное количество пассажиров.
- `-s, --startPoint` — Начальная точка маршрута.
- `-e, --endPoint` — Конечная точка маршрута.

### 4. Бронирование места

 ```bash 
bookSeat -t <id_поездки> -s <количество_мест> -p <номер_паспорта> -e <срок_паспорта>
```

**Описание:** Бронирует место в поездке.

**Параметры:**

- `-t, --tripId` — ID поездки.
- `-s, --seatCount` — Количество мест.
- `-p, --passportNumber` — Номер паспорта.
- `-e, --passportExpiryDate` — Дата окончания срока паспорта (гггг-ММ-дд).

### 5. Оценка поездки

```bash 
rateTrip -t <id_поездки> -r <рейтинг> [-c <комментарий>]
```

**Описание:** Оценивает поездку.

**Параметры:** 

- `-t, --tripId` — ID поездки.
- `-r, --rating` — Рейтинг (от 1 до 5).
- `-c, --comment` — Комментарий (опционально).

### 6. Выбор типа хранилища

```bash 
setStorage -t <тип_хранилища>
```

**Описание:** Устанавливает тип хранилища данных.

**Параметры:**

- `-t, --type` — Тип хранилища (XML, CSV, MONGO, POSTGRES).

## Диаграммы

### Диаграмма классов

Диаграмма классов описывает структуру основных сущностей приложения и их взаимосвязи. Она включает следующие классы:

- **Пользователь:** Содержит информацию о пользователе (имя, email, пароль, телефон и т.д.).
- **Поездка:** Описывает поездку, включая время отправления, максимальное количество пассажиров и статус.
- **Маршрут:** Описывает маршрут поездки (начальная и конечная точки, дата и продолжительность).
- **Бронирование:** Содержит информацию о бронировании места в поездке (количество мест, статус, данные паспорта).
- **Оценка:** Содержит оценку и комментарий к поездке.

![Диаграмма классов](https://www.plantuml.com/plantuml/png/TLJ1RjD04BtxArOvKhM5gm-eGlm1Ns2e1PPSHuhD0I6as444gKWLdAk6yWMMoLB7sEONPl-8jxCcRDPnSeXtzisRcVSclaYpO96zlOgzLt6Gfe8srDDlzOKUy5jHgGgI-YpUUmAVa-XI-29ACdEYcbfrWyYBR14bhqqml0gYs8dH7r0j3VNu2c4dQCgIL1znsRaPM-whfE-u_8MJf8vgxkDva8K3gAGjAagLZ-hfCB9GLwrLOMKFi0-UBOQa1u0RAYSag9WECj0tTJsWIxiLQj5Bet8MQuNJ1ktdE9TelcPLAb-yV3V0zzFTuuB9J7yM8x8B3L8hbJkwyMXShz3SQiMzNTFE5y_cypWC4d5-BgXYxfRrwwZNX336oURE_fMtLBdLNghFcbqL4wfqmAofr7xpymtzq7ApZeuVZROOHgrTu8JjjhQ0Vc83-ttwXllUwgLYVOS5_Z-cBz8tyqLc3VILKxh3aKbEZDOmTOs2KXEobhVcMLW-djWkMQFO5hk5NQkDaZsCaa3mtl3QBdNsSSexeoUBpsHqp-JGSqp0PQ33Neax8-VqklEAxFHw0FLpf4kBB_9vy2Kmk-CUMgKdNpYdfq_5xF7PsQEPyp_q3ey6-8I1KquUJvavg-oYh0viORoBCBd46_Cl)

### Диаграмма вариантов использования

Диаграмма вариантов использования описывает основные функции приложения и взаимодействие пользователя с системой. Она включает следующие сценарии:

- **Регистрация и авторизация пользователя:** Пользователь может зарегистрироваться и войти в систему.
- **Создание поездки:** Пользователь может создать новую поездку, указав маршрут и другие параметры.
- **Бронирование места:** Пользователь может забронировать место в поездке.
- **Оценка поездки:** Пользователь может оставить оценку и комментарий после завершения поездки.
- **Управление профилем:** Пользователь может редактировать свои данные.
- **Управление поездкой:** Пользователь может редактировать или удалять созданные поездки.

![Диаграмма вариантов использования](https://www.plantuml.com/plantuml/png/hLLDJi905Dxt5BEqY_O0XXWsBZm6WQea2AOEYGiNY3_4n8qNG8aNA6XZRSjmXJStydiKcmOBKmmbQTCtt_VrVU-z6UfNik7B_aN7wdYdKiYUyDjdvrAqshxNbErUrxAiHbFsV46lf2ZHp_I5vum2FQIGt_Fr2KNwTXbDAR3usaGHZdCa3dYLAHGBMf2YXRxJGpsYY68AR1DvGWehdnJG78mmamWv17faMqQ4SDcnorfZQ2_q0CWPVrjGb84JP3G29UCZKuNVL3-jGj-sE1GdtMQdt_ACPb-WhvY69w_dMQKKSjdOSYMKSFkNUTO80u6Cfx_4GauLzMgkosfmd530eVLAGV7uMdhTLjK4UOJsLzcABcuSPrkP5SpCSVyWLUJjINtEK8fN4n03y93pRUDiZFNZqdZyj_R6FSIPuDCfxaCz4Aizg12EA2ggAOgDQkzvivAyEHo2tZrUE9Z2plza2huKEcnLn0dNpJInapVXYbE_e-uRWgmSmqFULkCtS1dEBr5pdBKpqNmcbPFC0rfAt3fNvOJTfgbSeyeCRQIQ-rPAhUDmmd_O3m00)

### Диаграмма базы данных

Диаграмма базы данных описывает структуру таблиц и их взаимосвязи в базе данных. Она включает следующие сущности:

- **Пользователь:** Таблица с данными о пользователях.
- **Поездка:** Таблица с данными о поездках, связанная с пользователями и маршрутами.
- **Маршрут:** Таблица с данными о маршрутах.
- **Бронирование:** Таблица с данными о бронированиях, связанная с поездками и пользователями.
- **Оценка:** Таблица с данными об оценках, связанная с поездками.

![Диаграмма базы данных](https://www.plantuml.com/plantuml/png/bLNHQjH057tFL-HT-WDIIX4Hn1V1BuWqQ62Rm7PyA4rXauWLAXHzBbth5uIOiTcuQN_XpXzvvawwZfDDP7SVDZFtddldd7CdUtWYWhduS3ppmbX4ujId9TtJR_M5xl1RKQboadhjdta-Fa-Y8_-f7yN2tzz__UhWm6nIGsjrXS0RCO_YTsOpF0wYcRD73wYuC1s68MmxcwOxIVKHmSv5r7oyahuZ-0ihIHqrV8xdWGXD4i8rEagL3VhfRQ3GLrset4BtQUUUBGhYTyXkgPoMekAoa8E-gSiULFvxf3KErQgCLIkmMe97XdK95WtBKm7mzfG1Dj5RXBZU5gAeaEiqLGoe6gBSDDSDLC4dlAGL4fyboImCOjk4eVil_r9zmLGVUQ3OPrrGvMwAfTwrdJUjNeVK2ofrJtw-eX_Q7fRTyQ_L8q4ImUb4evxZ0N8_iUFt6_oIbQ-r0En3Jvrpe2qFxCdxP2viQbyPr3tQPfmXEKsovHuDEajshSBwsZopxrEMEgIs7y2z0w00ioHIUDHuv1SQFbRrngZBLd0zkkNwKDSfuSZwO5yKPoHs6Z5Rv1lZrN_l7i_vIiyuad-Q14loDRuemvekaBCQORy38nnoz8MPfdjxoPdpxW1SPPjRB5NkEN636X-i7b-pcnZJqGgsQa013FRREfuZFqsTMye8TDgOfGh67eRn4Vuj_W00)

## Установка и запуск

1. Убедитесь, что у вас установлена Java (версия 17 или выше).
2. Скачайте или клонируйте репозиторий.
3. Соберите проект с помощью Maven:
```bash
mvn clean install
```
5. Запустите приложение:
```bash
java -jar target/carpooling-cli.jar
```
## Лицензия

Этот проект распространяется под лицензией MIT. Подробности см. в файле LICENSE.

## Контакты

- **Email:** manachinsky88@gmail.com  
- **GitHub:** [Buka228](https://github.com/Buka228)

