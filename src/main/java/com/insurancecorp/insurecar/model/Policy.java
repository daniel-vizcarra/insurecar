package com.insurancecorp.insurecar.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import javax.persistence.*;

@Entity
@Getter
@Setter
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Vehicle vehicle;

    private String policyNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double premium;
    private String status;

    @ManyToOne
    private Coverage coverage;

    private LocalDate createdAt;
    private LocalDate updatedAt;
}
