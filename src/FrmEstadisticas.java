import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import datechooser.beans.DateChooserCombo;
import entidades.RegistroTemperatura;
import servicios.TemperaturaServicio;

public class FrmEstadisticas extends JFrame {

    private DateChooserCombo dccFecha;
    private JTabbedPane tpTemperaturas;
    private JPanel pnlMaxMin;
    private JPanel pnlTemperatura;
    private List<RegistroTemperatura> datos;

    public FrmEstadisticas() {
        setTitle("Estadisticas por dia");
        setSize(600, 300);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        IconoApp.aplicarIcono(this);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                new FrmInicio().setVisible(true);
            }
        });

        JPanel pnlEstadisticas = new JPanel();
        JPanel pnlDatosProceso = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JLabel lblFecha = new JLabel("Fecha");
        JButton btnProcesar = new JButton("Procesar");
        Calendar calFecha = Calendar.getInstance();

        dccFecha = new DateChooserCombo();
        pnlMaxMin = new JPanel(new BorderLayout());
        pnlTemperatura = new JPanel(new BorderLayout());
        tpTemperaturas = new JTabbedPane();

        pnlEstadisticas.setLayout(new BoxLayout(pnlEstadisticas, BoxLayout.Y_AXIS));
        btnProcesar.addActionListener(this::btnCalcularEstadisticasClick);

        calFecha.set(2024, Calendar.JANUARY, 1);
        dccFecha.setSelectedDate(calFecha);

        pnlDatosProceso.add(lblFecha);
        pnlDatosProceso.add(dccFecha);
        pnlDatosProceso.add(btnProcesar);

        tpTemperaturas.addTab("Max y Min", pnlMaxMin);
        tpTemperaturas.addTab("Temperatura", pnlTemperatura);

        pnlEstadisticas.add(pnlDatosProceso);
        pnlEstadisticas.add(tpTemperaturas);

        getContentPane().add(pnlEstadisticas, BorderLayout.CENTER);

        cargarDatos();
    }

    private void cargarDatos() {
        String nombreArchivo = System.getProperty("user.dir") + "/src/datos/Temperaturas.csv";
        datos = TemperaturaServicio.getDatos(nombreArchivo);
    }

    private void btnCalcularEstadisticasClick(ActionEvent evt) {
        LocalDate fecha = dccFecha.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        pnlMaxMin.removeAll();
        pnlTemperatura.removeAll();

        Map<String, RegistroTemperatura> estadisticas = TemperaturaServicio.getEstadisticas(fecha, datos);

        // Tabla Maximos y Minimos
        String[] columnas = { "Indicador", "Ciudad", "Temperatura" };
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        if (estadisticas.isEmpty()) {
            modelo.addRow(new Object[] { "Máxima", "—", "No hay datos" });
            modelo.addRow(new Object[] { "Mínima", "—", "No hay datos" });
        } else {
            for (Map.Entry<String, RegistroTemperatura> entry : estadisticas.entrySet()) {
                String tipo = entry.getKey(); // "Máxima" o "Mínima"
                RegistroTemperatura rt = entry.getValue();
                modelo.addRow(new Object[] { tipo, rt.getCiudad(), String.format("%.2f °C", rt.getTemperatura()) });
            }
        }

        JTable tablaMaxMin = new JTable(modelo);
        tablaMaxMin.setEnabled(false);
        tablaMaxMin.getTableHeader().setReorderingAllowed(false);
        tablaMaxMin.setRowHeight(25);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tablaMaxMin.getColumnCount(); i++) {
            tablaMaxMin.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollMaxMin = new JScrollPane(tablaMaxMin);
        scrollMaxMin.setPreferredSize(new Dimension(500, 100));
        pnlMaxMin.add(scrollMaxMin, BorderLayout.CENTER);

        // Tabla de todas las ciudades
        String[] columnasCiudad = { "Ciudad", "Temperatura" };
        DefaultTableModel modeloCiudad = new DefaultTableModel(columnasCiudad, 0);

        Set<String> ciudades = datos.stream()
                .map(RegistroTemperatura::getCiudad)
                .collect(Collectors.toCollection(TreeSet::new)); // Orden alfabético

        for (String ciudad : ciudades) {
            Optional<RegistroTemperatura> registro = datos.stream()
                    .filter(r -> r.getCiudad().equals(ciudad) && r.getFecha().equals(fecha))
                    .findFirst();

            String tempStr = registro.isPresent()
                    ? String.format("%.2f °C", registro.get().getTemperatura())
                    : "No hay datos";

            modeloCiudad.addRow(new Object[] { ciudad, tempStr });
        }

        JTable tablaCiudad = new JTable(modeloCiudad);
        tablaCiudad.setEnabled(false);
        tablaCiudad.getTableHeader().setReorderingAllowed(false);
        tablaCiudad.setRowHeight(25);

        DefaultTableCellRenderer centerRendererCiudad = new DefaultTableCellRenderer();
        centerRendererCiudad.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tablaCiudad.getColumnCount(); i++) {
            tablaCiudad.getColumnModel().getColumn(i).setCellRenderer(centerRendererCiudad);
        }

        JScrollPane scrollCiudad = new JScrollPane(tablaCiudad);
        scrollCiudad.setPreferredSize(new Dimension(500, 150));
        pnlTemperatura.add(scrollCiudad, BorderLayout.CENTER);

        // Actualizar interfaz
        pnlMaxMin.revalidate();
        pnlMaxMin.repaint();
        pnlTemperatura.revalidate();
        pnlTemperatura.repaint();
    }

}
