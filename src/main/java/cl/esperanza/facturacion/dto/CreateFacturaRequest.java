package cl.esperanza.facturacion.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import cl.esperanza.facturacion.model.Factura;
import jakarta.validation.constraints.NotBlank;

public record CreateFacturaRequest(
    @NotBlank(message = "El RUN del socio es obligatorio")
    String runSocio,

    @NotBlank(message = "El periodo es obligatorio (Ej: 2026-05)")
    String periodo
) {
    public Factura toEntity() {
        Factura factura = new Factura();
        factura.setRunSocio(this.runSocio());
        factura.setPeriodo(this.periodo());
        
        SimpleDateFormat formateador = new SimpleDateFormat("yyyy-MM-dd");
        String hoy = formateador.format(new Date());
        
        factura.setFechaEmision(hoy);
        factura.setFechaVencimiento(this.periodo() + "-15");
        factura.setInteresPorAtraso(0); 
        factura.setEstado("EMITIDA");
        return factura;
    }
}