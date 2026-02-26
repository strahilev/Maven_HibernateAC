package org.example.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "buques")
@Check(constraints = "calado_maximo >= 5 AND calado_maximo <= 40")
public abstract class Buque {
	private static final Logger logger = LogManager.getLogger(Buque.class);

	@Id
	@Column(name = "codigo_imo", length = 20)
	private String codigoIMO;

	private String nombre;

	@Column(name = "calado_maximo")
	private Double caladoMaximo;

	@OneToMany(mappedBy = "buque", cascade = CascadeType.ALL)
	private List<IntervencionAstillero> intervenciones = new ArrayList<>();

	@ManyToMany(mappedBy = "buquesCertificados")
	private List<Capitan> capitanesCertificados = new ArrayList<>();

	public Buque() {
	}

	public Buque(String codigoIMO, String nombre, Double caladoMaximo) {
		super();
		this.codigoIMO = codigoIMO;
		this.nombre = nombre;
		this.caladoMaximo = caladoMaximo;
		
	}

	public void setCaladoMaximo(Double caladoMaximo) {
		if (caladoMaximo < 5 || caladoMaximo > 40) {
			String errorMsg = "ERROR DE VALIDACIÓN: El calado debe estar entre 5 y 40. Valor: " + caladoMaximo;
			logger.error(errorMsg);
			throw new IllegalArgumentException(errorMsg);
		}
		this.caladoMaximo = caladoMaximo;
	}

	// Getters y Setters...
	public String getCodigoIMO() {
		return codigoIMO;
	}

	public void setCodigoIMO(String codigoIMO) {
		this.codigoIMO = codigoIMO;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Double getCaladoMaximo() {
		return caladoMaximo;
	}

    public List<IntervencionAstillero> getIntervenciones() { return intervenciones; }
}