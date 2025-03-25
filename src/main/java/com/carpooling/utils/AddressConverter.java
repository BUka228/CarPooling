package com.carpooling.utils;

import com.carpooling.entities.database.Address;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class AddressConverter extends AbstractBeanField<Address, String> {
    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException {
        if (value == null || value.isEmpty()) {
            return null;
        }
        // "street,zipcode,city"
        String[] parts = value.split(",");
        if (parts.length != 3) {
            throw new CsvDataTypeMismatchException("Invalid address format: " + value);
        }
        Address address = new Address();
        address.setStreet(parts[0].trim());
        address.setZipcode(parts[1].trim());
        address.setCity(parts[2].trim());
        return address;
    }

    @Override
    protected String convertToWrite(Object value) {
        if (value == null) {
            return "";
        }
        Address address = (Address) value;
        return String.format("%s,%s,%s",
                address.getStreet() != null ? address.getStreet() : "",
                address.getZipcode() != null ? address.getZipcode() : "",
                address.getCity() != null ? address.getCity() : "");
    }
}
