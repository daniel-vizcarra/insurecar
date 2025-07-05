package com.insurancecorp.insurecar.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String lastName;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "El teléfono debe tener entre 10 y 15 dígitos")
    private String phone;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate dateOfBirth;

    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    @NotBlank(message = "La ciudad es obligatoria")
    private String city;

    @NotBlank(message = "El estado es obligatorio")
    private String state;

    @Pattern(regexp = "^[0-9]{5}(-[0-9]{4})?$", message = "El código postal debe tener formato válido")
    private String zipCode;

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
     * Calcula la edad del cliente
     * @return edad en años
     */
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Verifica si el cliente es mayor de edad
     * @return true si es mayor de edad
     */
    public boolean isAdult() {
        return getAge() >= 18;
    }

    /**
     * Obtiene el nombre completo del cliente
     * @return nombre completo
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Valida si el cliente puede obtener un seguro
     * @return true si es elegible
     */
    public boolean isEligibleForInsurance() {
        return isAdult() && 
               firstName != null && !firstName.trim().isEmpty() &&
               lastName != null && !lastName.trim().isEmpty() &&
               email != null && !email.trim().isEmpty();
    }
}
