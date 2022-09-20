package org.wyeworks.services.board;

import org.wyeworks.model.Album;

import java.util.List;

public interface BoardService {

    void postAlbums(List<Album> albums);
}
