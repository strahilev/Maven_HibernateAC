package org.example.service;

import org.example.entities.*;
import org.example.util.JPAUtil;
import org.junit.jupiter.api.*;

import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ShippingServiceTest {

    private static ShippingService service;

    @BeforeAll
    public static void setUp() {
        
        service = new ShippingService();
        JPAUtil.getEntityManagerFactory();
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            // borrar los datos de las tablas
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
            
            
            em.createNativeQuery("TRUNCATE TABLE certificaciones_capitan_buque").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE intervenciones_astillero").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE portacontenedores").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE buques_cisterna").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE capitanes").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE buques").executeUpdate();
            
           
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error limpiando la BD: " + e.getMessage());
        } finally {
            em.close();
        }
    }
   
    @AfterAll
    public static void tearDown() {
        
    	// para cerrar la conexion
        JPAUtil.shutdown();
    }

    @Test
    @Order(1)
    @DisplayName("Alta de Buques y Búsqueda por IMO")
    public void testAltaYBquedaBuques() {
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

        assertDoesNotThrow(() -> service.registrarBuque(pc));
        assertDoesNotThrow(() -> service.registrarBuque(bc));

        Buque encontrado = service.buscarPorIMO("IMO1111111");
        assertNotNull(encontrado);
        assertEquals("Lázaro Express", encontrado.getNombre());
        assertTrue(encontrado instanceof Portacontenedores);
    }

    @Test
    @Order(2)
    @DisplayName("Gestión de Estado: Actualización de calado con validación")
    public void testGestionEstadoCalado() {
        assertDoesNotThrow(() -> service.actualizarCalado("IMO1111111", 30.0));
        Buque actualizado = service.buscarPorIMO("IMO1111111");
        assertEquals(30.0, actualizado.getCaladoMaximo());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.actualizarCalado("IMO1111111", 45.0);
        });
        assertTrue(exception.getMessage().contains("entre 5 y 40"));
    }

    @Test
    @Order(3)
    @DisplayName("Listado Global: Recuperación de la flota completa")
    public void testListadoGlobal() {
        List<Buque> flota = service.listarFlota();
        assertNotNull(flota);
        assertTrue(flota.size() >= 2);
    }

    @Test
    @Order(4)
    @DisplayName("Registro de Capitanes y Actualización de Experiencia")
    public void testRegistroYExperienciaCapitanes() {
        Capitan capitan = new Capitan();
        capitan.setNombre("Jack Sparrow");
        capitan.setRegistroMaritimo("REG-001"); 
        capitan.setMillasNavegadas(1000.0);

        assertDoesNotThrow(() -> service.registrarCapitan(capitan));
        assertDoesNotThrow(() -> service.sumarMillas(capitan.getId(), 500.0));
    }

    @Test
    @Order(5)
    @DisplayName("Auditoría de Gastos, Registro y Consulta de Historial")
    public void testIntervencionesYGastos() {
        IntervencionAstillero inter1 = new IntervencionAstillero();
        inter1.setFechaEntrada(LocalDate.now());
        inter1.setMotivo("Revisión de motor"); 
        inter1.setCoste(15000.0); 

        IntervencionAstillero inter2 = new IntervencionAstillero();
        inter2.setFechaEntrada(LocalDate.now());
        inter2.setMotivo("Pintura de casco");
        inter2.setCoste(5000.0);

        assertDoesNotThrow(() -> service.registrarIntervencion("IMO2222222", inter1));
        assertDoesNotThrow(() -> service.registrarIntervencion("IMO2222222", inter2));

        List<IntervencionAstillero> historial = service.consultarHistorial("IMO2222222");
        assertEquals(2, historial.size());

        Double totalGastos = service.calcularGastosTotales("IMO2222222");
        assertEquals(20000.0, totalGastos);
    }
    
    @Test
    @Order(6)
    @DisplayName("Gestión de Certificaciones y Consulta de Competencias")
    public void testGestionyConsultaCertificaciones() {
        Portacontenedores buquePrueba = new Portacontenedores();
        buquePrueba.setCodigoIMO("IMO3333333");
        buquePrueba.setNombre("Perla Negra");
        buquePrueba.setCaladoMaximo(12.0);
        buquePrueba.setNumeroMaximoTEUs(200);
        service.registrarBuque(buquePrueba);

        Capitan capitanPrueba = new Capitan();
        capitanPrueba.setNombre("Hector Barbossa");
        capitanPrueba.setRegistroMaritimo("REG-002"); 
        capitanPrueba.setMillasNavegadas(50000.0);
        service.registrarCapitan(capitanPrueba);

        Long idCapitan = capitanPrueba.getId();
        assertNotNull(idCapitan, "El ID del capitán no debe ser nulo tras el registro");

        assertDoesNotThrow(() -> service.vincularCapitanABuque(idCapitan, "IMO3333333"));

        List<Capitan> certificados = service.consultarCapitanesCertificados("IMO3333333");

        assertNotNull(certificados);
        assertFalse(certificados.isEmpty(), "La lista no debe estar vacía tras la vinculación");
        assertEquals(1, certificados.size(), "Debería haber exactamente 1 capitán certificado para este buque");
        assertEquals("Hector Barbossa", certificados.get(0).getNombre(), "El nombre del capitán no coincide");
    }

    @Test
    @Order(7)
    public void testEliminarBuqueSinIntervenciones() {
        Buque nuevo = new Portacontenedores("IMO9999", "Barco de Prueba", 10.0,15,"Normal");
        service.registrarBuque(nuevo); 
        
        service.eliminarBuque(nuevo.getCodigoIMO());
        
        Buque recuperado = service.buscarPorIMO("IMO9999");
        assertNull( recuperado,"El buque debería haber sido eliminado");
    }

    @Test
    @Order(8)
    public void testActualizarCaladoEmergencia() {
        String imo = "IMO7777";
        Buque buqueParaResetear = new Portacontenedores(imo, "Buque Test Emergencia", 30.0,15,"Normal");
        service.registrarBuque(buqueParaResetear);
        
        service.actualizarCaladoEmergencia(imo);
        
        Buque recuperado = service.buscarPorIMO(imo);
        assertEquals( 5.0, recuperado.getCaladoMaximo(), 0.01,"El calado debe haberse reseteado a 5.0");
    }
}