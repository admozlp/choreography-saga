package choreograpyhsaga.stock.repository;

import choreograpyhsaga.stock.model.ReserveStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReserveStockRepository extends JpaRepository<ReserveStock, Long> {
    Optional<ReserveStock> findByOrderId(Long orderId);
}
