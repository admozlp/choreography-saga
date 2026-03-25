package choreograpyhsaga.stock.service;


import choreographysaga.common.dto.DecreaseStockRequest;
import choreograpyhsaga.stock.exception.OperationException;
import choreograpyhsaga.stock.model.Stock;
import choreograpyhsaga.stock.repository.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository repository;

    /*
    Isolation.READ_UNCOMMITTED: başka bir transaction henüz commit etmedği veriyi okuyabilir, dirty read olabilir.:
    * SORUN: bir transaction henüz commit etmediği veriyi okursak, o transaction rollback yaparsa bizim okuduğumuz veri geçersiz olur. Bu durum tutarsızlığa yol açar.

    Isolation.READ_COMMITTED(default): sadece commit edilmiş veriyi okuyabilir, dirty read olmaz ancak non-repeatable read ve phantom read olabilir.
    * SORUN: bir transaction içinde aynı veriyi tekrar okuduğumuzda farklı veri alabiliriz, çünkü başka bir transaction o veriyi değiştirmiş olabilir ve commit etmiş olabilir. Bu durum tutarsızlığa yol açar.

    Isolation.REPEATABLE_READ: bir transaction içinde aynı veriyi tekrar okuduğunda aynı sonucu alır, bunu istememizin sebebi; eğer bir transaction içerisinde
    aynı veriyi tekrar okudduğumuzda farklı veri alırsak, bu işlem kendi içinde tutarlı olmaz. İlk okuma ile şu an kini karşılaştırma gibi yollara gitmemize sebep olur.
    * SORUN: phantom read olabilir, yani bir transaction içinde belirli bir koşulu sağlayan verileri okuduğumuzda, başka bir transaction o koşulu sağlayan yeni veriler ekleyebilir ve commit edebilir.
    Bu durumda ilk okuma ile şu an ki karşılaştırma yapmamız gerekir.

    Isolation.SERIALIZABLE: en yüksek izolasyon seviyesidir, tüm transactionlar birbirini seri olarak çalıştırır, dirty read, non-repeatable read ve phantom read olmaz ancak performans düşer.
    * SORUN: farklı transactionların aynı anda okumasına izin verir ancak yazmasına izin vermez, bu durumlarda deadlock oluşabilir, yani iki veya daha fazla transaction birbirini bekler ve hiçbiri ilerleyemez.
    veri tabanı birini rollback eder ve diğer transaction devam eder, bu durumda retry mekanizması eklememiz gerekir, yani bir transaction başarısız olduğunda belirli bir sayıda tekrar denemesi yapar.

     */
    @Retryable(value = {LockTimeoutException.class}, maxRetries = 3, delay = 200)
    @Transactional
    public void decreaseStock(DecreaseStockRequest request) {
        log.info("Decreasing stock for productId: {} with quantity: {}", request.productId(), request.quantity());
        Stock stock = repository.findByProductId(request.productId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + request.productId()));

        if (stock.getQuantity() < request.quantity()) {
            throw new OperationException("Insufficient stock for productId: " + request.productId(), HttpStatus.CONFLICT);
        }

        stock.setQuantity(stock.getQuantity() - request.quantity());
        repository.save(stock);
        log.info("Stock decreased for productId: {} with quantity: {}", request.productId(), request.quantity());
    }
}
