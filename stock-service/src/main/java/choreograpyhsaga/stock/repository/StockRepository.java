package choreograpyhsaga.stock.repository;

import choreograpyhsaga.stock.model.Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    /*
      LockModeType.OPTIMISTIC: farklı transaction'ların aynı anda okumasına izin verir ancak yazarlarken versiyon kontrolü yapar, eğer bir transaction veriyi okuduktan sonra başka bir transaction o veriyi değiştirmiş ve
      commit etmişse, ilk transaction yazmaya çalıştığında OptimisticLockException fırlatır. Bu durumda retry mekanizması eklememiz gerekir, yani bir transaction başarısız olduğunda belirli bir sayıda tekrar denemesi yapar.

      LockModeType.PESSIMISTIC_WRITE: farklı transaction'ların aynı anda okumasına izin vermez, gelen transaction diğerinin commitlemesini bekler,
      timeout ayarlanmalıdır default -1 (yml dosyasında verilebilir, tüm psimistik write sorgularında geçerli olur.) yani sonsuza kadar bekler. timeout exception fırlatıldığında duruma göre retry veya direkt hata dönülebilir
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Stock> findByProductId(long l);

    @Query(value = """
             select s.quantity - coalesce(sum(rs.quantity), 0) from stocks s
             left join reserve_stocks rs on (rs.stock_id = s.id and rs.status = 'RESERVED' and rs.expires_at > now())
             where s.id = :id
             group by s.id, s.quantity
            """, nativeQuery = true)
    Integer findActiveQuantity(@Param("id") Long id);
}
