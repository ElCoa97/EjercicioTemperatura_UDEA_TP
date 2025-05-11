package servicios;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import entidades.RegistroTemperatura;

public class TemperaturaServicio {

    public static List<RegistroTemperatura> getDatos(String nombreArchivo) {
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("d/M/yyyy");
        try {
            Stream<String> lineas = Files.lines(Paths.get(nombreArchivo));
            return lineas.skip(1)
                    .map(linea -> linea.split(","))
                    .map(textos -> new RegistroTemperatura(textos[0], LocalDate.parse(textos[1], formatoFecha),
                            Double.parseDouble(textos[2])))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public static Map<String, RegistroTemperatura> getEstadisticas(LocalDate fecha, List<RegistroTemperatura> datos) {
    
    // Filtra los datos solo para la fecha seleccionada
    var datosDelDia = datos.stream()
        .filter(d -> d.getFecha().equals(fecha))
        .collect(Collectors.toList());

    if (datosDelDia.isEmpty()) return Collections.emptyMap();

    RegistroTemperatura max = Collections.max(datosDelDia, Comparator.comparingDouble(RegistroTemperatura::getTemperatura));
    RegistroTemperatura min = Collections.min(datosDelDia, Comparator.comparingDouble(RegistroTemperatura::getTemperatura));

    Map<String, RegistroTemperatura> estadisticas = new LinkedHashMap<>();
    estadisticas.put("Máxima", max);
    estadisticas.put("Mínima", min);
    return estadisticas;
}

    public static Map<String, Double> getPromediosPorCiudad(LocalDate desde, LocalDate hasta, List<RegistroTemperatura> datos) {
        return datos.stream()
            .filter(d -> !d.getFecha().isBefore(desde) && !d.getFecha().isAfter(hasta))
            .collect(Collectors.groupingBy(
                RegistroTemperatura::getCiudad,
                Collectors.averagingDouble(RegistroTemperatura::getTemperatura)
            ));
    }
}
