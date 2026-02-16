package org.example.service;

import java.util.List;



import org.example.entities.Buque;
import org.example.entities.Capitan;
import org.example.entities.IntervencionAstillero;

import jakarta.persistence.EntityManager;

public class ShippingService {

	private final EntityManager em;

	public ShippingService(EntityManager em) {
		this.em = em;

	}

	public void registrarBuque(Buque b) {

		em.getTransaction().begin();

		try {
			em.persist(b);
			em.getTransaction().commit();
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}

		}
	}

	public Buque buscarPorIMO(String imo) {

		em.getTransaction().begin();

		try {
			Buque b = em.find(Buque.class, imo);
			if (b == null)
				throw new RuntimeException("No se ha encontrado");

			return b;

		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
				return null;
			}
		}
		return null;

	}

	// cambiar logica
	
	public void gestionEstado(Buque b) {

		em.getTransaction().begin();

		try {
			
			

			em.persist(b);
			em.getTransaction().commit();

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public List<Buque> recuperarFlotaCompleta() {

		return em.createQuery("Select b FROM Buque b", Buque.class).getResultList();

	}
	
	

	public void registroCapitan(Capitan c) {

		em.getTransaction().begin();

		try {
			em.persist(c);
			em.getTransaction().commit();
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}

		}

	}

	// gestion de certificaciones FALTA

	public List<Capitan> consultarCapitanesBuque(String imo) {

		return em.createQuery("Select c FROM Capitan c JOIN c.buquesCertificados b WHERE b.imo = :imo", Capitan.class)
				.setParameter("imo", imo).getResultList();

	}
	
	// actualizacion experencia FALTA

	public void registrarIntervencion(String imo, IntervencionAstillero inter) {

		em.getTransaction().begin();

		try {
			Buque b = em.find(Buque.class, imo);
			if (b == null)
				throw new RuntimeException("No se ha encontrado");
			inter.setBuque(b);
			em.persist(inter);
			em.getTransaction().commit();

		} catch (Exception e) {

			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		}

	}
	
	public List<IntervencionAstillero> consultarHistorial(String imo){
		
		return em.createQuery("Select i FROM IntervencionAstillero i JOIN i.buque b WHERE b.imo = :imo", IntervencionAstillero.class)
				.setParameter("imo", imo).getResultList();
		
	}
	
	
	
	public double totalGastosBuque(String imo) {
		
		
		return em.createQuery("Select SUM(i.gasto) FROM IntervencionAstillero i JOIN i.buque b GROUP BY i.buque HAVING b.imo = :imo", IntervencionAstillero.class)
				.setParameter("imo", imo).getSingleResult().getCoste();
		
		
	}

}
