package cl.esperanza.facturacion.dto;

public record ConsumoRequest( 
    String runSocio,
    String fechaLectura,
    double medidaActual,
    double consumoMensual){
}
