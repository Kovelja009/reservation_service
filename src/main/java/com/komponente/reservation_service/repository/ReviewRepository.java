package com.komponente.reservation_service.repository;

import com.komponente.reservation_service.dto.CompanyRating;
import com.komponente.reservation_service.model.Review;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query(value = "select * from reservations.review where vehicle_plate_number=?1 and user_id=?2", nativeQuery = true)
    Optional<Review> findReviewForDeleting(String plateNumber, long userId);

    @Query(value = "select r.id, r.comment, r.rating, r.vehicle_plate_number, r.user_id from reservations.review r join reservations.vehicle v on r.vehicle_plate_number = v.plate_number join reservations.city c on v.city_id = c.id join reservations.company co on v.company_id = co.id where c.city=?1 and co.name=?2 order by r.rating asc", nativeQuery = true)
    Optional<List<Review>> findByCityAndCompany(String city, String company);

    @Query(value = "select r.id, r.comment, r.rating, r.vehicle_plate_number, r.user_id from reservations.review r join reservations.vehicle v on r.vehicle_plate_number = v.plate_number join reservations.city c on v.city_id = c.id where c.city=?1 order by r.rating asc", nativeQuery = true)
    Optional<List<Review>> findByCity(String city);

    @Query(value = "select r.id, r.comment, r.rating, r.vehicle_plate_number, r.user_id from reservations.review r join reservations.vehicle v on r.vehicle_plate_number = v.plate_number join reservations.company co on v.company_id = co.id where co.name=?1 order by r.rating asc", nativeQuery = true)
    Optional<List<Review>> findByCompany(String company);

    @Query(value = "select * from reservations.review order by r.rating asc", nativeQuery = true)
    Optional<List<Review>> findAllReviews();

    @Query(nativeQuery = true)
    Optional<List<CompanyRating>> findCompanyRating();
}
