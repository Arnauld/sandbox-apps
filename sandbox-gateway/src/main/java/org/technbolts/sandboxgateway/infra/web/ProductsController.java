package org.technbolts.sandboxgateway.infra.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
public class ProductsController {

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('product_read')")
    public Mono<ProductDTO> product(@PathVariable String id) {
        ProductDTO dto = new ProductDTO(id, "sku-" + id);
        return Mono.create(sink -> sink.success(dto));
    }
}