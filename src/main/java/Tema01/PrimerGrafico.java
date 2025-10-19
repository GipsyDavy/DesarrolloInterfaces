package Tema01;

import javax.swing.*;

/**
 *
 * @author Gipsy Dávy
 */
public class PrimerGrafico {
    
    public PrimerGrafico() {
    // Constructor vacío
   }
    
    public static void main(String[] args) {
        // Crear ventana JFrame
        JFrame f = new JFrame("Mi primera ventana");
        
        // Establecer tamañO de la ventana
        f.setSize(800, 400);
        
        // Hacer visible la ventana
        f.setVisible(true);
        
        // Cerramos la venta al pulsar el boton de cerrar
        f.setDefaultCloseOperation(Jframe.EXIT_ON_CLOSE);
    }
    
}
