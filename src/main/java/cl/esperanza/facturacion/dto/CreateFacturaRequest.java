package cl.esperanza.facturacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import cl.esperanza.facturacion.model.Factura;
import java.text.SimpleDateFormat;
import java.util.Date;

public record CreateFacturaRequest(
    @NotBlank(message = "El RUN del socio es obligatorio")
    String runSocio,

    @NotBlank(message = "El periodo es obligatorio (Ej: 2026-05)")
    String periodo,

    @PositiveOrZero(message = "Los metros cúbicos no pueden ser negativos")
    double metrosCubicosFacturados
) {
    public Factura toEntity() {
        Factura factura = new Factura();
        factura.setRunSocio(this.runSocio());
        factura.setPeriodo(this.periodo());
        factura.setMetrosCubicosFacturados(this.metrosCubicosFacturados());
        
        SimpleDateFormat formateador = new SimpleDateFormat("yyyy-MM-dd");
        String hoy = formateador.format(new Date());
        
        factura.setFechaEmision(hoy);
        
        factura.setFechaVencimiento(this.periodo() + "-15");
        
        factura.setInteresPorAtraso(0); 
        factura.setEstado("EMITIDA");
        return factura;
    }
}