package com.insurancecorp.insurecar.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Getter
@Setter
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vin;
    private String make;
    private String model;
    private String year;
    private String licensePlate;
    private String color;

    @ManyToOne
    private Customer owner;

    private LocalDate createdAt;
    private LocalDate updatedAt;
}
