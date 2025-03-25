package com.carpooling.dao.xml;


import com.carpooling.dao.base.RatingDao;
import com.carpooling.entities.database.Rating;
import com.carpooling.exceptions.dao.DataAccessException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class XmlRatingDao extends AbstractXmlDao<Rating, XmlRatingDao.RatingWrapper> implements RatingDao {

    public XmlRatingDao(String filePath) {
        super(Rating.class, RatingWrapper.class, filePath);
    }

    @Override
    public String createRating(@NotNull Rating rating) throws DataAccessException {
        UUID ratingId = generateId();
        rating.setId(ratingId);

        try {
            List<Rating> ratings = readAll();
            ratings.add(rating);
            writeAll(ratings);
            log.info("Rating created successfully: {}", ratingId);
            return ratingId.toString();
        } catch (JAXBException e) {
            log.error("Error creating rating: {}", e.getMessage());
            throw new DataAccessException("Error creating rating", e);
        }
    }

    @Override
    public Optional<Rating> getRatingById(String id) throws DataAccessException {
        try {
            Optional<Rating> rating = findById(record -> record.getId().toString().equals(id));
            if (rating.isPresent()) {
                log.info("Rating found: {}", id);
            } else {
                log.warn("Rating not found: {}", id);
            }
            return rating;
        } catch (JAXBException e) {
            log.error("Error reading rating: {}", e.getMessage());
            throw new DataAccessException("Error reading rating", e);
        }
    }

    @Override
    public void updateRating(@NotNull Rating rating) throws DataAccessException {
        try {
            boolean updated = updateItem(record -> record.getId().equals(rating.getId()), rating);
            if (!updated) {
                log.warn("Rating not found for update: {}", rating.getId());
                throw new DataAccessException("Rating not found");
            }
            log.info("Rating updated successfully: {}", rating.getId());
        } catch (JAXBException e) {
            log.error("Error updating rating: {}", e.getMessage());
            throw new DataAccessException("Error updating rating", e);
        }
    }

    @Override
    public void deleteRating(String id) throws DataAccessException {
        try {
            boolean removed = deleteById(record -> record.getId().toString().equals(id));
            if (removed) {
                log.info("Rating deleted successfully: {}", id);
            } else {
                log.warn("Rating not found for deletion: {}", id);
            }
        } catch (JAXBException e) {
            log.error("Error deleting rating: {}", e.getMessage());
            throw new DataAccessException("Error deleting rating", e);
        }
    }

    @Override
    protected List<Rating> getItemsFromWrapper(@NotNull RatingWrapper wrapper) {
        return wrapper.getRatings();
    }

    @Override
    protected RatingWrapper createWrapper(List<Rating> items) {
        return new RatingWrapper(items);
    }

    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @XmlRootElement(name = "ratings")
    protected static class RatingWrapper {
        private List<Rating> ratings;

        @XmlElement(name = "rating")
        public List<Rating> getRatings() {
            return ratings;
        }
    }
}