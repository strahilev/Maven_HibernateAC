package org.example.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "buques_cisterna")
@PrimaryKeyJoinColumn(name = "buque_imo")
public class BuqueCisterna extends Buque {

    @Column(name = "capacidad_carga_m3")
    private Double capacidadCarga;

    @Column(name = "tipo_doble_casco")
    private String tipoDobleCasco;

    public BuqueCisterna() {}

    public Double getCapacidadCarga() { return capacidadCarga; }
    public void setCapacidadCarga(Double capacidadCarga) { this.capacidadCarga = capacidadCarga; }

    public String getTipoDobleCasco() { return tipoDobleCasco; }
    public void setTipoDobleCasco(String tipoDobleCasco) { this.tipoDobleCasco = tipoDobleCasco; }
}