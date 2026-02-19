package org.example.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class GenericDAO<T, ID> {
    private static final Logger logger = LogManager.getLogger(GenericDAO.class);
    private final Class<T> entityClass;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void save(T entity) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entity);
            tx.commit();
            logger.info("Entidad {} guardada correctamente", entityClass.getSimpleName());
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.error("Error al guardar {}: {}", entityClass.getSimpleName(), e.getMessage());
            throw e;
        } finally {
            em.close();
        }
    }

    public T findById(ID id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }
}