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
import org.springframework.web.server.ResponseStatusException;

import cl.esperanza.facturacion.dto.ConsumoRequest;
import cl.esperanza.facturacion.dto.CreateFacturaRequest;
import cl.esperanza.facturacion.dto.SocioResponse;
import cl.esperanza.facturacion.mapper.FacturaMapper;
import cl.esperanza.facturacion.model.Factura;
import cl.esperanza.facturacion.model.GastoOperacional;
import cl.esperanza.facturacion.service.FacturaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/facturacion")
public class FacturaController {

    private final FacturaService facturaService;
    private final WebClient consumoWebClient;
    private final WebClient sociosWebClient;

    public FacturaController(FacturaService facturaService, WebClient consumoWebClient, WebClient sociosWebClient) {
        this.facturaService = facturaService;
        this.consumoWebClient = consumoWebClient;
        this.sociosWebClient = sociosWebClient;
    }
    
    @PostMapping("/generar")
    public ResponseEntity<Factura> generarNuevaFactura(@Valid @RequestBody CreateFacturaRequest request) {
        Boolean existeSocio = false;

        try {
            existeSocio = sociosWebClient.get()
                .uri("http://localhost:8082/api/v1/socios/existe/{run}", request.runSocio())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
        } catch (Exception e) {
            throw new RuntimeException("Error de conexion con el microservicio 'Socios'");
        }
        
        // si el boolean existeSocio es false se corta el proceso
        if (Boolean.FALSE.equals(existeSocio)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se puede generar la factura: El socio con RUN " + request.runSocio() + " no existe.");
        }

        Factura facturaModel = FacturaMapper.toModel(request);

        try {
            ConsumoRequest ultimaLectura = consumoWebClient.get()
                .uri("http://localhost:8085/api/v1/lecturas/socio/{runSocio}/ultima", request.runSocio())
                .retrieve()
                .bodyToMono(ConsumoRequest.class)
                .block();
            
            if (ultimaLectura != null){
                facturaModel.setMetrosCubicosFacturados(ultimaLectura.consumoMensual());
            }
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException.NotFound e) {
            throw new org.springframework.web.server.ResponseStatusException(
                HttpStatus.NOT_FOUND, "El socio existe, pero no registra lecturas de consumo para este periodo.");
        } catch (Exception e) {
            throw new org.springframework.web.server.ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE, "No se pudo obtener la lectura del socio (Servicio de Lecturas no disponible)");
        }
        
        Factura nuevaFactura = facturaService.generarFactura(facturaModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFactura);
    }
    
    @PutMapping("/{id}/revisar-vencimiento")
    public ResponseEntity<Factura> revisarVencimiento(@PathVariable Integer id) {
        Factura facturaActualizada = facturaService.aplicarInteresPorVencimiento(id);
        return ResponseEntity.ok(facturaActualizada);
    }

    @GetMapping("/socio/{run}")
    public ResponseEntity<List<Factura>> getFacturasPorSocio(@PathVariable String run) {
        try {
            sociosWebClient.get()
                .uri("/run/{runSocio}", run.trim().toUpperCase())
                .retrieve()
                .bodyToMono(SocioResponse.class)
                .block();
        } catch (Exception e) {
            throw new RuntimeException("El socio con RUN "+ run +" no existe en el sistema...");
        }

        List<Factura> facturas = facturaService.obtenerPorSocio(run);
        return ResponseEntity.ok(facturas);
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

    @GetMapping("/periodo/{periodo}/total-consumo")
    public ResponseEntity<Double> getTotalConsumoPorPeriodo(@PathVariable String periodo) {
        Double totalAgua = facturaService.obtenerTotalAguaFacturadaPorPeriodo(periodo);
        return ResponseEntity.ok(totalAgua);
    }
}
