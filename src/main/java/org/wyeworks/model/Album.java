package org.wyeworks.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Optional;

@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@Getter
public class Album {
    private Integer year;
    private String name;
    private Optional<Picture> picture;
}