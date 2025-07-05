package com.insurancecorp.insurecar.model;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Pruebas de caja blanca para el modelo Policy
 */
public class PolicyTest {
    
    private Policy policy;
    private Customer customer;
    private Vehicle vehicle;
    private Coverage coverage;
    
    @Before
    public void setUp() {
        customer = new Customer();
        customer.setFirstName("Ana");
        customer.setLastName("Martínez");
        customer.setEmail("ana.martinez@email.com");
        customer.setDateOfBirth(LocalDate.of(1985, 6, 15));
        
        vehicle = new Vehicle();
        vehicle.setVin("1HGBH41JXMN109186");
        vehicle.setMake("Ford");
        vehicle.setModel("Focus");
        vehicle.setYear("2019");
        vehicle.setOwner(customer);
        
        coverage = new Coverage();
        coverage.setName("Cobertura Completa");
        coverage.setBasePremium(800.0);
        coverage.setIsActive(true);
        
        policy = new Policy();
        policy.setCustomer(customer);
        policy.setVehicle(vehicle);
        policy.setCoverage(coverage);
        policy.setPolicyNumber("POL-123456");
        policy.setStartDate(LocalDate.now());
        policy.setEndDate(LocalDate.now().plusYears(1));
        policy.setPremium(1000.0);
    }
    
    @Test
    public void testUpdateStatusByPayments_NoPayments() {
        // Prueba sin pagos
        policy.setPayments(null);
        policy.updateStatusByPayments();
        assertEquals("El estado debe ser UNPAID sin pagos", PolicyStatus.UNPAID, policy.getStatus());
    }
    
    @Test
    public void testUpdateStatusByPayments_EmptyPayments() {
        // Prueba con lista de pagos vacía
        policy.setPayments(new ArrayList<>());
        policy.updateStatusByPayments();
        assertEquals("El estado debe ser UNPAID con pagos vacíos", PolicyStatus.UNPAID, policy.getStatus());
    }
    
    @Test
    public void testUpdateStatusByPayments_UnpaidStatus() {
        // Prueba con pagos fallidos
        List<Payment> payments = new ArrayList<>();
        Payment failedPayment = new Payment();
        failedPayment.setAmount(100.0);
        failedPayment.setStatus("failed");
        payments.add(failedPayment);
        policy.setPayments(payments);
        
        policy.updateStatusByPayments();
        assertEquals("El estado debe ser UNPAID con pagos fallidos", PolicyStatus.UNPAID, policy.getStatus());
    }
    
    @Test
    public void testUpdateStatusByPayments_PartiallyPaid() {
        // Prueba con pago parcial
        List<Payment> payments = new ArrayList<>();
        Payment partialPayment = new Payment();
        partialPayment.setAmount(500.0);
        partialPayment.setStatus("completed");
        payments.add(partialPayment);
        policy.setPayments(payments);
        
        policy.updateStatusByPayments();
        assertEquals("El estado debe ser PARTIALLY_PAID con pago parcial", 
                    PolicyStatus.PARTIALLY_PAID, policy.getStatus());
    }
    
    @Test
    public void testUpdateStatusByPayments_FullyPaid() {
        // Prueba con pago completo
        List<Payment> payments = new ArrayList<>();
        Payment fullPayment = new Payment();
        fullPayment.setAmount(1000.0);
        fullPayment.setStatus("completed");
        payments.add(fullPayment);
        policy.setPayments(payments);
        
        policy.updateStatusByPayments();
        assertEquals("El estado debe ser PAID con pago completo", PolicyStatus.PAID, policy.getStatus());
    }
    
    @Test
    public void testUpdateStatusByPayments_Overpaid() {
        // Prueba con pago excesivo
        List<Payment> payments = new ArrayList<>();
        Payment overPayment = new Payment();
        overPayment.setAmount(1200.0);
        overPayment.setStatus("completed");
        payments.add(overPayment);
        policy.setPayments(payments);
        
        policy.updateStatusByPayments();
        assertEquals("El estado debe ser PAID con pago excesivo", PolicyStatus.PAID, policy.getStatus());
    }
    
    @Test
    public void testGetDurationInDays_ValidDates() {
        // Prueba cálculo de duración con fechas válidas
        int duration = policy.getDurationInDays();
        assertTrue("La duración debe ser mayor a 0", duration > 0);
        assertTrue("La duración debe ser aproximadamente 365 días", duration >= 365);
    }
    
    @Test
    public void testGetDurationInDays_NullStartDate() {
        // Prueba con fecha de inicio nula
        policy.setStartDate(null);
        int duration = policy.getDurationInDays();
        assertEquals("La duración debe ser 0 con fecha de inicio nula", 0, duration);
    }
    
    @Test
    public void testGetDurationInDays_NullEndDate() {
        // Prueba con fecha de fin nula
        policy.setEndDate(null);
        int duration = policy.getDurationInDays();
        assertEquals("La duración debe ser 0 con fecha de fin nula", 0, duration);
    }
    
    @Test
    public void testIsActive_ActivePolicy() {
        // Prueba con póliza activa
        assertTrue("Póliza válida debe estar activa", policy.isActive());
    }
    
    @Test
    public void testIsActive_FutureStartDate() {
        // Prueba con fecha de inicio futura
        policy.setStartDate(LocalDate.now().plusDays(1));
        assertFalse("Póliza con fecha de inicio futura no debe estar activa", policy.isActive());
    }
    
    @Test
    public void testIsActive_ExpiredEndDate() {
        // Prueba con fecha de fin vencida
        policy.setEndDate(LocalDate.now().minusDays(1));
        assertFalse("Póliza vencida no debe estar activa", policy.isActive());
    }
    
    @Test
    public void testIsActive_CancelledPolicy() {
        // Prueba con póliza cancelada
        policy.setStatus(PolicyStatus.CANCELLED);
        assertFalse("Póliza cancelada no debe estar activa", policy.isActive());
    }
    
    @Test
    public void testIsActive_NullDates() {
        // Prueba con fechas nulas
        policy.setStartDate(null);
        policy.setEndDate(null);
        assertFalse("Póliza con fechas nulas no debe estar activa", policy.isActive());
    }
    
    @Test
    public void testIsExpired_NotExpired() {
        // Prueba con póliza no vencida
        assertFalse("Póliza no vencida debe retornar false", policy.isExpired());
    }
    
    @Test
    public void testIsExpired_Expired() {
        // Prueba con póliza vencida
        policy.setEndDate(LocalDate.now().minusDays(1));
        assertTrue("Póliza vencida debe retornar true", policy.isExpired());
    }
    
    @Test
    public void testIsExpired_NullEndDate() {
        // Prueba con fecha de fin nula
        policy.setEndDate(null);
        assertFalse("Póliza con fecha de fin nula no debe estar vencida", policy.isExpired());
    }
    
    @Test
    public void testGetRemainingAmount_NoPayments() {
        // Prueba sin pagos
        policy.setPayments(null);
        double remaining = policy.getRemainingAmount();
        assertEquals("El monto restante debe ser igual a la prima", 1000.0, remaining, 0.01);
    }
    
    @Test
    public void testGetRemainingAmount_EmptyPayments() {
        // Prueba con pagos vacíos
        policy.setPayments(new ArrayList<>());
        double remaining = policy.getRemainingAmount();
        assertEquals("El monto restante debe ser igual a la prima", 1000.0, remaining, 0.01);
    }
    
    @Test
    public void testGetRemainingAmount_PartialPayment() {
        // Prueba con pago parcial
        List<Payment> payments = new ArrayList<>();
        Payment payment = new Payment();
        payment.setAmount(300.0);
        payment.setStatus("completed");
        payments.add(payment);
        policy.setPayments(payments);
        
        double remaining = policy.getRemainingAmount();
        assertEquals("El monto restante debe ser correcto", 700.0, remaining, 0.01);
    }
    
    @Test
    public void testGetRemainingAmount_FailedPayment() {
        // Prueba con pago fallido
        List<Payment> payments = new ArrayList<>();
        Payment payment = new Payment();
        payment.setAmount(300.0);
        payment.setStatus("failed");
        payments.add(payment);
        policy.setPayments(payments);
        
        double remaining = policy.getRemainingAmount();
        assertEquals("El monto restante debe ser igual a la prima", 1000.0, remaining, 0.01);
    }
    
    @Test
    public void testGetRemainingAmount_NullPremium() {
        // Prueba con prima nula
        policy.setPremium(null);
        policy.setPayments(new ArrayList<>());
        double remaining = policy.getRemainingAmount();
        assertEquals("El monto restante debe ser 0 con prima nula", 0.0, remaining, 0.01);
    }
    
    @Test
    public void testIsEligibleForCreation_ValidPolicy() {
        // Prueba con póliza elegible
        assertTrue("Póliza válida debe ser elegible", policy.isEligibleForCreation());
    }
    
    @Test
    public void testIsEligibleForCreation_NullCustomer() {
        // Prueba con cliente nulo
        policy.setCustomer(null);
        assertFalse("Póliza sin cliente no debe ser elegible", policy.isEligibleForCreation());
    }
    
    @Test
    public void testIsEligibleForCreation_InvalidCustomer() {
        // Prueba con cliente inválido
        customer.setDateOfBirth(LocalDate.of(2010, 1, 1)); // Menor de edad
        assertFalse("Póliza con cliente menor de edad no debe ser elegible", policy.isEligibleForCreation());
    }
    
    @Test
    public void testIsEligibleForCreation_NullVehicle() {
        // Prueba con vehículo nulo
        policy.setVehicle(null);
        assertFalse("Póliza sin vehículo no debe ser elegible", policy.isEligibleForCreation());
    }
    
    @Test
    public void testIsEligibleForCreation_InvalidVehicle() {
        // Prueba con vehículo inválido
        vehicle.setVin(null);
        assertFalse("Póliza con vehículo inválido no debe ser elegible", policy.isEligibleForCreation());
    }
    
    @Test
    public void testIsEligibleForCreation_NullCoverage() {
        // Prueba con cobertura nula
        policy.setCoverage(null);
        assertFalse("Póliza sin cobertura no debe ser elegible", policy.isEligibleForCreation());
    }
    
    @Test
    public void testIsEligibleForCreation_InactiveCoverage() {
        // Prueba con cobertura inactiva
        coverage.setIsActive(false);
        assertFalse("Póliza con cobertura inactiva no debe ser elegible", policy.isEligibleForCreation());
    }
    
    @Test
    public void testIsEligibleForCreation_InvalidDates() {
        // Prueba con fechas inválidas
        policy.setStartDate(LocalDate.now().plusDays(1));
        policy.setEndDate(LocalDate.now());
        assertFalse("Póliza con fechas inválidas no debe ser elegible", policy.isEligibleForCreation());
    }
    
    @Test
    public void testIsEligibleForCreation_InvalidPremium() {
        // Prueba con prima inválida
        policy.setPremium(0.0);
        assertFalse("Póliza con prima inválida no debe ser elegible", policy.isEligibleForCreation());
    }
    
    @Test
    public void testOnCreate() {
        // Prueba que se establezcan las fechas de creación
        Policy newPolicy = new Policy();
        newPolicy.onCreate();
        
        assertNotNull("La fecha de creación no debe ser nula", newPolicy.getCreatedAt());
        assertNotNull("La fecha de actualización no debe ser nula", newPolicy.getUpdatedAt());
        assertEquals("Las fechas deben ser iguales al crear", 
                    newPolicy.getCreatedAt(), newPolicy.getUpdatedAt());
        assertEquals("El estado debe ser UNPAID por defecto", PolicyStatus.UNPAID, newPolicy.getStatus());
    }
    
    @Test
    public void testOnUpdate() {
        // Prueba que se actualice la fecha de actualización
        LocalDate originalDate = LocalDate.now().minusDays(1);
        policy.setUpdatedAt(originalDate);
        
        policy.onUpdate();
        
        assertNotNull("La fecha de actualización no debe ser nula", policy.getUpdatedAt());
        assertNotEquals("La fecha de actualización debe cambiar", originalDate, policy.getUpdatedAt());
    }
} 