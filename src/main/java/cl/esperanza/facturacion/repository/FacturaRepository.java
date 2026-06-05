package cl.esperanza.facturacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cl.esperanza.facturacion.model.Factura;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {
    List<Factura> findByRunSocio(String runSocio);
    List<Factura> findByEstado(String estado);

    @Query("SELECT COALESCE(SUM(f.metrosCubicosFacturados), 0.0) FROM Factura f WHERE f.periodo = :periodo")
    Double sumarMetrosCubicosPorPeriodo(@Param("periodo") String periodo);
}