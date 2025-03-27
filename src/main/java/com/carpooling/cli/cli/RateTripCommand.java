package com.carpooling.cli.cli;

import com.carpooling.cli.context.CliContext;
import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.exceptions.service.OperationNotSupportedException;
import com.carpooling.exceptions.service.RatingException;
import com.carpooling.factories.ServiceFactory; // Используем ServiceFactory
import com.carpooling.services.base.RatingService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "rateTrip", description = "Оценка поездки")
public class RateTripCommand implements Runnable {

    @Option(names = {"-t", "--tripId"}, required = true) private String tripId;
    @Option(names = {"-r", "--rating"}, required = true) private int rating;
    @Option(names = {"-c", "--comment"}) private String comment;

    @Override
    public void run() {
        String currentUserId = CliContext.getCurrentUserId();
        if (currentUserId == null) {
            System.err.println("Ошибка: Вы должны войти в систему (login).");
            return;
        }
        System.out.println("Попытка оценки поездки ID: " + tripId + " пользователем ID: " + currentUserId);

        try {
            RatingService ratingService = ServiceFactory.getRatingService();

            if (rating < 1 || rating > 5) {
                System.err.println("Ошибка: Рейтинг должен быть целым числом от 1 до 5.");
                return;
            }

            // Вызов сервиса
            String ratingId = ratingService.createRating(
                    currentUserId,
                    tripId,
                    rating,
                    comment
            );

            System.out.println("Оценка успешно добавлена!");
            System.out.println("Rating ID: " + ratingId);
            System.out.println("Используемое хранилище: " + CliContext.getCurrentStorageType());

        } catch (RatingException e) {
            System.err.println("Ошибка оценки: " + e.getMessage());
        } catch (OperationNotSupportedException e) {
            System.err.println("Ошибка: Операция (" + e.getMessage() + ") не поддерживается текущим хранилищем ("+ CliContext.getCurrentStorageType() +").");
        } catch (DataAccessException e) {
            System.err.println("Ошибка доступа к данным: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Произошла непредвиденная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}