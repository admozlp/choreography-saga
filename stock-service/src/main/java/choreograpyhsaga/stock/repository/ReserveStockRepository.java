package choreograpyhsaga.stock.repository;

import choreograpyhsaga.stock.model.ReserveStock;
import choreograpyhsaga.stock.model.enm.ReserveStockStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface ReserveStockRepository extends JpaRepository<ReserveStock, Long> {
    Optional<ReserveStock> findByOrderId(Long orderId);

    boolean existsByOrderId(Long orderId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ReserveStock r SET r.status = :expired " +
            "WHERE r.status = :reserved AND r.expiresAt < :now")
    int expireStaleReservations(@Param("reserved") ReserveStockStatus reserved,
                                @Param("expired") ReserveStockStatus expired,
                                @Param("now") Instant now);
}
