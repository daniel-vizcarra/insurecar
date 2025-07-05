package com.insurancecorp.insurecar.model;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.time.LocalDate;

/**
 * Pruebas de caja blanca para el modelo Vehicle
 */
public class VehicleTest {
    
    private Vehicle vehicle;
    private Customer owner;
    
    @Before
    public void setUp() {
        owner = new Customer();
        owner.setFirstName("Carlos");
        owner.setLastName("López");
        owner.setEmail("carlos.lopez@email.com");
        owner.setDateOfBirth(LocalDate.of(1980, 3, 10));
        
        vehicle = new Vehicle();
        vehicle.setVin("1HGBH41JXMN109186");
        vehicle.setMake("Toyota");
        vehicle.setModel("Camry");
        vehicle.setYear("2018");
        vehicle.setLicensePlate("XYZ789");
        vehicle.setColor("Plateado");
        vehicle.setOwner(owner);
    }
    
    @Test
    public void testGetVehicleAge_ValidYear() {
        // Prueba cálculo de antigüedad con año válido
        int age = vehicle.getVehicleAge();
        assertTrue("La antigüedad debe ser mayor o igual a 0", age >= 0);
        assertTrue("La antigüedad debe ser razonable", age < 50);
    }
    
    @Test
    public void testGetVehicleAge_InvalidYear() {
        // Prueba cálculo de antigüedad con año inválido
        vehicle.setYear("invalid");
        int age = vehicle.getVehicleAge();
        assertEquals("La antigüedad debe ser 0 cuando el año es inválido", 0, age);
    }
    
    @Test
    public void testGetVehicleAge_NullYear() {
        // Prueba cálculo de antigüedad con año nulo
        vehicle.setYear(null);
        int age = vehicle.getVehicleAge();
        assertEquals("La antigüedad debe ser 0 cuando el año es nulo", 0, age);
    }
    
    @Test
    public void testGetVehicleAge_CurrentYear() {
        // Prueba con año actual
        String currentYear = String.valueOf(LocalDate.now().getYear());
        vehicle.setYear(currentYear);
        int age = vehicle.getVehicleAge();
        assertEquals("La antigüedad debe ser 0 para el año actual", 0, age);
    }
    
    @Test
    public void testIsNewVehicle_NewVehicle() {
        // Prueba con vehículo nuevo (menos de 2 años)
        String currentYear = String.valueOf(LocalDate.now().getYear());
        vehicle.setYear(currentYear);
        assertTrue("Vehículo del año actual debe ser nuevo", vehicle.isNewVehicle());
    }
    
    @Test
    public void testIsNewVehicle_OldVehicle() {
        // Prueba con vehículo antiguo (más de 2 años)
        vehicle.setYear("2015");
        assertFalse("Vehículo de 2015 no debe ser nuevo", vehicle.isNewVehicle());
    }
    
    @Test
    public void testIsNewVehicle_Exactly2Years() {
        // Prueba con vehículo de exactamente 2 años
        String twoYearsAgo = String.valueOf(LocalDate.now().getYear() - 2);
        vehicle.setYear(twoYearsAgo);
        assertTrue("Vehículo de exactamente 2 años debe ser nuevo", vehicle.isNewVehicle());
    }
    
    @Test
    public void testIsEligibleForInsurance_ValidVehicle() {
        // Prueba con vehículo elegible
        assertTrue("Vehículo válido debe ser elegible", vehicle.isEligibleForInsurance());
    }
    
    @Test
    public void testIsEligibleForInsurance_NullVin() {
        // Prueba con VIN nulo
        vehicle.setVin(null);
        assertFalse("Vehículo sin VIN no debe ser elegible", vehicle.isEligibleForInsurance());
    }
    
    @Test
    public void testIsEligibleForInsurance_EmptyVin() {
        // Prueba con VIN vacío
        vehicle.setVin("");
        assertFalse("Vehículo con VIN vacío no debe ser elegible", vehicle.isEligibleForInsurance());
    }
    
    @Test
    public void testIsEligibleForInsurance_NullMake() {
        // Prueba con marca nula
        vehicle.setMake(null);
        assertFalse("Vehículo sin marca no debe ser elegible", vehicle.isEligibleForInsurance());
    }
    
    @Test
    public void testIsEligibleForInsurance_NullModel() {
        // Prueba con modelo nulo
        vehicle.setModel(null);
        assertFalse("Vehículo sin modelo no debe ser elegible", vehicle.isEligibleForInsurance());
    }
    
    @Test
    public void testIsEligibleForInsurance_NullYear() {
        // Prueba con año nulo
        vehicle.setYear(null);
        assertFalse("Vehículo sin año no debe ser elegible", vehicle.isEligibleForInsurance());
    }
    
    @Test
    public void testIsEligibleForInsurance_NullOwner() {
        // Prueba con propietario nulo
        vehicle.setOwner(null);
        assertFalse("Vehículo sin propietario no debe ser elegible", vehicle.isEligibleForInsurance());
    }
    
    @Test
    public void testGetFullDescription_ValidVehicle() {
        // Prueba descripción completa con vehículo válido
        String description = vehicle.getFullDescription();
        assertEquals("La descripción debe ser correcta", "2018 Toyota Camry (Plateado)", description);
    }
    
    @Test
    public void testGetFullDescription_NullColor() {
        // Prueba descripción completa con color nulo
        vehicle.setColor(null);
        String description = vehicle.getFullDescription();
        assertEquals("La descripción debe manejar color nulo", "2018 Toyota Camry (null)", description);
    }
    
    @Test
    public void testGetFullDescription_NullYear() {
        // Prueba descripción completa con año nulo
        vehicle.setYear(null);
        String description = vehicle.getFullDescription();
        assertEquals("La descripción debe manejar año nulo", "null Toyota Camry (Plateado)", description);
    }
    
    @Test
    public void testGetFullDescription_NullMake() {
        // Prueba descripción completa con marca nula
        vehicle.setMake(null);
        String description = vehicle.getFullDescription();
        assertEquals("La descripción debe manejar marca nula", "2018 null Camry (Plateado)", description);
    }
    
    @Test
    public void testGetFullDescription_NullModel() {
        // Prueba descripción completa con modelo nulo
        vehicle.setModel(null);
        String description = vehicle.getFullDescription();
        assertEquals("La descripción debe manejar modelo nulo", "2018 Toyota null (Plateado)", description);
    }
    
    @Test
    public void testOnCreate() {
        // Prueba que se establezcan las fechas de creación
        Vehicle newVehicle = new Vehicle();
        newVehicle.onCreate();
        
        assertNotNull("La fecha de creación no debe ser nula", newVehicle.getCreatedAt());
        assertNotNull("La fecha de actualización no debe ser nula", newVehicle.getUpdatedAt());
        assertEquals("Las fechas deben ser iguales al crear", 
                    newVehicle.getCreatedAt(), newVehicle.getUpdatedAt());
    }
    
    @Test
    public void testOnUpdate() {
        // Prueba que se actualice la fecha de actualización
        LocalDate originalDate = LocalDate.now().minusDays(1);
        vehicle.setUpdatedAt(originalDate);
        
        vehicle.onUpdate();
        
        assertNotNull("La fecha de actualización no debe ser nula", vehicle.getUpdatedAt());
        assertNotEquals("La fecha de actualización debe cambiar", originalDate, vehicle.getUpdatedAt());
    }
} 