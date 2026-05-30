package cl.esperanza.facturacion.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import cl.esperanza.facturacion.dto.ConsumoRequest;
import cl.esperanza.facturacion.dto.CreateFacturaRequest;
import cl.esperanza.facturacion.dto.CreateGastoRequest;
import cl.esperanza.facturacion.model.Factura;
import cl.esperanza.facturacion.model.GastoOperacional;
import cl.esperanza.facturacion.service.FacturaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/facturacion")
public class FacturaController {

    private final FacturaService facturaService;
    private final WebClient consumoWebClient;

    public FacturaController(FacturaService facturaService, WebClient consumoWebClient) {
        this.facturaService = facturaService;
        this.consumoWebClient = consumoWebClient;
    }

    @PostMapping("/generar")
    public ResponseEntity<Factura> generarNuevaFactura(@Valid @RequestBody CreateFacturaRequest request) {
        Factura facturaEntity = request.toEntity();

        try {
            ConsumoRequest lectura = consumoWebClient.get()
                .uri("/socio/{run}/periodo/{periodo}", request.runSocio(), request.periodo())
                .retrieve()
                .bodyToMono(ConsumoRequest.class)
                .block();
            if (lectura != null) {
                facturaEntity.setMetrosCubicosFacturados(lectura.metrosCubicosConsumidos());
            }
        } catch (Exception e) {
            throw new RuntimeException("No se pudo obtener la lectura del socio en este periodo");
        }
        Factura nuevaFactura = facturaService.generarFactura(facturaEntity);
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
}
