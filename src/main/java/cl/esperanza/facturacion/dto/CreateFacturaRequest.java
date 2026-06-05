package cl.esperanza.facturacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateFacturaRequest(
    @NotBlank(message = "El RUN del socio es obligatorio")String runSocio,
    @NotBlank(message = "El periodo es obligatorio (Ej: 2026-05)") String periodo,

    @PositiveOrZero(message = "SubTotalConsumo no puede estar vacio") int subtotalConsumo,
    @PositiveOrZero(message = "cargoFijo no puede estar vacio") int cargoFijo,
    @PositiveOrZero(message = "interesPorAtraso no puede estar vacio") int interesPorAtraso,
    @PositiveOrZero(message = "montoTotal no puede estar vacio") int montoTotal,
    
    @NotBlank(message = "fechaEmision no puede estar vacio") String fechaEmision,
    @NotBlank(message = "fechaVencimiento no puede estar vacio") String fechaVencimiento,
    @NotBlank(message = "estado no puede estar vacio") String estado
) {}