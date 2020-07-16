package org.technbolts.sandboxgateway.infra.web;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductDTO {

    @JsonProperty("id")
    public final String id;

    @JsonProperty("sku")
    public final String sku;

    public ProductDTO(String id, String sku) {
        this.id = id;
        this.sku = sku;
    }
}
