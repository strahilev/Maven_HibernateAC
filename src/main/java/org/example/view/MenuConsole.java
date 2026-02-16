package org.example.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Scanner;

public class MenuConsole implements IView {
    private static final Logger logger = LogManager.getLogger(MenuConsole.class);
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void iniciar() {
        mostrarMensaje("=== BIENVENIDO A LÁZARO SHIPPING ===");
        boolean salir = false;
        while (!salir) {
            mostrarMensaje("\n1. Operación A\n0. Salir");
            int opcion = leerEntero("Seleccione:");
            if (opcion == 0) salir = true;
        }
        cerrar();
    }

    @Override
    public void mostrarMensaje(String m) { System.out.println(m); }
    @Override
    public void mostrarError(String e) { System.err.println("ERROR: " + e); }
    @Override
    public int leerEntero(String m) {
        System.out.print(m + " ");
        int dato = scanner.nextInt();
        scanner.nextLine();
        return dato;
    }
    @Override
    public void cerrar() { scanner.close(); }
    // Implementar leerTexto y leerDouble...

	@Override
	public String leerTexto(String mensaje) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double leerDouble(String mensaje) {
		// TODO Auto-generated method stub
		return 0;
	}
}