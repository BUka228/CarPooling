package com.man.constant;

public final class ErrorMessages {

    // Общие ошибки
    public static final String ERROR_INIT_FILE = "Ошибка при инициализации файла";
    public static final String ERROR_DELETE_RECORD = "Запись не найдена для удаления";
    public static final String ERROR_CONVERT_OBJECT_TO_DOCUMENT = "Ошибка при преобразовании объекта в документ: {}";
    public static final String ERROR_CONVERT_DOCUMENT_TO_OBJECT = "Ошибка при преобразовании документа в объект: {}";
    public static final String CONVERT_OBJECT_TO_DOCUMENT_ERROR = "Не удалось преобразовать объект в документ.";
    public static final String CONVERT_DOCUMENT_TO_OBJECT_ERROR = "Не удалось преобразовать документ в объект.";

    // Ошибки, связанные с историей
    public static final String HISTORY_CREATION_ERROR = "Не удалось создать запись в истории контента.";
    public static final String HISTORY_NOT_FOUND_ERROR = "Не удалось найти запись истории с указанным ID.";
    public static final String HISTORY_UPDATE_ERROR = "Не удалось обновить запись в истории контента.";
    public static final String HISTORY_DELETE_ERROR = "Не удалось удалить запись в истории контента.";

    // Ошибки, связанные с пользователями
    public static final String USER_CREATION_ERROR = "Ошибка при создании пользователя.";
    public static final String USER_NOT_FOUND_ERROR = "Пользователь с ID %s не найден.";
    public static final String USER_UPDATE_ERROR = "Ошибка при обновлении пользователя.";
    public static final String USER_DELETE_ERROR = "Ошибка при удалении пользователя.";
    public static final String USER_REGISTRATION_ERROR = "Ошибка при регистрации пользователя.";
    public static final String USER_DELETION_ERROR = "Ошибка при удалении пользователя.";
    public static final String USER_AUTHENTICATION_ERROR = "Ошибка при аутентификации пользователя.";
    public static final String USER_PASSWORD_CHANGE_ERROR = "Ошибка при изменении пароля.";
    public static final String USER_SEARCH_ERROR = "Ошибка при поиске пользователя.";

    // Ошибки, связанные с поездками
    public static final String TRIP_CREATION_ERROR = "Ошибка при создании поездки.";
    public static final String TRIP_NOT_FOUND_ERROR = "Поездка с ID %s не найдена.";
    public static final String TRIP_UPDATE_ERROR = "Ошибка при обновлении поездки.";
    public static final String TRIP_DELETE_ERROR = "Ошибка при удалении поездки.";
    public static final String TRIP_DELETION_ERROR = "Ошибка при удалении поездки.";
    public static final String TRIP_SEARCH_ERROR = "Ошибка при поиске поездки.";
    public static final String TRIP_GET_ALL_ERROR = "Ошибка при получении всех поездок.";
    public static final String TRIP_GET_BY_USER_ERROR = "Ошибка при получении поездок пользователя.";
    public static final String TRIP_GET_BY_STATUS_ERROR = "Ошибка при получении поездок по статусу.";
    public static final String TRIP_GET_BY_CREATION_DATE_ERROR = "Ошибка при получении поездок по дате создания.";
    public static final String TRIP_GET_BY_ROUTE_ERROR = "Ошибка при получении поездок по маршруту.";

    // Ошибки, связанные с маршрутами
    public static final String ROUTE_CREATION_ERROR = "Ошибка при создании маршрута.";
    public static final String ROUTE_NOT_FOUND_ERROR = "Маршрут с ID %s не найден.";
    public static final String ROUTE_UPDATE_ERROR = "Ошибка при обновлении маршрута.";
    public static final String ROUTE_DELETE_ERROR = "Ошибка при удалении маршрута.";
    public static final String ROUTE_DELETION_ERROR = "Ошибка при удалении маршрута.";
    public static final String ROUTE_SEARCH_ERROR = "Ошибка при поиске маршрута.";
    public static final String ROUTE_GET_ALL_ERROR = "Ошибка при получении всех маршрутов.";
    public static final String ROUTE_GET_BY_USER_ERROR = "Ошибка при получении маршрутов пользователя.";
    public static final String ROUTE_GET_BY_DATE_ERROR = "Ошибка при получении маршрутов по дате.";

    // Ошибки, связанные с рейтингами
    public static final String RATING_CREATION_ERROR = "Ошибка при создании рейтинга.";
    public static final String RATING_NOT_FOUND_ERROR = "Рейтинг с ID %s не найден.";
    public static final String RATING_UPDATE_ERROR = "Ошибка при обновлении рейтинга.";
    public static final String RATING_DELETE_ERROR = "Ошибка при удалении рейтинга.";
    public static final String RATING_DELETION_ERROR = "Ошибка при удалении оценки.";
    public static final String RATING_SEARCH_ERROR = "Ошибка при поиске оценки.";
    public static final String RATING_GET_ALL_ERROR = "Ошибка при получении всех оценок.";
    public static final String RATING_GET_BY_TRIP_ERROR = "Ошибка при получении оценок по поездке.";
    public static final String RATING_GET_BY_RATING_ERROR = "Ошибка при получении оценок по рейтингу.";
    public static final String RATING_GET_AVERAGE_ERROR = "Ошибка при расчете средней оценки.";

    // Ошибки, связанные с бронированиями
    public static final String BOOKING_CREATION_ERROR = "Ошибка при создании бронирования.";
    public static final String BOOKING_NOT_FOUND_ERROR = "Бронирование с ID %s не найдено.";
    public static final String BOOKING_UPDATE_ERROR = "Ошибка при обновлении бронирования.";
    public static final String BOOKING_DELETE_ERROR = "Ошибка при удалении бронирования.";
    public static final String BOOKING_DELETION_ERROR = "Ошибка при удалении бронирования.";
    public static final String BOOKING_SEARCH_ERROR = "Ошибка при поиске бронирования.";
    public static final String BOOKING_GET_ALL_ERROR = "Ошибка при получении всех бронирований.";
    public static final String BOOKING_GET_BY_TRIP_ERROR = "Ошибка при получении бронирований по поездке.";
    public static final String BOOKING_GET_BY_USER_ERROR = "Ошибка при получении бронирований по пользователю.";
    public static final String BOOKING_GET_BY_STATUS_ERROR = "Ошибка при получении бронирований по статусу.";
    public static final String BOOKING_CANCEL_ERROR = "Ошибка при отмене бронирования.";

    // Ошибки инициализации DAO
    public static final String ERROR_INIT_USER_DAO = "Ошибка при инициализации DAO для пользователей";
    public static final String ERROR_INIT_TRIP_DAO = "Ошибка при инициализации DAO для поездок";
    public static final String ERROR_INIT_ROUTE_DAO = "Ошибка при инициализации DAO для маршрутов";
    public static final String ERROR_INIT_BOOKING_DAO = "Ошибка при инициализации DAO для бронирований";
    public static final String ERROR_INIT_RATING_DAO = "Ошибка при инициализации DAO для оценок";



    // Исключения для PropertiesConfigLoader
    public static final String ERROR_LOADING_PROPERTIES = "Ошибка загрузки properties: ";

    // Исключения для XmlConfigLoader
    public static final String ERROR_LOADING_XML = "Ошибка загрузки XML-файла: ";

    // Исключения для YamlConfigLoader
    public static final String ERROR_LOADING_YAML = "Ошибка загрузки YAML-файла: ";

    private ErrorMessages() {
        // Приватный конструктор для предотвращения создания экземпляров класса
    }
}