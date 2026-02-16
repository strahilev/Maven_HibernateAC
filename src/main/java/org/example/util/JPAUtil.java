package org.example.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JPAUtil {
    private static final Logger logger = LogManager.getLogger(JPAUtil.class);
    
    // El nombre debe coincidir EXACTAMENTE con el de persistence.xml
    private static final String PERSISTENCE_UNIT_NAME = "unidad-persistencia";
    private static EntityManagerFactory factory;

    // Método para obtener la factoría (Singleton)
    public static EntityManagerFactory getEntityManagerFactory() {
        if (factory == null) {
            try {
                factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                logger.info("EntityManagerFactory creado con éxito.");
            } catch (Exception e) {
                logger.fatal("Error al inicializar la persistencia: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return factory;
    }

    // Método de utilidad para obtener un EntityManager nuevo
    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    // Método para cerrar la factoría al apagar la app
    public static void shutdown() {
        if (factory != null && factory.isOpen()) {
            factory.close();
            logger.info("Conexión con la base de datos cerrada.");
        }
    }
}