package com.komponente.reservation_service.model;

import com.komponente.reservation_service.dto.CompanyRating;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter


@NamedNativeQuery(name = "Review.findCompanyRating",
        query = "select co.name as companyName, avg(r.rating) as rating from reservations.review r join reservations.vehicle v on r.vehicle_plate_number = v.plate_number join reservations.company co on v.company_id = co.id group by co.name order by avg(r.rating) desc",
        resultSetMapping = "Mapping.CompanyRating")
@SqlResultSetMapping(name = "Mapping.CompanyRating",
        classes = @ConstructorResult(targetClass = CompanyRating.class,
                columns = {@ColumnResult(name = "companyName", type = String.class),
                        @ColumnResult(name = "rating", type = Double.class)}))

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;

    private String comment;

    @OneToOne(optional = false)
    private Vehicle vehicle;

    private Long user_id;
}
