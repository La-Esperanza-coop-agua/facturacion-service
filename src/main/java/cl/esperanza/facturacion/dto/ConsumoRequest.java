package cl.esperanza.facturacion.dto;

public record ConsumoRequest( 
    String runSocio,
    String periodo,
    double metrosCubicosConsumidos){
}
