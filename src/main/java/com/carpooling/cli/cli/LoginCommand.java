package com.carpooling.cli.cli;

import com.carpooling.cli.context.CliContext;
import com.carpooling.exceptions.service.UserServiceException;
import com.carpooling.factories.DaoFactory;
import com.carpooling.services.base.UserService;
import com.carpooling.services.impl.UserServiceImpl;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.Optional;


@Command(name = "login", description = "Авторизация пользователя")
public class LoginCommand implements Runnable {

    @Option(names = {"-e", "--email"}, description = "Email пользователя", required = true)
    private String email;

    @Option(names = {"-p", "--password"}, description = "Пароль пользователя", required = true)
    private String password;

    @Override
    public void run() {
        /*UserService userService = new UserServiceImpl(DaoFactory.getUserDao(CliContext.getCurrentStorageType()));
        try {
            Optional<User> userOptional = userService.authenticateUser(email, password);
            if (userOptional.isPresent()) {
                CliContext.setCurrentUserId(userOptional.get().getId());
                System.out.println("Пользователь авторизован: " + userOptional.get().getName());
            } else {
                System.err.println("Неверный email или пароль.");
            }
        } catch (UserServiceException e) {
            System.err.println("Ошибка при авторизации: " + e.getMessage());
        }*/
    }
}