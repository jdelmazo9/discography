package org.wyeworks.services.picture;

import org.wyeworks.model.Album;

import java.util.List;

public interface PictureService {

    List<Album> getAlbumsWithPictures(List<Album> albums);
}
