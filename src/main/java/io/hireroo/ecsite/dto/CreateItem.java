package io.hireroo.ecsite.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class CreateItem implements Serializable {
    private String id;
    private String name;
    private String description;
    private Integer price;
    private Integer stock;
}
