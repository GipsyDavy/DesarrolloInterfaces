package Tema01;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Gipsy Dávy
 */

/**
 * Ejemplo mínimo de una ventana Swing (JFrame) creada solo con código.
 * Demuestra los pasos imprescindibles:
 *  1) Establecer tamaño/posición (setBounds)
 *  2) Hacerla visible (setVisible)
 *  3) Definir la acción de cierre (setDefaultCloseOperation)
 */

public class Jframe extends JFrame {
    
     //Buenas prácticas en clases serializables (Jframe lo es): id de versión.
    private static final long serialVersionUID = 1L;
    
    // Panel raíz que contendrá todos los componentes de la ventana
    private JPanel contentPane;
    
      /**
     * Punto de entrada de la aplicación.
     * Usamos EventQueue.invokeLater para crear y mostrar la GUI
     * en el hilo de despacho de eventos (EDT), como recomienda Swing.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    // Crear instancia (instanciamos) de la ventana
                    Jframe frame = new Jframe();
                    // Ahora la hacemos visibe
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }     
        });
    }

/**
 * Constructor: configura la ventana y su panel de contenido.
 */

    public Jframe() {
        
        // Configurar la operación de cierre (cerrar la aplicación al cerrar la ventana)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        // Establecer tamaño y posición (x, y, width (ancho), height (alto))
        setBounds(100, 100, 450, 300);
        
        // Crear el panel de contenido (donde se añaden los componentes)
        contentPane = new JPanel();
        
        // Establecer un borde vacío con un margen de 5 píxeles
        // Borde interno (márgenes) de 5px en cada lado para separar del borde de la ventana.
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Asignar el panel como panel de contenido del Jframe
        setContentPane(contentPane);
           
        // Usar un diseño absoluto (null) para posicionar componentes manualmente
        contentPane.setLayout(null);
    
    }
    
}
