package choreograpyhsaga.stock.service;


import choreograpyhsaga.stock.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;

    public void getAll() {

    }
}
