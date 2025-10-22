package Tema02.LoginDosVentanas;

/**
 *
 * @author Gipsy Dávy
 */

import Tema02LoginDosVentanas.Inicio;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HexFormat;
import javax.swing.*;


/**
 * Ventana de login que valida usuario/contraseña leyendo "data/usuarios.txt".
 * Soporta dos formatos:
 *    - Plano:  usuario;contraseña
 *    - Hash:   usuario;hashSha256(contraseña)
 * Alternas entre formatos con la constante USAR_HASH.
 */
public class Principal extends JFrame {                    // Hereda de JFrame para crear la ventana
    
     // -------------------- CONFIGURACIÓN --------------------
    private static final boolean USAR_HASH = false;        // true: fichero con SHA-256; false: contraseñas en claro

    private static final Path RUTA_USUARIOS =              // Ruta al fichero de usuarios.txt
            Paths.get(System.getProperty("user.dir"),      // Carpeta del proyecto en ejecución
                      "src", "data",                       // Subcarpeta "data"
                      "usuarios.txt");                     // Nombre del fichero

     // -------------------- CAMPOS DE UI --------------------
    private JTextField txtUsuario;                         // Campo de texto para el usuario
    private JPasswordField txtPass;                        // Campo de contraseña (oculta caracteres)
    
   public Principal() {                                   // Constructor: monta la interfaz
        super("Acceso");                                   // Título de la ventana
        
        JLabel lbUsuario = new JLabel("Usuario:");         // Etiqueta para el usuario
        JLabel lbPass   = new JLabel("Contraseña:");       // Etiqueta para la contraseña

        txtUsuario = new JTextField(15);                   // Campo de usuario (15 columnas aprox)
        txtPass    = new JPasswordField(15);               // Campo de contraseña

        JButton btnInicio = new JButton("Inicio");         // Botón para validar y pasar a Inicio
        JButton btnSalir  = new JButton("Salir");          // Botón para cerrar la app

        btnInicio.addActionListener(this::onInicio);       // Asocia handler al botón Inicio
        btnSalir.addActionListener(e -> System.exit(0));   // Cierra la aplicación

        setLayout(new GridBagLayout());                    // Layout de cuadrícula flexible
        GridBagConstraints c = new GridBagConstraints();   // Restricciones para colocar componentes
        c.insets = new Insets(8, 10, 8, 10);               // Margen alrededor de cada control
        c.fill   = GridBagConstraints.HORIZONTAL;          // Que los campos se estiren en horizontal

        c.gridx = 0; c.gridy = 0; add(lbUsuario, c);       // Columna 0, fila 0: etiqueta Usuario
        c.gridx = 1; c.gridy = 0; add(txtUsuario, c);      // Columna 1, fila 0: textfield Usuario
        c.gridx = 0; c.gridy = 1; add(lbPass, c);          // Columna 0, fila 1: etiqueta Contraseña
        c.gridx = 1; c.gridy = 1; add(txtPass, c);         // Columna 1, fila 1: passwordfield

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0)); // Panel para botones
        pnlBtns.add(btnInicio);                             // Añade botón Inicio al panel
        pnlBtns.add(btnSalir);                              // Añade botón Salir al panel

        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;         // Fila 2 ocupa 2 columnas
        add(pnlBtns, c);                                    // Añade panel de botones

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);     // Cerrar app al cerrar ventana
        setSize(360, 190);                                  // Tamaño por defecto
        setLocationRelativeTo(null);                        // Centra en pantalla
        getRootPane().setDefaultButton(btnInicio);          // Enter dispara "Inicio"
    
   }
/**
     * Handler del botón "Inicio".
     * 1) Lee usuario y password de los campos.
     * 2) Valida contra el fichero (plano o hash según USAR_HASH).
     * 3) Si OK → abre Inicio y cierra este JFrame; si KO → mensaje de error.
     */
    private void onInicio(ActionEvent e) {                  // Recibe el evento del botón
        String user = txtUsuario.getText().trim();          // Usuario sin espacios extremos
        char[] pass = txtPass.getPassword();                // Copia de la contraseña (char[], más seguro que String)

        boolean ok = USAR_HASH                              // Escoge método de validación según configuración
                ? validarConFicheroHash(user, pass)         // Validación con hashes
                : validarConFicheroPlano(user, pass);       // Validación con contraseña en claro

        Arrays.fill(pass, '\0');                            // Limpia la copia local de la contraseña

        if (ok) {                                           // Si credenciales válidas...
            SwingUtilities.invokeLater(                     // Asegura ejecutar en el hilo de eventos de Swing
                    () -> new Inicio(user).setVisible(true) // Crea y muestra la ventana de bienvenida
            );
            dispose();                                      // Cierra la ventana de login
        } else {                                            // Si no coincide...
            JOptionPane.showMessageDialog(                  // Muestra un diálogo de error
                    this,
                    "Usuario o contraseña incorrectos",
                    "Acceso denegado",
                    JOptionPane.ERROR_MESSAGE
            );
            txtPass.setText("");                            // Vacía el campo de contraseña
            txtPass.requestFocusInWindow();                 // Devuelve el foco a la contraseña
        }
    }

    // =================== VALIDACIÓN: FICHERO PLANO ===================

    /**
     * Lee "usuarios.txt" con formato: usuario;contraseña (en claro).
     * Ignora líneas vacías o que empiezan por '#'.
     */
    private boolean validarConFicheroPlano(String usuario, char[] passIngresada) {
        if (!Files.exists(RUTA_USUARIOS)) {                 // Comprueba que el fichero exista
            error("No existe " + RUTA_USUARIOS.toAbsolutePath()); // Mensaje claro si no existe
            return false;                                   // Sin fichero no se puede validar
        }

        try (BufferedReader br = Files.newBufferedReader(   // Abre el fichero en UTF-8
                RUTA_USUARIOS, StandardCharsets.UTF_8)) {

            String linea;                                   // Línea actual leída
            while ((linea = br.readLine()) != null) {       // Lee hasta EOF
                linea = linea.trim();                       // Quita espacios extremos
                if (linea.isEmpty() || linea.startsWith("#")) continue; // Salta comentarios/vacías

                String[] partes = linea.split(";", 2);      // Divide por el primer ';' (máx 2 partes)
                if (partes.length != 2) continue;           // Si el formato no es correcto, ignora

                String u  = partes[0].trim();               // Usuario del fichero
                String pw = partes[1];                      // Contraseña en claro del fichero

                if (u.equals(usuario)                       // Compara usuario exacto...
                        && pw.contentEquals(new String(passIngresada))) { // ...y contraseña igual
                    return true;                            // Coincidencia encontrada → válido
                }
            }
        } catch (IOException ex) {                          // Cualquier error de E/S
            error("No puedo leer " + RUTA_USUARIOS + "\n" + ex.getMessage());
        }
        return false;                                       // Si no se encontró coincidencia → inválido
    }

    // =================== VALIDACIÓN: FICHERO CON HASH ===================

    /**
     * Lee "usuarios.txt" con formato: usuario;hashHexSHA256.
     * Calcula SHA-256 de la contraseña ingresada y compara con el hash almacenado.
     */
    private boolean validarConFicheroHash(String usuario, char[] passIngresada) {
        if (!Files.exists(RUTA_USUARIOS)) {                 // Verifica existencia del fichero
            error("No existe " + RUTA_USUARIOS.toAbsolutePath());
            return false;
        }

        String hashIngresado = sha256(new String(passIngresada)); // Calcula hash de lo que tecleó el usuario

        try (BufferedReader br = Files.newBufferedReader(   // Abre el fichero en UTF-8
                RUTA_USUARIOS, StandardCharsets.UTF_8)) {

            String linea;                                   // Línea actual
            while ((linea = br.readLine()) != null) {       // Itera por todas las líneas
                linea = linea.trim();                       // Quita espacios
                if (linea.isEmpty() || linea.startsWith("#")) continue; // Ignora comentarios

                String[] partes = linea.split(";", 2);      // usuario;hash
                if (partes.length != 2) continue;           // Formato incorrecto → ignora

                String u = partes[0].trim();                // Usuario del fichero
                String hashGuardado = partes[1].trim();     // Hash guardado (hexadecimal)

                if (u.equals(usuario)                       // Usuario coincide
                        && hashIngresado.equalsIgnoreCase(hashGuardado)) { // Hash coincide
                    return true;                            // Credenciales válidas
                }
            }
        } catch (IOException ex) {                          // Error leyendo el fichero
            error("No puedo leer " + RUTA_USUARIOS + "\n" + ex.getMessage());
        }
        return false;                                       // No se encontró usuario/hash válido
    }

    // =================== UTILIDADES ===================

    /** Muestra un diálogo de error con título estándar. */
    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Devuelve el SHA-256 del texto en hexadecimal minúscula.
     * Útil para preparar el fichero de usuarios en modo USAR_HASH = true.
     */
    private static String sha256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256"); // Instancia del algoritmo
            byte[] out = md.digest(texto.getBytes(StandardCharsets.UTF_8)); // Hash de los bytes UTF-8
            return HexFormat.of().formatHex(out);                     // Convierte bytes → cadena hex
        } catch (Exception e) {
            throw new RuntimeException("No se pudo calcular SHA-256", e); // Propaga como unchecked
        }
    }

    /** main: crea y muestra la ventana de login en el hilo de eventos de Swing. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {                  // Ejecuta UI en el EDT
            try { UIManager.setLookAndFeel(                 // (Opcional) Look&Feel nativo
                    UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new Principal().setVisible(true);               // Crea y muestra el login
        });
    }
}
