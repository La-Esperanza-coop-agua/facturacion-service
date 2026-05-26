package cl.esperanza.facturacion.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "facturas")

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int socioId;

    private double consumoMensual;

    private double tarifaMetroCubico;

    private double montoTotal;

    private LocalDate fechaEmision;

    private LocalDate fechaVencimiento;

    private String estadoPago;
}