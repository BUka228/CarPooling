package com.carpooling.cli.cli;

import com.carpooling.cli.context.CliContext;
import com.carpooling.entities.database.User;
import com.carpooling.exceptions.service.UserServiceException;
import com.carpooling.services.base.UserService;
import com.carpooling.services.impl.UserServiceImpl;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.sql.Date;


@Command(name = "register", description = "Регистрация нового пользователя")
public class RegisterCommand implements Runnable {

    private final UserService userService; // Внедряем UserService через конструктор

    public RegisterCommand() { this.userService = new UserServiceImpl(); }

    public RegisterCommand(UserService userService) { this.userService = userService; }

    @Option(names = {"-n", "--name"}, description = "Имя пользователя", required = true)
    private String name;

    @Option(names = {"-e", "--email"}, description = "Email пользователя", required = true)
    private String email;

    @Option(names = {"-p", "--password"}, description = "Пароль пользователя", required = true)
    private String password;

    @Option(names = {"-g", "--gender"}, description = "Пол пользователя", required = true)
    private String gender;

    @Option(names = {"-ph", "--phone"}, description = "Телефон пользователя", required = true)
    private String phone;

    @Option(names = {"-b", "--birthDate"}, description = "Дата рождения пользователя (гггг-ММ-дд)", required = true)
    private String birthDate;

    @Option(names = {"-a", "--address"}, description = "Адрес пользователя", required = true)
    private String address;

    @Option(names = {"-pr", "--preferences"}, description = "Предпочтения пользователя", required = false)
    private String preferences;

    @Override
    public void run() {
        User user = new User(
                "",
                name,
                email,
                password,
                gender,
                phone,
                Date.valueOf(birthDate),
                address,
                preferences
        );

        try {
            String userId = userService.registerUser(user);
            CliContext.setCurrentUserId(userId);
            System.out.println("Пользователь зарегистрирован с ID: " + userId);
        } catch (UserServiceException e) {
            System.err.println("Ошибка при регистрации пользователя: " + e.getMessage());
        }
    }
}