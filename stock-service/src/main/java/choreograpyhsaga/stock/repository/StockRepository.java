package choreograpyhsaga.stock.repository;

import choreograpyhsaga.stock.model.Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

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
}
