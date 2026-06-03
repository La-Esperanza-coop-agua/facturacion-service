package cl.esperanza.facturacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocioResponse {
    private String run;
    private int telefono;
    private String nombre;
    private String apellido;
    private String direccion;
    private String correo;
}