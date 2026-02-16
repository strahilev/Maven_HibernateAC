package org.example.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "intervenciones_astillero")
public class IntervencionAstillero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaEntrada;
    private String motivo;
    private Double coste;

    @ManyToOne
    @JoinColumn(name = "buque_imo", nullable = false)
    private Buque buque;

    public IntervencionAstillero() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFechaEntrada() { return fechaEntrada; }
    public void setFechaEntrada(LocalDate f) { this.fechaEntrada = f; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String m) { this.motivo = m; }
    public Double getCoste() { return coste; }
    public void setCoste(Double c) { this.coste = c; }
    public Buque getBuque() { return buque; }
    public void setBuque(Buque b) { this.buque = b; }
}