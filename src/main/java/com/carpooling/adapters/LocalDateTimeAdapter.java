package com.carpooling.adapters;


import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Адаптер JAXB для преобразования между LocalDateTime и строкой в формате ISO_LOCAL_DATE_TIME.
 */
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    // Используем стандартный ISO формат (например, "2011-12-03T10:15:30"),
    // который LocalDateTime.parse() и LocalDateTime.toString() используют по умолчанию.
    // Можно явно задать формат, если требуется:
    // private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public LocalDateTime unmarshal(String v) throws Exception {
        // Преобразование из строки XML в LocalDateTime
        if (v == null) {
            return null;
        }
        // return LocalDateTime.parse(v, formatter); // Если используете явный formatter
        return LocalDateTime.parse(v);
    }

    @Override
    public String marshal(LocalDateTime v) throws Exception {
        // Преобразование из LocalDateTime в строку для XML
        if (v == null) {
            return null;
        }
        // return v.format(formatter); // Если используете явный formatter
        return v.toString();
    }
}