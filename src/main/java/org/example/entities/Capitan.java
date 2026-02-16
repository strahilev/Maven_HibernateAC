package org.example.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "capitanes")
public class Capitan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String registroMaritimo;
    private Double millasNavegadas;

    @ManyToMany
    @JoinTable(
            name = "certificaciones_capitan_buque",
            joinColumns = @JoinColumn(name = "capitan_id"),
            inverseJoinColumns = @JoinColumn(name = "buque_imo")
    )
    private List<Buque> buquesCertificados = new ArrayList<Buque>();

    public Capitan() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRegistroMaritimo() { return registroMaritimo; }
    public void setRegistroMaritimo(String r) { this.registroMaritimo = r; }
    public Double getMillasNavegadas() { return millasNavegadas; }
    public void setMillasNavegadas(Double m) { this.millasNavegadas = m; }
    public List<Buque> getBuquesCertificados() { return buquesCertificados; }
    public void setBuquesCertificados(List<Buque> b) { this.buquesCertificados = b; }
}