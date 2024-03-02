package io.hireroo.ecsite.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Order {
    private String id;
    @JsonProperty("item_id")
    private String itemId;
    @JsonProperty("user_id")
    private String userId;
    private Integer amount;
    private Integer quantity;
}
