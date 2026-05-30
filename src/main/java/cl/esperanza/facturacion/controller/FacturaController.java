package cl.esperanza.facturacion.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List; 

import cl.esperanza.facturacion.model.Factura;
import cl.esperanza.facturacion.model.GastoOperacional;
import cl.esperanza.facturacion.service.FacturaService;
import cl.esperanza.facturacion.dto.CreateFacturaRequest;
import cl.esperanza.facturacion.dto.CreateGastoRequest;

@RestController
@RequestMapping("/api/v1/facturacion")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @PostMapping("/generar")
    public ResponseEntity<Factura> generarNuevaFactura(@Valid @RequestBody CreateFacturaRequest request) {
        Factura nuevaFactura = facturaService.generarFactura(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFactura);
    }

    @PutMapping("/{id}/revisar-vencimiento")
    public ResponseEntity<Factura> revisarVencimiento(@PathVariable Integer id) {
        Factura facturaActualizada = facturaService.aplicarInteresPorVencimiento(id);
        return ResponseEntity.ok(facturaActualizada);
    }

    @GetMapping("/socio/{run}")
    public ResponseEntity<List<Factura>> getFacturasPorSocio(@PathVariable String run) {
        return ResponseEntity.ok(facturaService.obtenerPorSocio(run));
    }

    @PostMapping("/gasto")
    public ResponseEntity<GastoOperacional> registrarGastoOperacional(@Valid @RequestBody CreateGastoRequest request) {
        GastoOperacional nuevoGasto = facturaService.registrarGasto(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoGasto);
    }

    @GetMapping("/gasto/todos")
    public ResponseEntity<List<GastoOperacional>> obtenerGastos() {
        return ResponseEntity.ok(facturaService.obtenerTodosLosGastos());
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<Factura> pagarFactura(@PathVariable Integer id) {
        Factura facturaPagada = facturaService.cambiarEstadoAPagada(id);
        return ResponseEntity.ok(facturaPagada);
    }

    @GetMapping("/gasto/total-monto")
        public ResponseEntity<Integer> getTotalGastosOperacionales() {
        return ResponseEntity.ok(facturaService.obtenerTotalGastos());
    }
}
