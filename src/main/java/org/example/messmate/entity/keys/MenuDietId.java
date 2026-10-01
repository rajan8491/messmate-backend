package org.example.messmate.entity.keys;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class MenuDietId implements Serializable {

    private Long menuPlanId;

    private Long dietId;
}