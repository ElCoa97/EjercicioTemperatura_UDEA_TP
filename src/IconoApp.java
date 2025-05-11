import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class IconoApp {
    public static void aplicarIcono(JFrame ventana){
        Image icono = new ImageIcon(IconoApp.class.getResource("/iconos/IconoApp.jpg")).getImage();
        ventana.setIconImage(icono);
    }

}
