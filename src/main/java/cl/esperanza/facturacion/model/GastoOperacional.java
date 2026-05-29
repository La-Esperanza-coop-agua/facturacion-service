package cl.esperanza.facturacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gasto_operacional")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GastoOperacional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String descripcion; 

    @Column(nullable = false)
    private int monto;

    @Column(nullable = false)
    private String fechaGasto;
}