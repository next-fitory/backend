package org.fitory.product.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Product {
    private Long id;
    private String name;
    private String description;

    @Setter
    private int price;
}
