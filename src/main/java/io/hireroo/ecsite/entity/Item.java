package io.hireroo.ecsite.entity;

import lombok.Data;

@Data
public class Item {
    private String id;
    private String name;
    private String description;
    private Integer price;
    private Integer stock;
}
