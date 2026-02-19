package org.example.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "portacontenedores")
@PrimaryKeyJoinColumn(name = "codigo_imo")
public class Portacontenedores extends Buque {

    @Column(name = "num_max_teus")
    private Integer numeroMaximoTEUs;

    @Column(name = "tipo_grua")
    private String tipoGrua;

    public Portacontenedores() {}

    public Integer getNumeroMaximoTEUs() { return numeroMaximoTEUs; }
    public void setNumeroMaximoTEUs(Integer numeroMaximoTEUs) { this.numeroMaximoTEUs = numeroMaximoTEUs; }

    public String getTipoGrua() { return tipoGrua; }
    public void setTipoGrua(String tipoGrua) { this.tipoGrua = tipoGrua; }
}