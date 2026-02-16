package org.example;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.view.MenuConsole;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Iniciando aplicación...");
        EntityManagerFactory emf = null;
        
        try {
            emf = Persistence.createEntityManagerFactory("unidad-persistencia");
            new MenuConsole().iniciar();
        } catch (Exception e) {
            logger.error("Error crítico", e);
        } finally {
            if (emf != null) emf.close();
        }
    }
}