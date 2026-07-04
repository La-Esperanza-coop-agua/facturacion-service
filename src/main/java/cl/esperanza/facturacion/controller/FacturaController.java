package cl.esperanza.facturacion.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;

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
    
    @Operation(
        summary = "Generar una nueva factura", 
        description = "Valida si el socio existe y obtiene su última lectura de consumo antes de generar la factura mensual"
    )
    @ApiResponse(responseCode = "201", description = "Factura generada exitosamente")
    @ApiResponse(responseCode = "404", description = "Socio no encontrado o sin lecturas registradas")
    @PostMapping("/generar")
    public ResponseEntity<Factura> generarNuevaFactura(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Estructura JSON requerida para generar una factura",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CreateFacturaRequest.class),
                examples = @ExampleObject(
                    name = "Ejemplo Factura Mensual",
                    value = "{\"runSocio\": \"19234567-8\", \"periodo\": \"2023-10\", \"montoFijo\": 2500}"
                )
            )
        )
        @Valid @org.springframework.web.bind.annotation.RequestBody CreateFacturaRequest request) {
        
        Boolean existeSocio = false;

        try {
            existeSocio = sociosWebClient.get()
                .uri("http://localhost:8082/api/v1/socios/existe/{run}", request.runSocio())
                .retrieve().bodyToMono(Boolean.class).block();
        } catch (Exception e) {
            throw new RuntimeException("Error de conexion con el microservicio 'Socios'");
        }
        
        if (Boolean.FALSE.equals(existeSocio)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se puede generar la factura: El socio con RUN " + request.runSocio() + " no existe");
        }

        Factura facturaModel = FacturaMapper.toModel(request);

        try {
            ConsumoRequest ultimaLectura = consumoWebClient.get()
                .uri("http://localhost:8085/api/v1/lecturas/socio/{runSocio}/ultima", request.runSocio())
                .retrieve().bodyToMono(ConsumoRequest.class).block();
            
            if (ultimaLectura != null){
                facturaModel.setMetrosCubicosFacturados(ultimaLectura.consumoMensual());
            }
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException.NotFound e) {
            throw new org.springframework.web.server.ResponseStatusException(
                HttpStatus.NOT_FOUND, "El socio existe, pero no registra lecturas de consumo para este periodo");
        } catch (Exception e) {
            throw new org.springframework.web.server.ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE, "No se pudo obtener la lectura del socio (Servicio de Lecturas no disponible)");
        }
        
        Factura nuevaFactura = facturaService.generarFactura(facturaModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFactura);
    }
    
    @Operation(summary = "Registrar un nuevo gasto operacional", description = "Guarda un nuevo gasto en el sistema (ej: mantenimiento, electricidad, etc.)")
    @ApiResponse(responseCode = "201", description = "Gasto registrado exitosamente")
    @PostMapping("/gasto")
    public ResponseEntity<GastoOperacional> registrarNuevoGasto(
        @org.springframework.web.bind.annotation.RequestBody GastoOperacional gasto) {
        
        GastoOperacional nuevoGasto = facturaService.registrarGasto(gasto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoGasto);
    }
    @Operation(summary = "Aplicar interés por vencimiento", description = "Aplica recargos a una factura específica que ha superado su fecha de pago")
    @ApiResponse(responseCode = "200", description = "Interés aplicado correctamente")
    @PutMapping("/{id}/revisar-vencimiento")
    public ResponseEntity<Factura> revisarVencimiento(@PathVariable Integer id) {
        Factura facturaActualizada = facturaService.aplicarInteresPorVencimiento(id);
        return ResponseEntity.ok(facturaActualizada);
    }

    @Operation(summary = "Historial de facturas por socio", description = "Obtiene todas las facturas asociadas al RUN de un socio")
    @ApiResponse(responseCode = "200", description = "Lista de facturas retornada")
    @GetMapping("/socio/{run}")
    public ResponseEntity<List<Factura>> getFacturasPorSocio(@PathVariable String run) {
        Boolean existeSocio = false;

        try {
            existeSocio = sociosWebClient.get()
                .uri("http://localhost:8082/api/v1/socios/existe/{run}", run.trim())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        if (Boolean.TRUE.equals(existeSocio)) {
        List<Factura> facturas = facturaService.obtenerPorSocio(run);
        return ResponseEntity.ok(facturas);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Listar todos los gastos operacionales", description = "Devuelve el detalle de los gastos del sistema de agua potable")
    @GetMapping("/gasto/todos")
    public ResponseEntity<List<GastoOperacional>> obtenerGastos() {
        return ResponseEntity.ok(facturaService.obtenerTodosLosGastos());
    }

    @Operation(summary = "Registrar pago de factura", description = "Cambia el estado de una factura específica a 'Pagada'")
    @ApiResponse(responseCode = "200", description = "Factura marcada como pagada")
    @PutMapping("/{id}/pagar")
    public ResponseEntity<Factura> pagarFactura(@PathVariable Integer id) {
        Factura facturaPagada = facturaService.cambiarEstadoAPagada(id);
        return ResponseEntity.ok(facturaPagada);
    }

    @Operation(summary = "Obtener monto total de gastos", description = "Calcula la suma total de todos los gastos operacionales registrados")
    @GetMapping("/gasto/total-monto")
    public ResponseEntity<Integer> getTotalGastosOperacionales() {
        return ResponseEntity.ok(facturaService.obtenerTotalGastos());
    }

    @Operation(summary = "Total de agua facturada", description = "Calcula el total de metros cúbicos facturados en un periodo (ej: 2023-10)")
    @GetMapping("/periodo/{periodo}/total-consumo")
    public ResponseEntity<Double> getTotalConsumoPorPeriodo(@PathVariable String periodo) {
        Double totalAgua = facturaService.obtenerTotalAguaFacturadaPorPeriodo(periodo);
        return ResponseEntity.ok(totalAgua);
    }
}
