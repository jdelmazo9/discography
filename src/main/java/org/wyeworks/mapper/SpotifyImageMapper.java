package org.wyeworks.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.wyeworks.model.Picture;
import se.michaelthelin.spotify.model_objects.specification.Image;

import java.util.List;

@Mapper
public interface SpotifyImageMapper {
    SpotifyImageMapper INSTANCE = Mappers.getMapper(SpotifyImageMapper.class);

    Picture convert(Image album);
}
