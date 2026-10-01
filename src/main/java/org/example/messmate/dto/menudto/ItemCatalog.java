package org.example.messmate.dto.menudto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ItemCatalog {
    private List<DietItemDto> diets;
    private List<ExtraItemDto> extras;
}
