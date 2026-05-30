package cl.esperanza.facturacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- IMPORTANTE: Te faltaba esta importación
import org.springframework.stereotype.Repository;
import cl.esperanza.facturacion.model.GastoOperacional;

@Repository
public interface GastoOperacionalRepository extends JpaRepository<GastoOperacional, Integer> {

    @Query("SELECT COALESCE(SUM(go.monto), 0) FROM GastoOperacional go")
    int sumTotalGastos();
}