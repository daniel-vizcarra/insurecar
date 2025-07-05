package com.insurancecorp.insurecar.service;

import com.insurancecorp.insurecar.model.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * Servicio para manejar la lógica de negocio del sistema de seguros
 */
public class InsuranceService {
    
    /**
     * Calcula la prima de seguro basada en varios factores
     * @param customer Cliente
     * @param vehicle Vehículo
     * @param coverage Cobertura
     * @param durationInMonths Duración en meses
     * @return prima calculada
     */
    public double calculatePremium(Customer customer, Vehicle vehicle, Coverage coverage, int durationInMonths) {
        if (customer == null || vehicle == null || coverage == null || durationInMonths <= 0) {
            return 0.0;
        }
        
        double basePremium = coverage.getBasePremium() != null ? coverage.getBasePremium() : 0.0;
        double finalPremium = basePremium;
        
        // Factor por edad del conductor
        int customerAge = customer.getAge();
        if (customerAge < 25) {
            finalPremium *= 1.5; // 50% más para conductores jóvenes
        } else if (customerAge < 30) {
            finalPremium *= 1.3; // 30% más para conductores menores de 30
        } else if (customerAge > 65) {
            finalPremium *= 1.2; // 20% más para conductores mayores
        }
        
        // Factor por antigüedad del vehículo
        int vehicleAge = vehicle.getVehicleAge();
        if (vehicleAge > 10) {
            finalPremium *= 1.4; // 40% más para vehículos antiguos
        } else if (vehicleAge > 5) {
            finalPremium *= 1.2; // 20% más para vehículos de más de 5 años
        }
        
        // Factor por duración
        if (durationInMonths >= 12) {
            finalPremium *= 0.9; // 10% de descuento para pólizas anuales
        }
        
        return Math.round(finalPremium * 100.0) / 100.0; // Redondear a 2 decimales
    }
    
    /**
     * Valida si una póliza puede ser creada
     * @param policy Póliza a validar
     * @return lista de errores de validación
     */
    public List<String> validatePolicy(Policy policy) {
        List<String> errors = new ArrayList<>();
        
        if (policy == null) {
            errors.add("La póliza no puede ser nula");
            return errors;
        }
        
        // Validar cliente
        if (policy.getCustomer() == null) {
            errors.add("El cliente es obligatorio");
        } else if (!policy.getCustomer().isEligibleForInsurance()) {
            errors.add("El cliente no es elegible para seguro");
        }
        
        // Validar vehículo
        if (policy.getVehicle() == null) {
            errors.add("El vehículo es obligatorio");
        } else if (!policy.getVehicle().isEligibleForInsurance()) {
            errors.add("El vehículo no es elegible para seguro");
        }
        
        // Validar cobertura
        if (policy.getCoverage() == null) {
            errors.add("La cobertura es obligatoria");
        } else if (!policy.getCoverage().getIsActive()) {
            errors.add("La cobertura no está activa");
        }
        
        // Validar fechas
        if (policy.getStartDate() == null) {
            errors.add("La fecha de inicio es obligatoria");
        }
        if (policy.getEndDate() == null) {
            errors.add("La fecha de fin es obligatoria");
        }
        if (policy.getStartDate() != null && policy.getEndDate() != null) {
            if (policy.getStartDate().isAfter(policy.getEndDate())) {
                errors.add("La fecha de inicio no puede ser posterior a la fecha de fin");
            }
            if (policy.getStartDate().isBefore(LocalDate.now())) {
                errors.add("La fecha de inicio no puede ser en el pasado");
            }
        }
        
        // Validar prima
        if (policy.getPremium() == null || policy.getPremium() <= 0) {
            errors.add("La prima debe ser mayor a 0");
        }
        
        return errors;
    }
    
    /**
     * Procesa un pago y actualiza el estado de la póliza
     * @param payment Pago a procesar
     * @return true si el pago fue procesado exitosamente
     */
    public boolean processPayment(Payment payment) {
        if (payment == null || payment.getPolicy() == null) {
            return false;
        }
        
        Policy policy = payment.getPolicy();
        
        // Validar monto del pago
        if (payment.getAmount() == null || payment.getAmount() <= 0) {
            payment.setStatus("failed");
            return false;
        }
        
        // Verificar que no se exceda el monto de la póliza
        double remainingAmount = policy.getRemainingAmount();
        if (payment.getAmount() > remainingAmount) {
            payment.setStatus("failed");
            return false;
        }
        
        // Procesar el pago
        payment.setStatus("completed");
        payment.setPaymentDate(LocalDate.now());
        
        // Actualizar estado de la póliza
        policy.updateStatusByPayments();
        
        return true;
    }
    
    /**
     * Cancela una póliza
     * @param policy Póliza a cancelar
     * @return true si la póliza fue cancelada exitosamente
     */
    public boolean cancelPolicy(Policy policy) {
        if (policy == null) {
            return false;
        }
        
        // Solo se pueden cancelar pólizas activas
        if (!policy.isActive()) {
            return false;
        }
        
        policy.setStatus(PolicyStatus.CANCELLED);
        return true;
    }
    
    /**
     * Renueva una póliza
     * @param policy Póliza a renovar
     * @param newEndDate Nueva fecha de fin
     * @return nueva póliza renovada
     */
    public Policy renewPolicy(Policy policy, LocalDate newEndDate) {
        if (policy == null || newEndDate == null || newEndDate.isBefore(LocalDate.now())) {
            return null;
        }
        
        // Solo se pueden renovar pólizas activas
        if (!policy.isActive()) {
            return null;
        }
        
        Policy renewedPolicy = new Policy();
        renewedPolicy.setCustomer(policy.getCustomer());
        renewedPolicy.setVehicle(policy.getVehicle());
        renewedPolicy.setCoverage(policy.getCoverage());
        renewedPolicy.setStartDate(policy.getEndDate().plusDays(1));
        renewedPolicy.setEndDate(newEndDate);
        renewedPolicy.setPremium(policy.getPremium());
        renewedPolicy.setPolicyNumber(generatePolicyNumber());
        
        return renewedPolicy;
    }
    
    /**
     * Genera un número de póliza único
     * @return número de póliza
     */
    private String generatePolicyNumber() {
        return "POL-" + String.format("%06d", (int)(Math.random() * 999999));
    }
} 