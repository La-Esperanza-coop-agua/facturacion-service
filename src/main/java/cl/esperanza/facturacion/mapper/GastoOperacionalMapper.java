package cl.esperanza.facturacion.mapper;

import cl.esperanza.facturacion.dto.CreateGastoRequest;
import cl.esperanza.facturacion.model.GastoOperacional;

public class GastoOperacionalMapper {

    public static GastoOperacional toModel(CreateGastoRequest request){
        return new GastoOperacional(null,
            request.descripcion(), request.monto(), request.fechaGasto()
        );
    }
}
