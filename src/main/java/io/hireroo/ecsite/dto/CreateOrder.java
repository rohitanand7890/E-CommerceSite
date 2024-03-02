package io.hireroo.ecsite.dto;

import java.io.Serializable;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateOrder implements Serializable {
    private String id;

    @JsonProperty("item_id")
    private String itemId;

    @JsonProperty("user_id")
    @Pattern(regexp = "^[A-Za-z0-9_]{1,8}$", message = "user id must be max 8 characters")
    private String userId;

    @NotNull(message = "quantity cannot be empty or null" )
    private Integer quantity;

    private Integer amount;

    public CreateOrder() {
        // Generate a unique ID with 8 characters
        this.id = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
