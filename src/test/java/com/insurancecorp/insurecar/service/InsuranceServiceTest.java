package com.insurancecorp.insurecar.service;

import com.insurancecorp.insurecar.model.*;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Pruebas de caja blanca para InsuranceService
 * Estas pruebas cubren todos los caminos de ejecución del código
 */
public class InsuranceServiceTest {
    
    private InsuranceService insuranceService;
    private Customer customer;
    private Vehicle vehicle;
    private Coverage coverage;
    private Policy policy;
    
    @Before
    public void setUp() {
        insuranceService = new InsuranceService();
        
        // Configurar datos de prueba
        customer = new Customer();
        customer.setFirstName("Juan");
        customer.setLastName("Pérez");
        customer.setEmail("juan.perez@email.com");
        customer.setDateOfBirth(LocalDate.of(1990, 5, 15));
        customer.setAddress("Calle Principal 123");
        customer.setCity("Ciudad");
        customer.setState("Estado");
        customer.setZipCode("12345");
        
        vehicle = new Vehicle();
        vehicle.setVin("1HGBH41JXMN109186");
        vehicle.setMake("Honda");
        vehicle.setModel("Civic");
        vehicle.setYear("2020");
        vehicle.setLicensePlate("ABC123");
        vehicle.setColor("Azul");
        vehicle.setOwner(customer);
        
        coverage = new Coverage();
        coverage.setName("Cobertura Básica");
        coverage.setDescription("Cobertura básica de responsabilidad civil");
        coverage.setBasePremium(500.0);
        coverage.setIsActive(true);
        
        policy = new Policy();
        policy.setCustomer(customer);
        policy.setVehicle(vehicle);
        policy.setCoverage(coverage);
        policy.setPolicyNumber("POL-123456");
        policy.setStartDate(LocalDate.now());
        policy.setEndDate(LocalDate.now().plusYears(1));
        policy.setPremium(600.0);
    }
    
    // ========== PRUEBAS PARA calculatePremium ==========
    
    @Test
    public void testCalculatePremium_ValidInputs() {
        // Prueba con datos válidos
        double premium = insuranceService.calculatePremium(customer, vehicle, coverage, 12);
        assertTrue("La prima debe ser mayor a 0", premium > 0);
    }
    
    @Test
    public void testCalculatePremium_NullCustomer() {
        // Prueba con cliente nulo
        double premium = insuranceService.calculatePremium(null, vehicle, coverage, 12);
        assertEquals("La prima debe ser 0 cuando el cliente es nulo", 0.0, premium, 0.01);
    }
    
    @Test
    public void testCalculatePremium_NullVehicle() {
        // Prueba con vehículo nulo
        double premium = insuranceService.calculatePremium(customer, null, coverage, 12);
        assertEquals("La prima debe ser 0 cuando el vehículo es nulo", 0.0, premium, 0.01);
    }
    
    @Test
    public void testCalculatePremium_NullCoverage() {
        // Prueba con cobertura nula
        double premium = insuranceService.calculatePremium(customer, vehicle, null, 12);
        assertEquals("La prima debe ser 0 cuando la cobertura es nula", 0.0, premium, 0.01);
    }
    
    @Test
    public void testCalculatePremium_InvalidDuration() {
        // Prueba con duración inválida
        double premium = insuranceService.calculatePremium(customer, vehicle, coverage, 0);
        assertEquals("La prima debe ser 0 cuando la duración es inválida", 0.0, premium, 0.01);
    }
    
    @Test
    public void testCalculatePremium_YoungDriver() {
        // Prueba con conductor joven (menor de 25 años)
        customer.setDateOfBirth(LocalDate.of(2005, 5, 15)); // 18 años
        double premium = insuranceService.calculatePremium(customer, vehicle, coverage, 12);
        assertTrue("La prima debe ser mayor para conductores jóvenes", premium > coverage.getBasePremium());
    }
    
    @Test
    public void testCalculatePremium_OldVehicle() {
        // Prueba con vehículo antiguo (más de 10 años)
        vehicle.setYear("2010"); // 13 años
        double premium = insuranceService.calculatePremium(customer, vehicle, coverage, 12);
        assertTrue("La prima debe ser mayor para vehículos antiguos", premium > coverage.getBasePremium());
    }
    
    @Test
    public void testCalculatePremium_AnnualDiscount() {
        // Prueba con descuento anual
        double premium = insuranceService.calculatePremium(customer, vehicle, coverage, 12);
        double monthlyPremium = insuranceService.calculatePremium(customer, vehicle, coverage, 1);
        assertTrue("La prima anual debe ser menor que 12 veces la prima mensual", 
                  premium < (monthlyPremium * 12));
    }
    
    // ========== PRUEBAS PARA validatePolicy ==========
    
    @Test
    public void testValidatePolicy_ValidPolicy() {
        // Prueba con póliza válida
        List<String> errors = insuranceService.validatePolicy(policy);
        assertTrue("No debe haber errores para una póliza válida", errors.isEmpty());
    }
    
    @Test
    public void testValidatePolicy_NullPolicy() {
        // Prueba con póliza nula
        List<String> errors = insuranceService.validatePolicy(null);
        assertFalse("Debe haber errores para una póliza nula", errors.isEmpty());
        assertTrue("Debe contener el error de póliza nula", 
                  errors.contains("La póliza no puede ser nula"));
    }
    
    @Test
    public void testValidatePolicy_NullCustomer() {
        // Prueba con cliente nulo
        policy.setCustomer(null);
        List<String> errors = insuranceService.validatePolicy(policy);
        assertTrue("Debe contener error de cliente nulo", 
                  errors.contains("El cliente es obligatorio"));
    }
    
    @Test
    public void testValidatePolicy_InvalidCustomer() {
        // Prueba con cliente inválido (menor de edad)
        customer.setDateOfBirth(LocalDate.of(2010, 5, 15)); // 13 años
        List<String> errors = insuranceService.validatePolicy(policy);
        assertTrue("Debe contener error de cliente no elegible", 
                  errors.contains("El cliente no es elegible para seguro"));
    }
    
    @Test
    public void testValidatePolicy_NullVehicle() {
        // Prueba con vehículo nulo
        policy.setVehicle(null);
        List<String> errors = insuranceService.validatePolicy(policy);
        assertTrue("Debe contener error de vehículo nulo", 
                  errors.contains("El vehículo es obligatorio"));
    }
    
    @Test
    public void testValidatePolicy_InvalidVehicle() {
        // Prueba con vehículo inválido (sin VIN)
        vehicle.setVin(null);
        List<String> errors = insuranceService.validatePolicy(policy);
        assertTrue("Debe contener error de vehículo no elegible", 
                  errors.contains("El vehículo no es elegible para seguro"));
    }
    
    @Test
    public void testValidatePolicy_NullCoverage() {
        // Prueba con cobertura nula
        policy.setCoverage(null);
        List<String> errors = insuranceService.validatePolicy(policy);
        assertTrue("Debe contener error de cobertura nula", 
                  errors.contains("La cobertura es obligatoria"));
    }
    
    @Test
    public void testValidatePolicy_InactiveCoverage() {
        // Prueba con cobertura inactiva
        coverage.setIsActive(false);
        List<String> errors = insuranceService.validatePolicy(policy);
        assertTrue("Debe contener error de cobertura inactiva", 
                  errors.contains("La cobertura no está activa"));
    }
    
    @Test
    public void testValidatePolicy_InvalidDates() {
        // Prueba con fechas inválidas
        policy.setStartDate(LocalDate.now().plusDays(1));
        policy.setEndDate(LocalDate.now());
        List<String> errors = insuranceService.validatePolicy(policy);
        assertTrue("Debe contener error de fechas inválidas", 
                  errors.contains("La fecha de inicio no puede ser posterior a la fecha de fin"));
    }
    
    @Test
    public void testValidatePolicy_PastStartDate() {
        // Prueba con fecha de inicio en el pasado
        policy.setStartDate(LocalDate.now().minusDays(1));
        List<String> errors = insuranceService.validatePolicy(policy);
        assertTrue("Debe contener error de fecha de inicio en el pasado", 
                  errors.contains("La fecha de inicio no puede ser en el pasado"));
    }
    
    @Test
    public void testValidatePolicy_InvalidPremium() {
        // Prueba con prima inválida
        policy.setPremium(0.0);
        List<String> errors = insuranceService.validatePolicy(policy);
        assertTrue("Debe contener error de prima inválida", 
                  errors.contains("La prima debe ser mayor a 0"));
    }
    
    // ========== PRUEBAS PARA processPayment ==========
    
    @Test
    public void testProcessPayment_ValidPayment() {
        // Prueba con pago válido
        Payment payment = new Payment();
        payment.setPolicy(policy);
        payment.setAmount(300.0);
        payment.setMethod("card");
        
        boolean result = insuranceService.processPayment(payment);
        assertTrue("El pago debe ser procesado exitosamente", result);
        assertEquals("El estado debe ser completed", "completed", payment.getStatus());
    }
    
    @Test
    public void testProcessPayment_NullPayment() {
        // Prueba con pago nulo
        boolean result = insuranceService.processPayment(null);
        assertFalse("El pago nulo no debe ser procesado", result);
    }
    
    @Test
    public void testProcessPayment_NullPolicy() {
        // Prueba con póliza nula
        Payment payment = new Payment();
        payment.setAmount(300.0);
        
        boolean result = insuranceService.processPayment(payment);
        assertFalse("El pago sin póliza no debe ser procesado", result);
    }
    
    @Test
    public void testProcessPayment_InvalidAmount() {
        // Prueba con monto inválido
        Payment payment = new Payment();
        payment.setPolicy(policy);
        payment.setAmount(0.0);
        
        boolean result = insuranceService.processPayment(payment);
        assertFalse("El pago con monto inválido no debe ser procesado", result);
        assertEquals("El estado debe ser failed", "failed", payment.getStatus());
    }
    
    @Test
    public void testProcessPayment_ExcessiveAmount() {
        // Prueba con monto excesivo
        Payment payment = new Payment();
        payment.setPolicy(policy);
        payment.setAmount(1000.0); // Más que la prima
        
        boolean result = insuranceService.processPayment(payment);
        assertFalse("El pago con monto excesivo no debe ser procesado", result);
        assertEquals("El estado debe ser failed", "failed", payment.getStatus());
    }
    
    // ========== PRUEBAS PARA cancelPolicy ==========
    
    @Test
    public void testCancelPolicy_ValidPolicy() {
        // Prueba con póliza válida
        boolean result = insuranceService.cancelPolicy(policy);
        assertTrue("La póliza debe ser cancelada exitosamente", result);
        assertEquals("El estado debe ser CANCELLED", PolicyStatus.CANCELLED, policy.getStatus());
    }
    
    @Test
    public void testCancelPolicy_NullPolicy() {
        // Prueba con póliza nula
        boolean result = insuranceService.cancelPolicy(null);
        assertFalse("No se puede cancelar una póliza nula", result);
    }
    
    @Test
    public void testCancelPolicy_ExpiredPolicy() {
        // Prueba con póliza vencida
        policy.setEndDate(LocalDate.now().minusDays(1));
        boolean result = insuranceService.cancelPolicy(policy);
        assertFalse("No se puede cancelar una póliza vencida", result);
    }
    
    // ========== PRUEBAS PARA renewPolicy ==========
    
    @Test
    public void testRenewPolicy_ValidPolicy() {
        // Prueba con póliza válida
        LocalDate newEndDate = LocalDate.now().plusYears(2);
        Policy renewedPolicy = insuranceService.renewPolicy(policy, newEndDate);
        
        assertNotNull("Debe retornar una nueva póliza", renewedPolicy);
        assertEquals("Debe tener el mismo cliente", policy.getCustomer(), renewedPolicy.getCustomer());
        assertEquals("Debe tener el mismo vehículo", policy.getVehicle(), renewedPolicy.getVehicle());
        assertEquals("Debe tener la misma cobertura", policy.getCoverage(), renewedPolicy.getCoverage());
        assertEquals("Debe tener la nueva fecha de fin", newEndDate, renewedPolicy.getEndDate());
    }
    
    @Test
    public void testRenewPolicy_NullPolicy() {
        // Prueba con póliza nula
        Policy renewedPolicy = insuranceService.renewPolicy(null, LocalDate.now().plusYears(1));
        assertNull("No debe renovar una póliza nula", renewedPolicy);
    }
    
    @Test
    public void testRenewPolicy_NullEndDate() {
        // Prueba con fecha de fin nula
        Policy renewedPolicy = insuranceService.renewPolicy(policy, null);
        assertNull("No debe renovar con fecha de fin nula", renewedPolicy);
    }
    
    @Test
    public void testRenewPolicy_PastEndDate() {
        // Prueba con fecha de fin en el pasado
        Policy renewedPolicy = insuranceService.renewPolicy(policy, LocalDate.now().minusDays(1));
        assertNull("No debe renovar con fecha de fin en el pasado", renewedPolicy);
    }
    
    @Test
    public void testRenewPolicy_InactivePolicy() {
        // Prueba con póliza inactiva
        policy.setEndDate(LocalDate.now().minusDays(1)); // Póliza vencida
        Policy renewedPolicy = insuranceService.renewPolicy(policy, LocalDate.now().plusYears(1));
        assertNull("No debe renovar una póliza inactiva", renewedPolicy);
    }
} 