package io.hireroo.ecsite.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class CreateUser implements Serializable {
    private String id;
    private String name;
    private Integer savings;
}
