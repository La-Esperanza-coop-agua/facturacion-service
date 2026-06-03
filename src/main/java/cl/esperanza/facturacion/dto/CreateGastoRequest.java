package cl.esperanza.facturacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateGastoRequest(
    @NotBlank(message = "La descripción del gasto es obligatoria") String descripcion,
    @Positive(message = "El monto del gasto debe ser mayor a cero") int monto,
    @NotBlank(message = "fechaGasto no puede estar vacio") String fechaGasto
) {
}
