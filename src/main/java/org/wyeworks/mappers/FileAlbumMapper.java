package org.wyeworks.mappers;

import org.mapstruct.Mapper;
import org.wyeworks.dtos.FileAlbumDTO;
import org.wyeworks.model.Album;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FileAlbumMapper {

    List<Album> convert(List<FileAlbumDTO> album);
}
