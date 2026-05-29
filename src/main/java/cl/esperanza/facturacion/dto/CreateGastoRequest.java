package cl.esperanza.facturacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import cl.esperanza.facturacion.model.GastoOperacional;
import java.text.SimpleDateFormat;
import java.util.Date;

public record CreateGastoRequest(
    @NotBlank(message = "La descripción del gasto es obligatoria")
    String descripcion,

    @Positive(message = "El monto del gasto debe ser mayor a cero")
    int monto
) {
    public GastoOperacional toEntity() {
        GastoOperacional gasto = new GastoOperacional();
        gasto.setDescripcion(this.descripcion());
        gasto.setMonto(this.monto());
        
        SimpleDateFormat formateador = new SimpleDateFormat("yyyy-MM-dd");
        gasto.setFechaGasto(formateador.format(new Date()));
        return gasto;
    }
}
