package org.example.messmate.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.example.messmate.enums.ItemType;
import org.example.messmate.enums.MealType;

import java.util.List;

@Getter
@Setter
public class AddRatingRequest {

    @NotNull
    @Positive
    private Long itemId;

    @NotNull
    private ItemType itemType;

    @NotNull
    private MealType meal;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    @Size(max = 10)
    private List<@NotBlank String> tags;

    @Size(max = 100)
    private String suggestion;
}
