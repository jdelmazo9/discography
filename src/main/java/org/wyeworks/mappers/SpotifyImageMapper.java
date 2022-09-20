package org.wyeworks.mappers;

import org.mapstruct.Mapper;
import org.wyeworks.model.Picture;
import se.michaelthelin.spotify.model_objects.specification.Image;

@Mapper(componentModel = "spring")
public interface SpotifyImageMapper {

    Picture convert(Image album);
}
