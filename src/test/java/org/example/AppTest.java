package org.example;

import static org.junit.jupiter.api.Assertions.*;

import org.example.entities.Portacontenedores;
import org.example.service.ShippingService;
import org.junit.Test;

public class AppTest {
    private ShippingService service = new ShippingService();

    @Test
    public void testValidacionCalado() {
        Portacontenedores p = new Portacontenedores();
        p.setCodigoIMO("IMO1234567");
        p.setNombre("Lázaro Express");
        
        
        assertThrows(IllegalArgumentException.class, () -> p.setCaladoMaximo(3.0));
    }

    @Test
    public void testPersistenciaBuque() {
        Portacontenedores p = new Portacontenedores();
        p.setCodigoIMO("IMO9999999");
        p.setNombre("Titan");
        p.setCaladoMaximo(15.0);
        p.setNumeroMaximoTEUs(5000);
        
        assertDoesNotThrow(() -> service.registrarBuque(p));
    }
}