import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import datechooser.beans.DateChooserCombo;
import entidades.RegistroTemperatura;
import servicios.TemperaturaServicio;

public class FrmTemperatura extends JFrame {

    private DateChooserCombo dccDesde, dccHasta;
    private JTabbedPane tpTemperaturas;
    private JPanel pnlGrafica;

    private List<RegistroTemperatura> datos;

    public FrmTemperatura() {

        setTitle("Temperaturas promedio por Ciudad");
        setSize(800, 700);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        IconoApp.aplicarIcono(this);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                new FrmInicio().setVisible(true);
            }
        });

        JPanel pnlTemperaturas = new JPanel();
        pnlTemperaturas.setLayout(new BoxLayout(pnlTemperaturas, BoxLayout.Y_AXIS));

        JPanel pnlDatosProceso = new JPanel();
        pnlDatosProceso.setPreferredSize(new Dimension(pnlDatosProceso.getWidth(), 50));
        pnlDatosProceso.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        pnlDatosProceso.setLayout(null);

        JLabel lblDesde = new JLabel("Desde");
        lblDesde.setBounds(10, 10, 50, 25);
        pnlDatosProceso.add(lblDesde);

        dccDesde = new DateChooserCombo();
        dccDesde.setBounds(60, 10, 120, 25);
        pnlDatosProceso.add(dccDesde);
        Calendar calDesde = Calendar.getInstance();
        calDesde.set(2024, Calendar.JANUARY, 1);
        dccDesde.setSelectedDate(calDesde);

        JLabel lblHasta = new JLabel("Hasta");
        lblHasta.setBounds(190, 10, 50, 25);
        pnlDatosProceso.add(lblHasta);

        dccHasta = new DateChooserCombo();
        dccHasta.setBounds(240, 10, 120, 25);
        pnlDatosProceso.add(dccHasta);

        JButton btnProcesar = new JButton("Procesar");
        btnProcesar.setBounds(400, 10, 120, 25);
        btnProcesar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnGraficarClick();
            }
        });
        pnlDatosProceso.add(btnProcesar);

        pnlGrafica = new JPanel();
        JScrollPane spGrafica = new JScrollPane(pnlGrafica);

        tpTemperaturas = new JTabbedPane();
        tpTemperaturas.addTab("Gráfica", spGrafica);

        pnlTemperaturas.add(pnlDatosProceso);
        pnlTemperaturas.add(tpTemperaturas);

        getContentPane().add(pnlTemperaturas, BorderLayout.CENTER);

        cargarDatos();
    }

    private void cargarDatos() {
        String nombreArchivo = System.getProperty("user.dir") + "/src/datos/Temperaturas.csv";
        datos = TemperaturaServicio.getDatos(nombreArchivo);
    }

    private void btnGraficarClick() {
        LocalDate desde = dccDesde.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate hasta = dccHasta.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (hasta.isBefore(desde)) {
            JOptionPane.showMessageDialog(this, "La fecha 'Hasta' debe ser posterior o igual a 'Desde'");
            return;
        }

        tpTemperaturas.setSelectedIndex(0);

        Map<String, Double> promedioPorCiudad = TemperaturaServicio.getPromediosPorCiudad(desde, hasta, datos);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        promedioPorCiudad.forEach((ciudad, promedio) -> {
            dataset.addValue(promedio, ciudad, ""); // Cada ciudad es una serie distinta
        });

        JFreeChart chart = ChartFactory.createBarChart(
                "Temperatura promedio por ciudad",
                "Ciudad",
                "Temperatura (°C)",
                dataset);

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        // Mostrar etiquetas encima de las barras
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);

        // Asignar colores diferentes a cada ciudad
        Color[] colores = new Color[] {
                Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA,
                new Color(128, 0, 128), new Color(0, 128, 128), Color.PINK
        };

        for (int i = 0; i < dataset.getRowCount(); i++) {
            renderer.setSeriesPaint(i, colores[i % colores.length]);
        }

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));

        pnlGrafica.removeAll();
        pnlGrafica.setLayout(new BorderLayout());
        pnlGrafica.add(chartPanel, BorderLayout.CENTER);
        pnlGrafica.revalidate();
    }

}
