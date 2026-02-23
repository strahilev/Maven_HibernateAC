package org.example.service;

import org.example.entities.*;
import org.example.util.JPAUtil;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ShippingServiceTest {

    private static ShippingService service;

    @BeforeAll
    public static void setUp() {
        // Inicializamos el servicio y forzamos la carga del EntityManagerFactory
        service = new ShippingService();
        JPAUtil.getEntityManagerFactory();
    }

    @AfterAll
    public static void tearDown() {
        // Cerramos la conexión al finalizar todos los tests
        JPAUtil.shutdown();
    }

    @Test
    @Order(1)
    @DisplayName("Alta de Buques y Búsqueda por IMO")
    public void testAltaYBquedaBuques() {
        // Preparación: Alta de un Portacontenedores y un Buque Cisterna [cite: 20]
        Portacontenedores pc = new Portacontenedores();
        pc.setCodigoIMO("IMO1111111");
        pc.setNombre("Lázaro Express");
        pc.setCaladoMaximo(15.0);
        pc.setNumeroMaximoTEUs(5000);
        pc.setTipoGrua("Pórtico");

        BuqueCisterna bc = new BuqueCisterna();
        bc.setCodigoIMO("IMO2222222");
        bc.setNombre("Cisterna Lázaro");
        bc.setCaladoMaximo(20.0);
        bc.setCapacidadCarga(150000.0);
        bc.setTipoDobleCasco("Estándar");

        // Ejecución
        assertDoesNotThrow(() -> service.registrarBuque(pc));
        assertDoesNotThrow(() -> service.registrarBuque(bc));

        // Verificación: Búsqueda por código IMO [cite: 21]
        Buque encontrado = service.buscarPorIMO("IMO1111111");
        assertNotNull(encontrado);
        assertEquals("Lázaro Express", encontrado.getNombre());
        assertTrue(encontrado instanceof Portacontenedores);
    }

    @Test
    @Order(2)
    @DisplayName("Gestión de Estado: Actualización de calado con validación")
    public void testGestionEstadoCalado() {
        // Verificamos que se actualiza si el valor es válido (ej: 30.0) [cite: 22]
        assertDoesNotThrow(() -> service.actualizarCalado("IMO1111111", 30.0));
        Buque actualizado = service.buscarPorIMO("IMO1111111");
        assertEquals(30.0, actualizado.getCaladoMaximo());

        // Verificamos que falla si el valor sale del rango 5-40 metros [cite: 22]
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.actualizarCalado("IMO1111111", 45.0);
        });
        assertTrue(exception.getMessage().contains("entre 5 y 40"));
    }

    @Test
    @Order(3)
    @DisplayName("Listado Global: Recuperación de la flota completa")
    public void testListadoGlobal() {
        // Recuperamos la flota y verificamos que al menos están los 2 que insertamos [cite: 23]
        List<Buque> flota = service.listarFlota();
        assertNotNull(flota);
        assertTrue(flota.size() >= 2);
    }

    @Test
    @Order(4)
    @DisplayName("Registro de Capitanes y Actualización de Experiencia")
    public void testRegistroYExperienciaCapitanes() {
        // Registro de un nuevo capitán [cite: 24]
        Capitan capitan = new Capitan();
        capitan.setNombre("Jack Sparrow");
        capitan.setRegistroMaritimo("REG-001"); // Debe ser único en el sistema 
        capitan.setMillasNavegadas(1000.0);

        assertDoesNotThrow(() -> service.registrarCapitan(capitan));

        // Como no sabemos el ID autogenerado, lo ideal sería buscarlo, pero asumiremos ID 1 si la BD está limpia.
        // Simularemos la actualización de experiencia sumando 500 millas [cite: 28]
        // Nota: Asegúrate de adaptar el ID al que genere tu BD.
        assertDoesNotThrow(() -> service.sumarMillas(capitan.getId(), 500.0));
    }

    @Test
    @Order(5)
    @DisplayName("Auditoría de Gastos, Registro y Consulta de Historial")
    public void testIntervencionesYGastos() {
        // Creamos una nueva intervención [cite: 29]
        IntervencionAstillero inter1 = new IntervencionAstillero();
        inter1.setFechaEntrada(LocalDate.now());
        inter1.setMotivo("Revisión de motor"); // Motivo [cite: 30]
        inter1.setCoste(15000.0); // Coste [cite: 30]

        IntervencionAstillero inter2 = new IntervencionAstillero();
        inter2.setFechaEntrada(LocalDate.now());
        inter2.setMotivo("Pintura de casco");
        inter2.setCoste(5000.0);

        // Añadimos las entradas al historial del buque [cite: 29]
        assertDoesNotThrow(() -> service.registrarIntervencion("IMO2222222", inter1));
        assertDoesNotThrow(() -> service.registrarIntervencion("IMO2222222", inter2));

        // Consulta de historial: Recuperamos intervenciones [cite: 31]
        List<IntervencionAstillero> historial = service.consultarHistorial("IMO2222222");
        assertEquals(2, historial.size());

        // Auditoría de gastos: Cálculo del sumatorio total [cite: 32]
        Double totalGastos = service.calcularGastosTotales("IMO2222222");
        assertEquals(20000.0, totalGastos);
        
        
    }
    
    @Test
    @Order(6)
    @DisplayName("Gestión de Certificaciones y Consulta de Competencias")
    public void testGestionyConsultaCertificaciones() {
        // 1. Preparamos un buque específico para esta prueba
        Portacontenedores buquePrueba = new Portacontenedores();
        buquePrueba.setCodigoIMO("IMO3333333");
        buquePrueba.setNombre("Perla Negra");
        buquePrueba.setCaladoMaximo(12.0);
        buquePrueba.setNumeroMaximoTEUs(200);
        service.registrarBuque(buquePrueba);

        // 2. Preparamos un capitán específico
        Capitan capitanPrueba = new Capitan();
        capitanPrueba.setNombre("Hector Barbossa");
        capitanPrueba.setRegistroMaritimo("REG-002"); // Distinto al del test anterior
        capitanPrueba.setMillasNavegadas(50000.0);
        service.registrarCapitan(capitanPrueba);

        // Al persistir, JPA asigna automáticamente el ID generado a la variable capitanPrueba
        Long idCapitan = capitanPrueba.getId();
        assertNotNull(idCapitan, "El ID del capitán no debe ser nulo tras el registro");

        // 3. Gestión de Certificaciones: Funcionalidad para vincular a un capitán con los buques que está habilitado para navegar [cite: 26]
        assertDoesNotThrow(() -> service.vincularCapitanABuque(idCapitan, "IMO3333333"));

        // 4. Consulta de Competencias: Capacidad de recuperar la lista de capitanes certificados para un buque concreto [cite: 27]
        List<Capitan> certificados = service.consultarCapitanesCertificados("IMO3333333");

        // 5. Verificamos que el cruce de datos ha sido un éxito
        assertNotNull(certificados);
        assertFalse(certificados.isEmpty(), "La lista no debe estar vacía tras la vinculación");
        assertEquals(1, certificados.size(), "Debería haber exactamente 1 capitán certificado para este buque");
        assertEquals("Hector Barbossa", certificados.get(0).getNombre(), "El nombre del capitán no coincide");
    }
    
    

   
   
}