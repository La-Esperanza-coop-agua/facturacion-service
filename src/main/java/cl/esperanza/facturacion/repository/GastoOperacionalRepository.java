package cl.esperanza.facturacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cl.esperanza.facturacion.model.GastoOperacional;

@Repository
public interface GastoOperacionalRepository extends JpaRepository<GastoOperacional, Integer> {
}