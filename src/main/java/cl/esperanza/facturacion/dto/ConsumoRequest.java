package cl.esperanza.facturacion.dto;

import java.time.LocalDate;

public record ConsumoRequest( 
    Integer id,
    String runSocio,
    LocalDate fechaLectura,
    double medidaActual,
    double consumoMensual,
    String periodo
){}
