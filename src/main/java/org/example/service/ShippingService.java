package org.example.service;

import org.example.entities.*;
import org.example.util.JPAUtil;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

public class ShippingService {
    private static final Logger logger = LogManager.getLogger(ShippingService.class);

    // --- SECCIÓN BUQUES ---

    // 1. Alta de Buques (Genérico para Portacontenedores y Cisternas)
    public void registrarBuque(Buque b) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(b); // JPA se encarga de insertarlo en la tabla correcta por la herencia
            em.getTransaction().commit();
            logger.info("Buque registrado: {} - {}", b.getCodigoIMO(), b.getNombre());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.error("Error al registrar buque: " + e.getMessage());
            throw e;
        } finally { em.close(); }
    }

    // 2. Búsqueda por IMO
    public Buque buscarPorIMO(String imo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Buque.class, imo);
        } finally { em.close(); }
    }

    // 3. Gestión de Estado (Actualizar Calado con Validación)
    public void actualizarCalado(String imo, Double nuevoCalado) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Buque b = em.find(Buque.class, imo);
            if (b != null) {
                b.setCaladoMaximo(nuevoCalado); // El setter de Buque ya tiene la validación 5-40
                em.merge(b);
                em.getTransaction().commit();
                logger.info("Calado actualizado para IMO: {}", imo);
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.error("Error en gestión de estado: " + e.getMessage());
            throw e;
        } finally { em.close(); }
    }

    // 4. Listado Global
    public List<Buque> listarFlota() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
        	return em.createQuery("SELECT b FROM Buque b WHERE TYPE(b) IN (Portacontenedores, BuqueCisterna)", Buque.class).getResultList();
        } finally { em.close(); }
    }

    // --- SECCIÓN CAPITANES ---

    // 5. Registro de Capitanes
    public void registrarCapitan(Capitan c) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(c);
            em.getTransaction().commit();
            logger.info("Capitán registrado: {}", c.getNombre());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.error("Error al registrar capitán: " + e.getMessage());
            throw e;
        } finally { em.close(); }
    }

    // 6. Gestión de Certificaciones (Vincular Capitán y Buque)
    public void vincularCapitanABuque(Long capitanId, String buqueImo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Capitan c = em.find(Capitan.class, capitanId);
            Buque b = em.find(Buque.class, buqueImo);
            if (c != null && b != null) {
                c.getBuquesCertificados().add(b);
                em.merge(c);
                em.getTransaction().commit();
                logger.info("Capitán {} vinculado al buque {}", c.getNombre(), b.getNombre());
            }
        } finally { em.close(); }
    }

    // 7. Consulta de Competencias
    public List<Capitan> consultarCapitanesCertificados(String imo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Capitan c JOIN c.buquesCertificados b WHERE b.codigoIMO = :imo", Capitan.class)
                    .setParameter("imo", imo)
                    .getResultList();
        } finally { em.close(); }
    }

    // 8. Actualización de Experiencia (Millas)
    public void sumarMillas(Long capitanId, Double millasExtra) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Capitan c = em.find(Capitan.class, capitanId);
            if (c != null) {
                c.setMillasNavegadas(c.getMillasNavegadas() + millasExtra);
                em.merge(c);
                em.getTransaction().commit();
            }
        } finally { em.close(); }
    }

    // --- SECCIÓN INTERVENCIONES ---

    // 9. Registro de Intervención
    public void registrarIntervencion(String imo, IntervencionAstillero inter) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Buque b = em.find(Buque.class, imo);
            inter.setBuque(b);
            em.persist(inter);
            em.getTransaction().commit();
            logger.info("Intervención registrada para buque: {}", imo);
        } finally { em.close(); }
    }

    // 10. Consulta de Historial
    public List<IntervencionAstillero> consultarHistorial(String imo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("FROM IntervencionAstillero WHERE buque.codigoIMO = :imo", IntervencionAstillero.class)
                    .setParameter("imo", imo)
                    .getResultList();
        } finally { em.close(); }
    }

    // 11. Auditoría de Gastos (Sumatorio)
    public Double calcularGastosTotales(String imo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Double total = em.createQuery("SELECT SUM(i.coste) FROM IntervencionAstillero i WHERE i.buque.codigoIMO = :imo", Double.class)
                    .setParameter("imo", imo)
                    .getSingleResult();
            return (total != null) ? total : 0.0;
        } finally { em.close(); }
    }
}