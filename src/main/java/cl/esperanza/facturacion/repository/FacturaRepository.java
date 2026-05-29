package cl.esperanza.facturacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cl.esperanza.facturacion.model.Factura;
import java.util.List;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {
    List<Factura> findByRunSocio(String runSocio);
    List<Factura> findByEstado(String estado);
}