package org.wyeworks.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.wyeworks.dto.FileAlbumDTO;
import org.wyeworks.model.Album;

import java.util.List;

@Mapper
public interface FileAlbumMapper {
    FileAlbumMapper INSTANCE = Mappers.getMapper(FileAlbumMapper.class);

    List<Album> convert(List<FileAlbumDTO> album);
}
