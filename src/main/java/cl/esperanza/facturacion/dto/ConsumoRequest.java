package cl.esperanza.facturacion.dto;

public record ConsumoRequest( 
    Integer id,
    String runSocio,
    String fechaLectura,
    double medidaActual,
    double consumoMensual){
}
