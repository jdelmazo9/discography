package org.wyeworks.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class TrelloListDTO {
    String id;
    String name;
    String pos;
}
