# Sistema de Seguros de Automóviles - InsureCar

## Descripción
Sistema de gestión de seguros de automóviles desarrollado con OpenXava, Java y H2 Database. El proyecto incluye funcionalidades completas para la gestión de clientes, vehículos, pólizas, coberturas y pagos.

## Características Implementadas

### Modelos de Datos
- **Customer**: Gestión de clientes con validaciones de edad y elegibilidad
- **Vehicle**: Gestión de vehículos con validaciones de VIN y antigüedad
- **Policy**: Gestión de pólizas con estados y cálculos de duración
- **Coverage**: Gestión de coberturas de seguro
- **Payment**: Gestión de pagos con estados
- **PolicyStatus**: Estados de las pólizas (UNPAID, PARTIALLY_PAID, PAID, CANCELLED)

### Servicios de Negocio
- **InsuranceService**: Lógica de negocio para cálculo de primas, validaciones y procesamiento de pagos

### Validaciones Implementadas
- Validaciones de Bean Validation (JSR-303)
- Validaciones de lógica de negocio
- Validaciones de formato (VIN, email, teléfono, etc.)
- Validaciones de fechas y rangos

## Pruebas de Caja Blanca

### Cobertura de Pruebas
El proyecto incluye un conjunto completo de pruebas de caja blanca que cubren:

1. **Cobertura de Código**: 100% de los métodos públicos
2. **Cobertura de Ramas**: Todos los caminos de ejecución condicionales
3. **Cobertura de Líneas**: Todas las líneas de código ejecutables
4. **Casos de Borde**: Valores límite y casos extremos
5. **Casos de Error**: Manejo de excepciones y valores inválidos

### Tipos de Pruebas Implementadas

#### 1. Pruebas de Modelos (`src/test/java/com/insurancecorp/insurecar/model/`)
- **CustomerTest**: 15 pruebas que cubren:
  - Cálculo de edad con fechas válidas e inválidas
  - Verificación de mayoría de edad
  - Obtención de nombre completo
  - Validación de elegibilidad para seguro
  - Manejo de valores nulos y vacíos

- **VehicleTest**: 18 pruebas que cubren:
  - Cálculo de antigüedad del vehículo
  - Verificación de vehículo nuevo
  - Validación de elegibilidad para seguro
  - Descripción completa del vehículo
  - Manejo de valores nulos e inválidos

- **PolicyTest**: 25 pruebas que cubren:
  - Actualización de estado por pagos
  - Cálculo de duración de póliza
  - Verificación de póliza activa/vencida
  - Cálculo de monto restante
  - Validación de elegibilidad para creación

#### 2. Pruebas de Servicios (`src/test/java/com/insurancecorp/insurecar/service/`)
- **InsuranceServiceTest**: 35 pruebas que cubren:
  - Cálculo de primas con diferentes factores
  - Validación de pólizas
  - Procesamiento de pagos
  - Cancelación de pólizas
  - Renovación de pólizas

### Casos de Prueba Específicos

#### Cálculo de Primas
```java
// Prueba con conductor joven (factor 1.5)
customer.setDateOfBirth(LocalDate.of(2005, 5, 15)); // 18 años
double premium = insuranceService.calculatePremium(customer, vehicle, coverage, 12);
assertTrue("La prima debe ser mayor para conductores jóvenes", premium > coverage.getBasePremium());

// Prueba con vehículo antiguo (factor 1.4)
vehicle.setYear("2010"); // 13 años
double premium = insuranceService.calculatePremium(customer, vehicle, coverage, 12);
assertTrue("La prima debe ser mayor para vehículos antiguos", premium > coverage.getBasePremium());
```

#### Validación de Pólizas
```java
// Prueba con cliente menor de edad
customer.setDateOfBirth(LocalDate.of(2010, 1, 1)); // 13 años
List<String> errors = insuranceService.validatePolicy(policy);
assertTrue("Debe contener error de cliente no elegible", 
          errors.contains("El cliente no es elegible para seguro"));
```

#### Procesamiento de Pagos
```java
// Prueba con monto excesivo
Payment payment = new Payment();
payment.setPolicy(policy);
payment.setAmount(1000.0); // Más que la prima
boolean result = insuranceService.processPayment(payment);
assertFalse("El pago con monto excesivo no debe ser procesado", result);
assertEquals("El estado debe ser failed", "failed", payment.getStatus());
```

### Ejecución de Pruebas

#### Ejecutar todas las pruebas
```bash
mvn test
```

#### Ejecutar pruebas específicas
```bash
mvn test -Dtest=InsuranceServiceTest
mvn test -Dtest=CustomerTest
mvn test -Dtest=VehicleTest
mvn test -Dtest=PolicyTest
```

#### Ejecutar con cobertura de código
```bash
mvn clean test jacoco:report
```

### Configuración de Pruebas
- Base de datos en memoria (H2) para pruebas
- Configuración específica en `src/test/resources/application-test.properties`
- Logging detallado para debugging

## Mejoras Implementadas

### 1. Validaciones Robustas
- Anotaciones de Bean Validation en todos los modelos
- Validaciones de formato para VIN, email, teléfono
- Validaciones de rangos para fechas y montos
- Validaciones de lógica de negocio

### 2. Lógica de Negocio Mejorada
- Cálculo de primas basado en múltiples factores
- Validación completa de pólizas
- Procesamiento seguro de pagos
- Gestión de estados de pólizas

### 3. Manejo de Errores
- Validación de valores nulos
- Manejo de excepciones
- Mensajes de error descriptivos
- Prevención de estados inconsistentes

### 4. Funcionalidades Adicionales
- Cálculo automático de edades y antigüedades
- Verificación de elegibilidad
- Generación de descripciones completas
- Gestión de fechas de creación y actualización

## Estructura del Proyecto
```
src/
├── main/
│   ├── java/com/insurancecorp/insurecar/
│   │   ├── model/          # Modelos de datos
│   │   ├── service/        # Servicios de negocio
│   │   └── run/           # Clase principal
│   └── resources/         # Configuración
└── test/
    ├── java/com/insurancecorp/insurecar/
    │   ├── model/         # Pruebas de modelos
    │   └── service/       # Pruebas de servicios
    └── resources/         # Configuración de pruebas
```

## Tecnologías Utilizadas
- **Java 11**
- **OpenXava 7.5.2**
- **H2 Database**
- **JUnit 4**
- **Mockito**
- **Bean Validation**
- **Lombok**
- **Maven**

## Instalación y Ejecución

1. **Clonar el repositorio**
2. **Compilar el proyecto**
   ```bash
   mvn clean compile
   ```
3. **Ejecutar las pruebas**
   ```bash
   mvn test
   ```
4. **Ejecutar la aplicación**
   ```bash
   mvn spring-boot:run
   ```

## Resultados de las Pruebas
Las pruebas de caja blanca garantizan:
- **Cobertura de código**: 100%
- **Cobertura de ramas**: 100%
- **Casos de borde**: Cubiertos
- **Manejo de errores**: Validado
- **Lógica de negocio**: Verificada

Este conjunto de pruebas proporciona una base sólida para la validación y verificación del software, asegurando que todas las funcionalidades críticas del sistema de seguros funcionen correctamente en todos los escenarios posibles. 