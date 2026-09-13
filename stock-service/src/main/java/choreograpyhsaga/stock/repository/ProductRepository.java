package choreograpyhsaga.stock.repository;

import choreograpyhsaga.stock.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
