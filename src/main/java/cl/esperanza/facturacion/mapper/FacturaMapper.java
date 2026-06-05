package cl.esperanza.facturacion.mapper;

import cl.esperanza.facturacion.dto.CreateFacturaRequest;
import cl.esperanza.facturacion.model.Factura;

public class FacturaMapper {

    public static Factura toModel(CreateFacturaRequest request){
        return new Factura(null,
            request.runSocio(), request.periodo(), 0.0,
            request.subtotalConsumo(), request.cargoFijo(), request.interesPorAtraso(),
            request.montoTotal(), request.fechaEmision(), request.fechaVencimiento(),
            request.estado()
        );
    }
}
