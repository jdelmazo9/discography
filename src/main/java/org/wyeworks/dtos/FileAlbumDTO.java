package org.wyeworks.dtos;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Builder
@Getter
public class FileAlbumDTO {
    private Integer year;
    private String name;
}