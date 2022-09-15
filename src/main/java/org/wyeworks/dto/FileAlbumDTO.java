package org.wyeworks.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class FileAlbumDTO {
    private Integer year;
    private String name;
}