package com.carpooling.cli.cli;

import com.carpooling.cli.context.CliContext;
import com.carpooling.entities.database.User;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.AuthenticationException;
import com.carpooling.exceptions.service.OperationNotSupportedException;
import com.carpooling.factories.ServiceFactory; // Используем ServiceFactory
import com.carpooling.services.base.UserService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "login", description = "Авторизация пользователя")
public class LoginCommand implements Runnable {

    @Option(names = {"-e", "--email"}, required = true) private String email;
    @Option(names = {"-p", "--password"}, required = true) private String password;

    @Override
    public void run() {
        System.out.println("Попытка входа пользователя " + email + "...");
        try {
            UserService userService = ServiceFactory.getUserService();
            User user = userService.loginUser(email, password);

            CliContext.setCurrentUserId(user.getId().toString());

            System.out.println("Авторизация успешна!");
            System.out.println("Добро пожаловать, " + user.getName() + "!");
            System.out.println("Ваш текущий User ID: " + CliContext.getCurrentUserId());
            System.out.println("Используемое хранилище: " + CliContext.getCurrentStorageType());

        } catch (AuthenticationException e) {
            System.err.println("Ошибка входа: " + e.getMessage());
            CliContext.setCurrentUserId(null);
        } catch (OperationNotSupportedException e) {
            System.err.println("Ошибка: Текущее хранилище (" + CliContext.getCurrentStorageType() + ") не поддерживает вход по email. " + e.getMessage());
            CliContext.setCurrentUserId(null);
        } catch (DataAccessException e) {
            System.err.println("Ошибка доступа к данным при входе: " + e.getMessage());
            CliContext.setCurrentUserId(null);
        } catch (Exception e) {
            System.err.println("Произошла непредвиденная ошибка при входе: " + e.getMessage());
            CliContext.setCurrentUserId(null);
            e.printStackTrace();
        }
    }
}