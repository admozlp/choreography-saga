package choreograpyhsaga.stock.repository;

import choreograpyhsaga.stock.model.ReserveStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReserveStockRepository extends JpaRepository<ReserveStock, Long> {
}
