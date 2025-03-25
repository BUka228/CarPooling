package com.carpooling.cli.cli;

import com.carpooling.cli.context.CliContext;
import com.carpooling.entities.database.Rating;
import com.carpooling.entities.database.Trip;
import com.carpooling.exceptions.service.RatingServiceException;
import com.carpooling.exceptions.service.TripServiceException;
import com.carpooling.factories.DaoFactory;
import com.carpooling.services.base.RatingService;
import com.carpooling.services.base.TripService;
import com.carpooling.services.impl.RatingServiceImpl;
import com.carpooling.services.impl.RouteServiceImpl;
import com.carpooling.services.impl.TripServiceImpl;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.Date;
import java.util.Optional;


@Command(name = "rateTrip", description = "Оценка поездки")
public class RateTripCommand implements Runnable {

    @Option(names = {"-t", "--tripId"}, description = "ID поездки", required = true)
    private String tripId;

    @Option(names = {"-r", "--rating"}, description = "Рейтинг (от 1 до 5)", required = true)
    private int rating;

    @Option(names = {"-c", "--comment"}, description = "Комментарий", required = false)
    private String comment;

    @Override
    public void run() {
        /*// Инициализация сервисов
        RatingService ratingService = new RatingServiceImpl(DaoFactory.getRatingDao(CliContext.getCurrentStorageType()));
        TripService tripService = new TripServiceImpl(
                DaoFactory.getTripDao(CliContext.getCurrentStorageType()),
                new RouteServiceImpl(DaoFactory.getRouteDao(CliContext.getCurrentStorageType()))
        );

        String currentUserId = CliContext.getCurrentUserId();
        if (currentUserId == null) {
            System.err.println("Ошибка: Вы не авторизованы.");
            return;
        }

        try {
            // Получение объекта Trip по tripId
            Optional<Trip> tripOptional = tripService.getTripById(tripId);
            if (tripOptional.isEmpty()) {
                System.err.println("Ошибка: Поездка с ID " + tripId + " не найдена.");
                return;
            }
            Trip trip = tripOptional.get();

            // Создание объекта Rating
            Rating ratingObj = new Rating();
            ratingObj.setRating(rating);
            ratingObj.setComment(comment);
            ratingObj.setDate(new Date(System.currentTimeMillis())); // Устанавливаем текущую дату

            // Вызов метода createRating с объектами Rating и Trip
            String ratingId = ratingService.createRating(ratingObj, trip);
            CliContext.setCurrentRatingId(ratingId);
            System.out.println("Поездка оценена с ID: " + ratingId);
        } catch (RatingServiceException | TripServiceException e) {
            System.err.println("Ошибка при оценке поездки: " + e.getMessage());
        }*/
    }
}