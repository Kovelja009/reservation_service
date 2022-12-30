package com.komponente.reservation_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
public class Vehicle {
    @Id
    private String plateNumber;
    @ManyToOne(optional = false)
    private Model model;
    @ManyToOne(optional = false)
    private Company company;
    @ManyToOne
    private City city;
    private int pricePerDay;
}
