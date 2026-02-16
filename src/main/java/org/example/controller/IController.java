package org.example.controller;

import java.util.List;

import org.example.entities.Buque;
import org.example.entities.Capitan;
import org.example.entities.IntervencionAstillero;

public interface IController {
	
	 void altaBuque();
	 Buque buscarPorIMO(String imo);
	 void gestionEstado(Buque b);
	 List<Buque> listarBuques();
	 void registroCapitan();
	 
	 //mirar luego
	 void gestionCertificaciones();
	 
	 IntervencionAstillero registrarIntervencion();
	 List<Capitan> consultarCapitanesBuque(Buque b);
	 
	 void actualizarExperencia(Buque b ,List<Capitan> capitanes);
	 void aniadirIntervencion(IntervencionAstillero inter,Buque b);
	 void consultarHistorial(Buque b);
	 void gastosPorBuque(Buque b);
	 

}
