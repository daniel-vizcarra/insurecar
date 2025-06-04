package com.insurancecorp.insurecar.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

@Entity
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Policy policy;

    private Double amount;
    private LocalDate paymentDate;
    private String method; // e.g., cash, card, transfer
    private String status; // e.g., completed, pending, failed

    private LocalDate createdAt;
    private LocalDate updatedAt;

    @PostPersist
    @PostUpdate
    public void updatePolicyStatus() {
        if (this.policy != null) {
            this.policy.updateStatusByPayments();
        }
    }
} 