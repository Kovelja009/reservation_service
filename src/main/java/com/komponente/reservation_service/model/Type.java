package com.komponente.reservation_service.model;

import jakarta.persistence.*;
import jakarta.persistence.GenerationType;

@Entity
@Table(indexes = {@Index(columnList = "type", unique = true)})
public class Type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
}
