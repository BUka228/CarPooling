package com.carpooling.cli.cli;

import com.carpooling.cli.context.CliContext;
import com.carpooling.entities.database.Address;
import com.carpooling.entities.database.User;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.RegistrationException;
import com.carpooling.factories.ServiceFactory; // Используем ServiceFactory
import com.carpooling.services.base.UserService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import org.jetbrains.annotations.NotNull; // Используем аннотацию JetBrains

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Command(name = "register", description = "Регистрация нового пользователя")
public class RegisterCommand implements Runnable {

    @Option(names = {"-n", "--name"}, required = true) private String name;
    @Option(names = {"-e", "--email"}, required = true) private String email;
    @Option(names = {"-p", "--password"}, required = true) private String password;
    @Option(names = {"-g", "--gender"}) private String gender;
    @Option(names = {"-ph", "--phone"}) private String phone;
    @Option(names = {"-b", "--birthDate"}, required = true) private String birthDateStr;
    @Option(names = {"--street"}, required = true) private String street;
    @Option(names = {"--zipcode"}, required = true) private String zipcode;
    @Option(names = {"--city"}, required = true) private String city;
    @Option(names = {"-pr", "--preferences"}) private String preferences;

    @Override
    public void run() {
        System.out.println("Попытка регистрации пользователя...");
        try {
            UserService userService = ServiceFactory.getUserService();

            LocalDate birthDate;
            try {
                birthDate = LocalDate.parse(birthDateStr);
            } catch (DateTimeParseException e) {
                System.err.println("Ошибка: Неверный формат даты рождения '" + birthDateStr + "'. Используйте гггг-ММ-дд.");
                return; // Завершаем выполнение команды
            }

            User user = buildUser(birthDate);

            String userId = userService.registerUser(user);

            System.out.println("Пользователь успешно зарегистрирован!");
            System.out.println("Ваш User ID: " + userId);
            System.out.println("Используемое хранилище: " + CliContext.getCurrentStorageType());

        } catch (RegistrationException e) {
            System.err.println("Ошибка регистрации: " + e.getMessage());
        } catch (DataAccessException e) {
            System.err.println("Ошибка доступа к данным при регистрации: " + e.getMessage());
            // Можно добавить детализацию из e.getCause() при необходимости
            // if(e.getCause() != null) System.err.println("  Причина: " + e.getCause().getMessage());
        } catch (Exception e) { // Ловим остальные непредвиденные ошибки
            System.err.println("Произошла непредвиденная ошибка при регистрации: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @NotNull
    private User buildUser(LocalDate birthDate) {
        Address address = new Address(street, zipcode, city);
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password); // Пароль будет хешироваться в сервисе (если реализовано)
        user.setGender(gender);
        user.setPhone(phone);
        user.setBirthDate(birthDate);
        user.setAddress(address);
        user.setPreferences(preferences);
        return user;
    }
}