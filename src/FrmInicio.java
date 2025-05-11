import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;


public class FrmInicio extends JFrame{

    public FrmInicio(){
        setTitle("Temperatura");
        setSize(600,400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        IconoApp.aplicarIcono(this);

        JPanel panelFondo = new JPanel(){
            private Image fondo;

            {
                ImageIcon iconoFondo = new ImageIcon(getClass().getResource("/iconos/fondo.jpg"));
                fondo = iconoFondo.getImage();
            }
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                if(fondo != null){
                    g.drawImage(fondo, 0, 0, getWidth(),getHeight(),this);
                }
            }
        };
        panelFondo.setLayout(null);

        JLabel lblTitulo = new JLabel();
        JButton btnGraficar = new JButton();
        JButton btnEstadisticas = new JButton();
        JToolBar tb = new JToolBar();

        String creditos = """
            By Moises Coa Luna
            
            Instructor: Fray Leon
            Tecnicas de Programacion y Laboratorio
            Ingenieria de Sistemas
            Universidad de Antioquia
        """;

        JButton btnCreditos = new JButton("Creditos");
        btnCreditos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                JOptionPane.showMessageDialog(null, creditos);
            }
        });
        tb.add(btnCreditos);
        
        lblTitulo.setText("Seleccione la opción de su preferencia");
        lblTitulo.setBounds(0, 40, 600, 40);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        int anchoIcono = 100;  // Ajusta este valor según lo que necesites
        int altoIcono = 100;   // Ajusta este valor según lo que necesites

        ImageIcon iconoGraficaOrg = new ImageIcon(getClass().getResource("/iconos/Grafica1.png"));
        ImageIcon iconoGrafica = new ImageIcon(
            iconoGraficaOrg.getImage().getScaledInstance(anchoIcono, altoIcono, java.awt.Image.SCALE_SMOOTH)
        );

        btnGraficar.setIcon(iconoGrafica);
        btnGraficar.setToolTipText("Gráfica Temperaturas promedio por ciudad");
        btnGraficar.setBounds(150, 150, 120, 120);
        btnGraficar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new FrmTemperatura().setVisible(true);
                dispose();
            }
        });

        btnGraficar.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        btnGraficar.setBackground(Color.LIGHT_GRAY);

        ImageIcon iconoMaxMinOrg = new ImageIcon(getClass().getResource("/iconos/Datos1.png"));
        ImageIcon iconoMaxMin = new ImageIcon(
            iconoMaxMinOrg.getImage().getScaledInstance(anchoIcono, altoIcono, java.awt.Image.SCALE_SMOOTH)
        );

        btnEstadisticas.setIcon(iconoMaxMin);
        btnEstadisticas.setToolTipText("Estadisticas por dia");
        btnEstadisticas.setBounds(280, 150, 120, 120);
        btnEstadisticas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new FrmEstadisticas().setVisible(true);
                dispose();
            }
        });

        btnEstadisticas.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        btnEstadisticas.setBackground(Color.LIGHT_GRAY);

        //Efectos al pasar el mouse
        MouseAdapter efecto = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                button.setBackground(Color.CYAN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                button.setBackground(Color.LIGHT_GRAY);
            }
        };

        btnGraficar.addMouseListener(efecto);
        btnEstadisticas.addMouseListener(efecto);

        panelFondo.add(lblTitulo);
        panelFondo.add(btnGraficar);
        panelFondo.add(btnEstadisticas);

        getContentPane().add(tb, BorderLayout.NORTH);
        getContentPane().add(panelFondo);

    }

}
