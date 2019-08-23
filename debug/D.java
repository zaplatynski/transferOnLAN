package debug;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import javax.swing.JOptionPane;

/**
 *
 * @author Arnaud FRANCOIS
 */
public class D {

    private static PrintWriter out = null;

    
    
    /**
     * écrit sur la sortie standard la chaîne associée à un objet
     * @param objet
     */
    public static void p(Object objet) {
        System.out.println(objet);
    }

    public static void f(String message) {
        if (out == null) {
            try {
                out = new PrintWriter(new FileOutputStream("debug_log.txt"));
            } catch (FileNotFoundException ex) {
                System.err.println("erreur au moment de l'écriture dans le fichier de log");
            }
        }
        out.println(message);
        out.flush();
    }

    /**
     * affiche un message de debug avec Swing
     *
     * @param message
     */
    public static void m(String message) {
        JOptionPane.showMessageDialog(null, message, "Debug message", JOptionPane.INFORMATION_MESSAGE);
    }
}
