package data.dao.xml;


import data.dao.base.RatingDao;
import data.model.record.RatingRecord;
import exceptions.dao.DataAccessException;
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

import static com.man.constant.ErrorMessages.RATING_CREATION_ERROR;
import static com.man.constant.ErrorMessages.RATING_UPDATE_ERROR;
import static com.man.constant.ErrorMessages.*;
import static com.man.constant.LogMessages.*;


@Slf4j
public class XmlRatingDao extends AbstractXmlDao<RatingRecord, XmlRatingDao.RatingWrapper> implements RatingDao {

    public XmlRatingDao(String filePath) {
        super(RatingRecord.class, RatingWrapper.class, filePath);
    }

    @Override
    public String createRating(@NotNull RatingRecord ratingRecord) throws DataAccessException {
        log.info(CREATE_RATING_START, ratingRecord.getTripId());

        // Генерация UUID для ID
        String ratingId = generateId();
        ratingRecord.setId(ratingId);

        try {
            List<RatingRecord> ratings = readAll();
            ratings.add(ratingRecord);
            writeAll(ratings);

            log.info(CREATE_RATING_SUCCESS, ratingId);
            return ratingId;
        } catch (JAXBException e) {
            log.error(ERROR_CREATE_RATING, ratingRecord.getTripId(), e);
            throw new DataAccessException(RATING_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<RatingRecord> getRatingById(String id) throws DataAccessException {
        log.info(GET_RATING_START, id);
        try {
            return findById(record -> record.getId().equals(id));
        } catch (JAXBException e) {
            log.error(ERROR_GET_RATING, id, e);
            throw new DataAccessException(String.format(RATING_NOT_FOUND_ERROR, id), e);
        }
    }

    @Override
    public void updateRating(RatingRecord ratingRecord) throws DataAccessException {
        try {
            List<RatingRecord> ratings = readAll();
            boolean updated = updateItem(ratings, record -> record.getId().equals(ratingRecord.getId()), ratingRecord);

            if (!updated) {
                log.warn(WARN_RATING_NOT_FOUND, ratingRecord.getId());
                throw new DataAccessException(String.format(RATING_NOT_FOUND_ERROR, ratingRecord.getId()));
            }

            writeAll(ratings);
            log.info(UPDATE_RATING_SUCCESS, ratingRecord.getId());
        } catch (JAXBException e) {
            log.error(ERROR_UPDATE_RATING, ratingRecord.getId(), e);
            throw new DataAccessException(RATING_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteRating(String id) throws DataAccessException {
        try {
            deleteById(record -> record.getId().equals(id));
            log.info(DELETE_RATING_SUCCESS, id);
        } catch (JAXBException e) {
            log.error(ERROR_DELETE_RATING, id, e);
            throw new DataAccessException(RATING_DELETE_ERROR, e);
        }
    }

    @Override
    protected List<RatingRecord> getItemsFromWrapper(@NotNull RatingWrapper wrapper) {
        return wrapper.getRatings();
    }

    @Override
    protected RatingWrapper createWrapper(List<RatingRecord> items) {
        return new RatingWrapper(items);
    }

    /**
     * Вспомогательный класс для обертки списка рейтингов в XML.
     */
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @XmlRootElement(name = "ratings")
    protected static class RatingWrapper {
        private List<RatingRecord> ratings;

        @XmlElement(name = "rating")
        public List<RatingRecord> getRatings() {
            return ratings;
        }
    }
}
