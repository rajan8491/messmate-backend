package org.example.messmate.dto.menudto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExtraItemDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
}
