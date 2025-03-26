package com.carpooling.adapters;


import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Адаптер JAXB для преобразования между LocalDate и строкой в формате ISO_LOCAL_DATE.
 */
public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    // Используем стандартный ISO формат (например, "2011-12-03"),
    // который LocalDate.parse() и LocalDate.toString() используют по умолчанию.
    // Можно явно задать формат, если требуется:
    // private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public LocalDate unmarshal(String v) throws Exception {
        // Преобразование из строки XML в LocalDate
        if (v == null) {
            return null;
        }
        // return LocalDate.parse(v, formatter); // Если используете явный formatter
        return LocalDate.parse(v);
    }

    @Override
    public String marshal(LocalDate v) throws Exception {
        // Преобразование из LocalDate в строку для XML
        if (v == null) {
            return null;
        }
        // return v.format(formatter); // Если используете явный formatter
        return v.toString();
    }
}