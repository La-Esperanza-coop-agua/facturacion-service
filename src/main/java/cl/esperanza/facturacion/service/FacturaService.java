package cl.esperanza.facturacion.service;

import org.springframework.stereotype.Service;
import cl.esperanza.facturacion.model.Factura;
import cl.esperanza.facturacion.model.GastoOperacional;
import cl.esperanza.facturacion.repository.FacturaRepository;
import cl.esperanza.facturacion.repository.GastoOperacionalRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepo;
    private final GastoOperacionalRepository gastoRepo;

    private final int VALOR_METRO_CUBICO = 500; 
    private final int CARGO_FIJO = 3000;         
    private final int INTERES_MULTA_ATRASO = 1500; 

    public FacturaService(FacturaRepository facturaRepo, GastoOperacionalRepository gastoRepo) {
        this.facturaRepo = facturaRepo;
        this.gastoRepo = gastoRepo;
    }

    public Factura generarFactura(Factura factura) {
        int subtotal = (int) (factura.getMetrosCubicosFacturados() * VALOR_METRO_CUBICO);
        factura.setSubtotalConsumo(subtotal);
        factura.setCargoFijo(CARGO_FIJO);
        factura.setMontoTotal(subtotal + CARGO_FIJO + factura.getInteresPorAtraso());
        
        return facturaRepo.save(factura);
    }

    public Factura aplicarInteresPorVencimiento(Integer id) {
        
        Factura factura = facturaRepo.findById(id).orElse(null);

        if (factura == null) {
            throw new RuntimeException("No se encontró la factura con ID: " + id);
        }

        SimpleDateFormat formateador = new SimpleDateFormat("yyyy-MM-dd");
        String fechaHoy = formateador.format(new Date());

        if (!"PAGADA".equals(factura.getEstado()) && fechaHoy.compareTo(factura.getFechaVencimiento()) > 0) {
            factura.setEstado("VENCIDA");
            factura.setInteresPorAtraso(INTERES_MULTA_ATRASO);
            factura.setMontoTotal(factura.getSubtotalConsumo() + factura.getCargoFijo() + INTERES_MULTA_ATRASO);
            return facturaRepo.save(factura);
        }
        
        return factura;
    }

    

    public GastoOperacional registrarGasto(GastoOperacional gasto) {
        return gastoRepo.save(gasto);
    }

    public List<Factura> obtenerPorSocio(String run) {
        return facturaRepo.findByRunSocio(run);
    }

    public List<GastoOperacional> obtenerTodosLosGastos() {
        return gastoRepo.findAll();
    }

    public Factura cambiarEstadoAPagada(Integer id) {
        Factura factura = facturaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la factura con ID: " + id));
        factura.setEstado("PAGADA");
        return facturaRepo.save(factura);
    }

    public int obtenerTotalGastos() {
        return gastoRepo.sumTotalGastos();
    }
}