package org.example.messmate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ExtraPurchaseInfo {
    private Long itemId;
    private Integer qty;
}
