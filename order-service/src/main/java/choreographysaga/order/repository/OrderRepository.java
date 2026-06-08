package choreographysaga.order.repository;

import choreographysaga.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("select o from Order o where o.id = ?1 and o.status = ?2")
    Optional<Order> findByIdAndStatus(Long id, Order.OrderStatus status);
}