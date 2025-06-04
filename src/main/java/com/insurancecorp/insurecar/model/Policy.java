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

    @ManyToOne
    private Coverage coverage;

    @OneToMany(mappedBy = "policy")
    private java.util.List<Payment> payments;

    private LocalDate createdAt;
    private LocalDate updatedAt;

    @Enumerated(EnumType.STRING)
    private PolicyStatus status;

    public void updateStatusByPayments() {
        if (payments == null || payments.isEmpty()) {
            this.status = PolicyStatus.UNPAID;
            return;
        }
        double totalPaid = payments.stream()
            .filter(p -> "completed".equalsIgnoreCase(p.getStatus()))
            .mapToDouble(Payment::getAmount)
            .sum();
        if (totalPaid == 0) {
            this.status = PolicyStatus.UNPAID;
        } else if (totalPaid < this.premium) {
            this.status = PolicyStatus.PARTIALLY_PAID;
        } else {
            this.status = PolicyStatus.PAID;
        }
    }
}
