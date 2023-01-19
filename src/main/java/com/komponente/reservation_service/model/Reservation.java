package com.komponente.reservation_service.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Vehicle vehicle;

    private Long userId;

    private Date startDate;
    private Date endDate;
    private int price;
    private boolean reminded;
}
