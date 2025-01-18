package com.carpooling.cli.cli;

import com.carpooling.cli.context.CliContext;
import com.carpooling.entities.database.Rating;
import com.carpooling.exceptions.service.RatingServiceException;
import com.carpooling.services.base.RatingService;
import com.carpooling.services.impl.RatingServiceImpl;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


@Command(name = "rateTrip", description = "Оценка поездки")
public class RateTripCommand implements Runnable {

    private final RatingService ratingService;

    public RateTripCommand() {
        this(new RatingServiceImpl());
    }

    public RateTripCommand(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @Option(names = {"-t", "--tripId"}, description = "ID поездки", required = true)
    private String tripId;

    @Option(names = {"-r", "--rating"}, description = "Рейтинг (от 1 до 5)", required = true)
    private int rating;

    @Option(names = {"-c", "--comment"}, description = "Комментарий", required = false)
    private String comment;

    @Override
    public void run() {
        String currentUserId = CliContext.getCurrentUserId();
        if (currentUserId == null) {
            System.err.println("Ошибка: Вы не авторизованы.");
            return;
        }

        Rating ratingObj = new Rating();
        ratingObj.setRating(rating);
        ratingObj.setComment(comment);

        try {
            String ratingId = ratingService.createRating(ratingObj, tripId);
            CliContext.setCurrentRatingId(ratingId);
            System.out.println("Поездка оценена с ID: " + ratingId);
        } catch (RatingServiceException e) {
            System.err.println("Ошибка при оценке поездки: " + e.getMessage());
        }
    }
}