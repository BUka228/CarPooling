package com.carpooling.constants;

public final class LogMessages {

    // Общие сообщения
    public static final String CREATE_HISTORY_SUCCESS = "Запись в истории успешно создана с ID: {}";
    public static final String ERROR_CREATE_HISTORY = "Ошибка при создании записи истории: {}";
    public static final String GET_HISTORY_SUCCESS = "Запись в истории успешно найдена с ID: {}";
    public static final String WARN_HISTORY_NOT_FOUND = "Запись в истории с ID {} не найдена.";
    public static final String ERROR_GET_HISTORY = "Ошибка при поиске записи истории с ID: {}";
    public static final String UPDATE_HISTORY_SUCCESS = "Запись в истории успешно обновлена с ID: {}";
    public static final String ERROR_UPDATE_HISTORY = "Ошибка при обновлении записи истории с ID: {}";
    public static final String DELETE_HISTORY_SUCCESS = "Запись в истории успешно удалена с ID: {}";
    public static final String ERROR_DELETE_HISTORY = "Ошибка при удалении записи истории с ID: {}";
    public static final String INIT_DAO_START = "Начало инициализации DAO для типа хранилища: {}";
    public static final String INIT_DAO_SUCCESS = "DAO успешно инициализирован для типа хранилища: {}";
    public static final String INIT_DAO_ERROR = "Ошибка при инициализации DAO для типа хранилища: {}";

    // Сообщения, связанные с пользователями
    public static final String CREATE_USER_START = "Создание нового пользователя: name={}.";
    public static final String CREATE_USER_SUCCESS = "Пользователь успешно создан с ID {}.";
    public static final String GET_USER_START = "Получение пользователя по ID: {}.";
    public static final String UPDATE_USER_SUCCESS = "Пользователь с ID {} успешно обновлен.";
    public static final String DELETE_USER_SUCCESS = "Пользователь с ID {} успешно удален.";
    public static final String ERROR_CREATE_USER = "Ошибка при создании пользователя. Контекст: name={}.";
    public static final String ERROR_GET_USER = "Ошибка при поиске пользователя. Контекст: userId={}.";
    public static final String ERROR_UPDATE_USER = "Ошибка при обновлении пользователя. Контекст: userId={}.";
    public static final String ERROR_DELETE_USER = "Ошибка при удалении пользователя. Контекст: userId={}.";
    public static final String WARN_USER_NOT_FOUND = "Пользователь с ID {} не найден.";

    public static final String USER_REGISTRATION_START = "Начало регистрации пользователя: {}";
    public static final String USER_REGISTRATION_SUCCESS = "Пользователь успешно зарегистрирован: {}";
    public static final String USER_REGISTRATION_ERROR = "Ошибка при регистрации пользователя: {}";

    public static final String USER_SEARCH_BY_ID_START = "Поиск пользователя по ID: {}";
    public static final String USER_SEARCH_BY_ID_SUCCESS = "Пользователь найден: {}";
    public static final String USER_SEARCH_BY_ID_ERROR = "Ошибка при поиске пользователя по ID: {}";

    public static final String USER_SEARCH_BY_EMAIL_START = "Поиск пользователя по email: {}";
    public static final String USER_SEARCH_BY_EMAIL_ERROR = "Ошибка при поиске пользователя по email: {}";

    public static final String USER_UPDATE_START = "Обновление данных пользователя: {}";
    public static final String USER_UPDATE_SUCCESS = "Данные пользователя успешно обновлены: {}";
    public static final String USER_UPDATE_ERROR = "Ошибка при обновлении данных пользователя: {}";

    public static final String USER_DELETION_START = "Удаление пользователя: {}";
    public static final String USER_DELETION_SUCCESS = "Пользователь успешно удален: {}";
    public static final String USER_DELETION_ERROR = "Ошибка при удалении пользователя: {}";

    public static final String USER_AUTHENTICATION_START = "Аутентификация пользователя: {}";
    public static final String USER_AUTHENTICATION_SUCCESS = "Аутентификация успешна: {}";
    public static final String USER_AUTHENTICATION_ERROR = "Ошибка при аутентификации пользователя: {}";

    public static final String USER_PASSWORD_CHANGE_START = "Изменение пароля пользователя: {}";
    public static final String USER_PASSWORD_CHANGE_SUCCESS = "Пароль успешно изменен: {}";
    public static final String USER_PASSWORD_CHANGE_ERROR = "Ошибка при изменении пароля: {}";

    // Сообщения, связанные с поездками
    public static final String CREATE_TRIP_START = "Создание новой поездки: userId={}, routeId={}.";
    public static final String CREATE_TRIP_SUCCESS = "Поездка успешно создана с ID {}.";
    public static final String GET_TRIP_START = "Получение поездки по ID: {}.";
    public static final String UPDATE_TRIP_SUCCESS = "Поездка с ID {} успешно обновлена.";
    public static final String DELETE_TRIP_SUCCESS = "Поездка с ID {} успешно удалена.";
    public static final String ERROR_CREATE_TRIP = "Ошибка при создании поездки. Контекст: userId={}, routeId={}.";
    public static final String ERROR_GET_TRIP = "Ошибка при поиске поездки. Контекст: tripId={}.";
    public static final String ERROR_UPDATE_TRIP = "Ошибка при обновлении поездки. Контекст: tripId={}.";
    public static final String ERROR_DELETE_TRIP = "Ошибка при удалении поездки. Контекст: tripId={}.";
    public static final String WARN_TRIP_NOT_FOUND = "Поездка с ID {} не найдена.";

    public static final String TRIP_CREATION_START = "Начало создания поездки для пользователя: {}";
    public static final String TRIP_CREATION_SUCCESS = "Поездка успешно создана: {}";
    public static final String TRIP_CREATION_ERROR = "Ошибка при создании поездки: {}";

    public static final String TRIP_SEARCH_BY_ID_START = "Поиск поездки по ID: {}";
    public static final String TRIP_SEARCH_BY_ID_SUCCESS = "Поездка найдена: {}";
    public static final String TRIP_SEARCH_BY_ID_ERROR = "Ошибка при поиске поездки по ID: {}";

    public static final String TRIP_UPDATE_START = "Обновление поездки: {}";
    public static final String TRIP_UPDATE_SUCCESS = "Поездка успешно обновлена: {}";
    public static final String TRIP_UPDATE_ERROR = "Ошибка при обновлении поездки: {}";

    public static final String TRIP_DELETION_START = "Удаление поездки: {}";
    public static final String TRIP_DELETION_SUCCESS = "Поездка успешно удалена: {}";
    public static final String TRIP_DELETION_ERROR = "Ошибка при удалении поездки: {}";

    public static final String TRIP_GET_ALL_START = "Получение всех поездок.";
    public static final String TRIP_GET_ALL_SUCCESS = "Все поездки успешно получены.";
    public static final String TRIP_GET_ALL_ERROR = "Ошибка при получении всех поездок: {}";

    public static final String TRIP_GET_BY_USER_START = "Получение поездок пользователя: {}";
    public static final String TRIP_GET_BY_USER_ERROR = "Ошибка при получении поездок пользователя: {}";

    public static final String TRIP_GET_BY_STATUS_START = "Получение поездок по статусу: {}";
    public static final String TRIP_GET_BY_STATUS_ERROR = "Ошибка при получении поездок по статусу: {}";

    public static final String TRIP_GET_BY_CREATION_DATE_START = "Получение поездок по дате создания: {}";
    public static final String TRIP_GET_BY_CREATION_DATE_ERROR = "Ошибка при получении поездок по дате создания: {}";

    public static final String TRIP_GET_BY_ROUTE_START = "Получение поездок по маршруту: {}";
    public static final String TRIP_GET_BY_ROUTE_ERROR = "Ошибка при получении поездок по маршруту: {}";

    // Сообщения, связанные с маршрутами
    public static final String CREATE_ROUTE_START = "Создание нового маршрута: startPoint={}, endPoint={}.";
    public static final String CREATE_ROUTE_SUCCESS = "Маршрут успешно создан с ID {}.";
    public static final String GET_ROUTE_START = "Получение маршрута по ID: {}.";
    public static final String UPDATE_ROUTE_SUCCESS = "Маршрут с ID {} успешно обновлен.";
    public static final String DELETE_ROUTE_SUCCESS = "Маршрут с ID {} успешно удален.";
    public static final String ERROR_CREATE_ROUTE = "Ошибка при создании маршрута. Контекст: startPoint={}, endPoint={}.";
    public static final String ERROR_GET_ROUTE = "Ошибка при поиске маршрута. Контекст: routeId={}.";
    public static final String ERROR_UPDATE_ROUTE = "Ошибка при обновлении маршрута. Контекст: routeId={}.";
    public static final String ERROR_DELETE_ROUTE = "Ошибка при удалении маршрута. Контекст: routeId={}.";
    public static final String WARN_ROUTE_NOT_FOUND = "Маршрут с ID {} не найден.";

    public static final String ROUTE_CREATION_START = "Начало создания маршрута: {} -> {}";
    public static final String ROUTE_CREATION_SUCCESS = "Маршрут успешно создан: {}";
    public static final String ROUTE_CREATION_ERROR = "Ошибка при создании маршрута: {}";

    public static final String ROUTE_SEARCH_BY_ID_START = "Поиск маршрута по ID: {}";
    public static final String ROUTE_SEARCH_BY_ID_SUCCESS = "Маршрут найден: {}";
    public static final String ROUTE_SEARCH_BY_ID_ERROR = "Ошибка при поиске маршрута по ID: {}";

    public static final String ROUTE_GET_ALL_START = "Получение всех маршрутов.";
    public static final String ROUTE_GET_ALL_SUCCESS = "Все маршруты успешно получены.";
    public static final String ROUTE_GET_ALL_ERROR = "Ошибка при получении всех маршрутов: {}";

    public static final String ROUTE_UPDATE_START = "Обновление маршрута: {}";
    public static final String ROUTE_UPDATE_SUCCESS = "Маршрут успешно обновлен: {}";
    public static final String ROUTE_UPDATE_ERROR = "Ошибка при обновлении маршрута: {}";

    public static final String ROUTE_DELETION_START = "Удаление маршрута: {}";
    public static final String ROUTE_DELETION_SUCCESS = "Маршрут успешно удален: {}";
    public static final String ROUTE_DELETION_ERROR = "Ошибка при удалении маршрута: {}";

    public static final String ROUTE_SEARCH_BY_START_POINT_START = "Поиск маршрутов по начальной точке: {}";
    public static final String ROUTE_SEARCH_BY_START_POINT_ERROR = "Ошибка при поиске маршрутов по начальной точке: {}";

    public static final String ROUTE_SEARCH_BY_END_POINT_START = "Поиск маршрутов по конечной точке: {}";
    public static final String ROUTE_SEARCH_BY_END_POINT_ERROR = "Ошибка при поиске маршрутов по конечной точке: {}";

    public static final String ROUTE_SEARCH_BY_START_AND_END_POINTS_START = "Поиск маршрутов по начальной и конечной точкам: {} -> {}";
    public static final String ROUTE_SEARCH_BY_START_AND_END_POINTS_ERROR = "Ошибка при поиске маршрутов по начальной и конечной точкам: {}";

    public static final String ROUTE_GET_BY_USER_START = "Получение маршрутов пользователя: {}";
    public static final String ROUTE_GET_BY_USER_ERROR = "Ошибка при получении маршрутов пользователя: {}";

    public static final String ROUTE_GET_BY_DATE_START = "Получение маршрутов по дате: {}";
    public static final String ROUTE_GET_BY_DATE_ERROR = "Ошибка при получении маршрутов по дате: {}";

    // Сообщения, связанные с рейтингами
    public static final String CREATE_RATING_START = "Создание нового рейтинга для поездки с ID={}.";
    public static final String CREATE_RATING_SUCCESS = "Рейтинг успешно создан с ID {}.";
    public static final String GET_RATING_START = "Получение рейтинга по ID: {}.";
    public static final String UPDATE_RATING_SUCCESS = "Рейтинг с ID {} успешно обновлен.";
    public static final String DELETE_RATING_SUCCESS = "Рейтинг с ID {} успешно удален.";
    public static final String ERROR_CREATE_RATING = "Ошибка при создании рейтинга. Контекст: tripId={}.";
    public static final String ERROR_GET_RATING = "Ошибка при поиске рейтинга. Контекст: ratingId={}.";
    public static final String ERROR_UPDATE_RATING = "Ошибка при обновлении рейтинга. Контекст: ratingId={}.";
    public static final String ERROR_DELETE_RATING = "Ошибка при удалении рейтинга. Контекст: ratingId={}.";
    public static final String WARN_RATING_NOT_FOUND = "Рейтинг с ID {} не найден.";

    public static final String RATING_CREATION_START = "Начало создания оценки для поездки: {}";
    public static final String RATING_CREATION_SUCCESS = "Оценка успешно создана: {}";
    public static final String RATING_CREATION_ERROR = "Ошибка при создании оценки: {}";

    public static final String RATING_SEARCH_BY_ID_START = "Поиск оценки по ID: {}";
    public static final String RATING_SEARCH_BY_ID_SUCCESS = "Оценка найдена: {}";
    public static final String RATING_SEARCH_BY_ID_ERROR = "Ошибка при поиске оценки по ID: {}";

    public static final String RATING_GET_ALL_START = "Получение всех оценок.";
    public static final String RATING_GET_ALL_SUCCESS = "Все оценки успешно получены.";
    public static final String RATING_GET_ALL_ERROR = "Ошибка при получении всех оценок: {}";

    public static final String RATING_UPDATE_START = "Обновление оценки: {}";
    public static final String RATING_UPDATE_SUCCESS = "Оценка успешно обновлена: {}";
    public static final String RATING_UPDATE_ERROR = "Ошибка при обновлении оценки: {}";

    public static final String RATING_DELETION_START = "Удаление оценки: {}";
    public static final String RATING_DELETION_SUCCESS = "Оценка успешно удалена: {}";
    public static final String RATING_DELETION_ERROR = "Ошибка при удалении оценки: {}";

    public static final String RATING_GET_BY_TRIP_START = "Получение оценок по поездке: {}";
    public static final String RATING_GET_BY_TRIP_ERROR = "Ошибка при получении оценок по поездке: {}";

    public static final String RATING_GET_BY_RATING_START = "Получение оценок по рейтингу: {}";
    public static final String RATING_GET_BY_RATING_ERROR = "Ошибка при получении оценок по рейтингу: {}";

    public static final String RATING_GET_AVERAGE_START = "Расчет средней оценки для поездки: {}";
    public static final String RATING_GET_AVERAGE_ERROR = "Ошибка при расчете средней оценки: {}";

    // Сообщения, связанные с бронированиями
    public static final String CREATE_BOOKING_START = "Создание нового бронирования: tripId={}, userId={}.";
    public static final String CREATE_BOOKING_SUCCESS = "Бронирование успешно создано с ID {}.";
    public static final String GET_BOOKING_START = "Получение бронирования по ID: {}.";
    public static final String UPDATE_BOOKING_SUCCESS = "Бронирование с ID {} успешно обновлено.";
    public static final String DELETE_BOOKING_SUCCESS = "Бронирование с ID {} успешно удалено.";
    public static final String ERROR_CREATE_BOOKING = "Ошибка при создании бронирования. Контекст: tripId={}, userId={}.";
    public static final String ERROR_GET_BOOKING = "Ошибка при поиске бронирования. Контекст: bookingId={}.";
    public static final String ERROR_UPDATE_BOOKING = "Ошибка при обновлении бронирования. Контекст: bookingId={}.";
    public static final String ERROR_DELETE_BOOKING = "Ошибка при удалении бронирования. Контекст: bookingId={}.";
    public static final String WARN_BOOKING_NOT_FOUND = "Бронирование с ID {} не найдено.";

    public static final String BOOKING_CREATION_START = "Начало создания бронирования для поездки: {} и пользователя: {}";
    public static final String BOOKING_CREATION_SUCCESS = "Бронирование успешно создано: {}";
    public static final String BOOKING_CREATION_ERROR = "Ошибка при создании бронирования: {}";

    public static final String BOOKING_SEARCH_BY_ID_START = "Поиск бронирования по ID: {}";
    public static final String BOOKING_SEARCH_BY_ID_SUCCESS = "Бронирование найдено: {}";
    public static final String BOOKING_SEARCH_BY_ID_ERROR = "Ошибка при поиске бронирования по ID: {}";

    public static final String BOOKING_GET_ALL_START = "Получение всех бронирований.";
    public static final String BOOKING_GET_ALL_SUCCESS = "Все бронирования успешно получены.";
    public static final String BOOKING_GET_ALL_ERROR = "Ошибка при получении всех бронирований: {}";

    public static final String BOOKING_UPDATE_START = "Обновление бронирования: {}";
    public static final String BOOKING_UPDATE_SUCCESS = "Бронирование успешно обновлено: {}";
    public static final String BOOKING_UPDATE_ERROR = "Ошибка при обновлении бронирования: {}";

    public static final String BOOKING_DELETION_START = "Удаление бронирования: {}";
    public static final String BOOKING_DELETION_SUCCESS = "Бронирование успешно удалено: {}";
    public static final String BOOKING_DELETION_ERROR = "Ошибка при удалении бронирования: {}";

    public static final String BOOKING_GET_BY_TRIP_START = "Получение бронирований по поездке: {}";
    public static final String BOOKING_GET_BY_TRIP_ERROR = "Ошибка при получении бронирований по поездке: {}";

    public static final String BOOKING_GET_BY_USER_START = "Получение бронирований по пользователю: {}";
    public static final String BOOKING_GET_BY_USER_ERROR = "Ошибка при получении бронирований по пользователю: {}";

    public static final String BOOKING_GET_BY_STATUS_START = "Получение бронирований по статусу: {}";
    public static final String BOOKING_GET_BY_STATUS_ERROR = "Ошибка при получении бронирований по статусу: {}";

    public static final String BOOKING_CANCEL_START = "Отмена бронирования: {}";
    public static final String BOOKING_CANCEL_ERROR = "Ошибка при отмене бронирования: {}";



    // Логи для PropertiesConfigLoader
    public static final String FAILED_TO_LOAD_PROPERTIES = "Не удалось загрузить properties из {}";
    public static final String FILE_NOT_FOUND_LOG = "Файл не найден: {}";
    public static final String IO_ERROR_LOG = "Ошибка ввода-вывода при работе с файлом: {}";

    // Логи для XmlConfigLoader
    public static final String FAILED_TO_LOAD_XML = "Не удалось загрузить XML из файла {}";

    // Логи для YamlConfigLoader
    public static final String FAILED_TO_LOAD_YAML = "Не удалось загрузить YAML из файла {}";

    private LogMessages() {
        // Приватный конструктор для предотвращения создания экземпляров класса
    }
}