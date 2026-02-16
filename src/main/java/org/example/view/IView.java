package org.example.view;

public interface IView {
    void iniciar();
    void mostrarMensaje(String mensaje);
    void mostrarError(String error);
    String leerTexto(String mensaje);
    int leerEntero(String mensaje);
    double leerDouble(String mensaje);
    void cerrar();
}