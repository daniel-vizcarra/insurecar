package com.insurancecorp.insurecar.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El VIN es obligatorio")
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "El VIN debe tener exactamente 17 caracteres alfanuméricos")
    private String vin;

    @NotBlank(message = "La marca es obligatoria")
    @Size(min = 2, max = 50, message = "La marca debe tener entre 2 y 50 caracteres")
    private String make;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(min = 1, max = 50, message = "El modelo debe tener entre 1 y 50 caracteres")
    private String model;

    @NotBlank(message = "El año es obligatorio")
    @Pattern(regexp = "^[0-9]{4}$", message = "El año debe ser un número de 4 dígitos")
    private String year;

    @Pattern(regexp = "^[A-Z0-9]{3,10}$", message = "La placa debe tener entre 3 y 10 caracteres alfanuméricos")
    private String licensePlate;

    @Size(max = 30, message = "El color no puede exceder 30 caracteres")
    private String color;

    @ManyToOne
    @NotNull(message = "El propietario es obligatorio")
    private Customer owner;

    private LocalDate createdAt;
    private LocalDate updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }

    /**
     * Calcula la antigüedad del vehículo
     * @return antigüedad en años
     */
    public int getVehicleAge() {
        try {
            int yearInt = Integer.parseInt(year);
            int currentYear = LocalDate.now().getYear();
            return currentYear - yearInt;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Verifica si el vehículo es nuevo (menos de 2 años)
     * @return true si es nuevo
     */
    public boolean isNewVehicle() {
        return getVehicleAge() <= 2;
    }

    /**
     * Verifica si el vehículo es elegible para seguro
     * @return true si es elegible
     */
    public boolean isEligibleForInsurance() {
        return vin != null && !vin.trim().isEmpty() &&
               make != null && !make.trim().isEmpty() &&
               model != null && !model.trim().isEmpty() &&
               year != null && !year.trim().isEmpty() &&
               owner != null;
    }

    /**
     * Obtiene la descripción completa del vehículo
     * @return descripción completa
     */
    public String getFullDescription() {
        return year + " " + make + " " + model + " (" + color + ")";
    }
}
