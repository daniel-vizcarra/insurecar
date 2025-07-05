package com.insurancecorp.insurecar.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.Period;
import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Getter
@Setter
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "El cliente es obligatorio")
    private Customer customer;

    @ManyToOne
    @NotNull(message = "El vehículo es obligatorio")
    private Vehicle vehicle;

    @NotBlank(message = "El número de póliza es obligatorio")
    @Pattern(regexp = "^POL-[0-9]{6}$", message = "El número de póliza debe tener formato POL-XXXXXX")
    private String policyNumber;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio debe ser presente o futura")
    private LocalDate startDate;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    private LocalDate endDate;

    @NotNull(message = "La prima es obligatoria")
    @DecimalMin(value = "0.01", message = "La prima debe ser mayor a 0")
    @DecimalMax(value = "100000.00", message = "La prima no puede exceder $100,000")
    private Double premium;

    @ManyToOne
    @NotNull(message = "La cobertura es obligatoria")
    private Coverage coverage;

    @OneToMany(mappedBy = "policy")
    private java.util.List<Payment> payments;

    private LocalDate createdAt;
    private LocalDate updatedAt;

    @Enumerated(EnumType.STRING)
    private PolicyStatus status = PolicyStatus.UNPAID;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
        if (status == null) {
            status = PolicyStatus.UNPAID;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }

    /**
     * Actualiza el estado de la póliza basado en los pagos realizados
     */
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

    /**
     * Calcula la duración de la póliza en días
     * @return duración en días
     */
    public int getDurationInDays() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * Verifica si la póliza está activa
     * @return true si está activa
     */
    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return startDate != null && endDate != null &&
               !startDate.isAfter(today) && !endDate.isBefore(today) &&
               status != PolicyStatus.CANCELLED;
    }

    /**
     * Verifica si la póliza está vencida
     * @return true si está vencida
     */
    public boolean isExpired() {
        return endDate != null && endDate.isBefore(LocalDate.now());
    }

    /**
     * Calcula el monto restante por pagar
     * @return monto restante
     */
    public double getRemainingAmount() {
        if (payments == null || payments.isEmpty()) {
            return premium != null ? premium : 0.0;
        }
        double totalPaid = payments.stream()
            .filter(p -> "completed".equalsIgnoreCase(p.getStatus()))
            .mapToDouble(Payment::getAmount)
            .sum();
        return Math.max(0, (premium != null ? premium : 0.0) - totalPaid);
    }

    /**
     * Verifica si la póliza es elegible para ser creada
     * @return true si es elegible
     */
    public boolean isEligibleForCreation() {
        return customer != null && customer.isEligibleForInsurance() &&
               vehicle != null && vehicle.isEligibleForInsurance() &&
               coverage != null && coverage.getIsActive() &&
               startDate != null && endDate != null &&
               startDate.isBefore(endDate) &&
               premium != null && premium > 0;
    }
}
