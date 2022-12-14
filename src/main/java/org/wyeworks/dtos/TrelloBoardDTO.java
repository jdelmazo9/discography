package org.wyeworks.dtos;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class TrelloBoardDTO {
    String id;
    String name;
    String url;
}
