package com.insurancecorp.insurecar.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Getter
@Setter
public class Coverage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Double basePremium;
    private Boolean isActive;

    private LocalDate createdAt;
    private LocalDate updatedAt;
}
