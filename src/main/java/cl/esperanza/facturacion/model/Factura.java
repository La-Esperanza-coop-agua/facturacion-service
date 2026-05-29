package cl.esperanza.facturacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "factura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 13)
    private String runSocio;

    @Column(nullable = false)
    private String periodo; 

    @Column(nullable = false)
    private double metrosCubicosFacturados;

    @Column(nullable = false)
    private int subtotalConsumo;

    @Column(nullable = false)
    private int cargoFijo;

    @Column(nullable = false)
    private int interesPorAtraso; 

    @Column(nullable = false)
    private int montoTotal;

    @Column(nullable = false)
    private String fechaEmision;

    @Column(nullable = false)
    private String fechaVencimiento; 

    @Column(nullable = false)
    private String estado; 
}