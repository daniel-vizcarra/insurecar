package com.insurancecorp.insurecar.model;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.time.LocalDate;

/**
 * Pruebas de caja blanca para el modelo Customer
 */
public class CustomerTest {
    
    private Customer customer;
    
    @Before
    public void setUp() {
        customer = new Customer();
        customer.setFirstName("María");
        customer.setLastName("García");
        customer.setEmail("maria.garcia@email.com");
        customer.setDateOfBirth(LocalDate.of(1985, 8, 20));
        customer.setAddress("Avenida Central 456");
        customer.setCity("Ciudad Grande");
        customer.setState("Estado Grande");
        customer.setZipCode("54321");
        customer.setPhone("+1234567890");
    }
    
    @Test
    public void testGetAge_ValidDateOfBirth() {
        // Prueba cálculo de edad con fecha válida
        int age = customer.getAge();
        assertTrue("La edad debe ser mayor a 0", age > 0);
        assertTrue("La edad debe ser razonable", age < 150);
    }
    
    @Test
    public void testGetAge_NullDateOfBirth() {
        // Prueba cálculo de edad con fecha nula
        customer.setDateOfBirth(null);
        int age = customer.getAge();
        assertEquals("La edad debe ser 0 cuando la fecha de nacimiento es nula", 0, age);
    }
    
    @Test
    public void testIsAdult_AdultCustomer() {
        // Prueba con cliente mayor de edad
        customer.setDateOfBirth(LocalDate.of(1990, 1, 1));
        assertTrue("Cliente mayor de edad debe retornar true", customer.isAdult());
    }
    
    @Test
    public void testIsAdult_YoungCustomer() {
        // Prueba con cliente menor de edad
        customer.setDateOfBirth(LocalDate.of(2010, 1, 1));
        assertFalse("Cliente menor de edad debe retornar false", customer.isAdult());
    }
    
    @Test
    public void testIsAdult_Exactly18() {
        // Prueba con cliente que cumple exactamente 18 años
        LocalDate eighteenYearsAgo = LocalDate.now().minusYears(18);
        customer.setDateOfBirth(eighteenYearsAgo);
        assertTrue("Cliente de exactamente 18 años debe retornar true", customer.isAdult());
    }
    
    @Test
    public void testGetFullName_ValidNames() {
        // Prueba obtención de nombre completo
        String fullName = customer.getFullName();
        assertEquals("El nombre completo debe ser correcto", "María García", fullName);
    }
    
    @Test
    public void testGetFullName_NullFirstName() {
        // Prueba con nombre nulo
        customer.setFirstName(null);
        String fullName = customer.getFullName();
        assertEquals("El nombre completo debe manejar nombre nulo", "null García", fullName);
    }
    
    @Test
    public void testGetFullName_NullLastName() {
        // Prueba con apellido nulo
        customer.setLastName(null);
        String fullName = customer.getFullName();
        assertEquals("El nombre completo debe manejar apellido nulo", "María null", fullName);
    }
    
    @Test
    public void testIsEligibleForInsurance_ValidCustomer() {
        // Prueba con cliente elegible
        assertTrue("Cliente válido debe ser elegible", customer.isEligibleForInsurance());
    }
    
    @Test
    public void testIsEligibleForInsurance_YoungCustomer() {
        // Prueba con cliente menor de edad
        customer.setDateOfBirth(LocalDate.of(2010, 1, 1));
        assertFalse("Cliente menor de edad no debe ser elegible", customer.isEligibleForInsurance());
    }
    
    @Test
    public void testIsEligibleForInsurance_NullFirstName() {
        // Prueba con nombre nulo
        customer.setFirstName(null);
        assertFalse("Cliente sin nombre no debe ser elegible", customer.isEligibleForInsurance());
    }
    
    @Test
    public void testIsEligibleForInsurance_EmptyFirstName() {
        // Prueba con nombre vacío
        customer.setFirstName("");
        assertFalse("Cliente con nombre vacío no debe ser elegible", customer.isEligibleForInsurance());
    }
    
    @Test
    public void testIsEligibleForInsurance_WhitespaceFirstName() {
        // Prueba con nombre con espacios
        customer.setFirstName("   ");
        assertFalse("Cliente con nombre con espacios no debe ser elegible", customer.isEligibleForInsurance());
    }
    
    @Test
    public void testIsEligibleForInsurance_NullLastName() {
        // Prueba con apellido nulo
        customer.setLastName(null);
        assertFalse("Cliente sin apellido no debe ser elegible", customer.isEligibleForInsurance());
    }
    
    @Test
    public void testIsEligibleForInsurance_NullEmail() {
        // Prueba con email nulo
        customer.setEmail(null);
        assertFalse("Cliente sin email no debe ser elegible", customer.isEligibleForInsurance());
    }
    
    @Test
    public void testIsEligibleForInsurance_EmptyEmail() {
        // Prueba con email vacío
        customer.setEmail("");
        assertFalse("Cliente con email vacío no debe ser elegible", customer.isEligibleForInsurance());
    }
    
    @Test
    public void testOnCreate() {
        // Prueba que se establezcan las fechas de creación
        Customer newCustomer = new Customer();
        newCustomer.onCreate();
        
        assertNotNull("La fecha de creación no debe ser nula", newCustomer.getCreatedAt());
        assertNotNull("La fecha de actualización no debe ser nula", newCustomer.getUpdatedAt());
        assertEquals("Las fechas deben ser iguales al crear", 
                    newCustomer.getCreatedAt(), newCustomer.getUpdatedAt());
    }
    
    @Test
    public void testOnUpdate() {
        // Prueba que se actualice la fecha de actualización
        LocalDate originalDate = LocalDate.now().minusDays(1);
        customer.setUpdatedAt(originalDate);
        
        customer.onUpdate();
        
        assertNotNull("La fecha de actualización no debe ser nula", customer.getUpdatedAt());
        assertNotEquals("La fecha de actualización debe cambiar", originalDate, customer.getUpdatedAt());
    }
} 