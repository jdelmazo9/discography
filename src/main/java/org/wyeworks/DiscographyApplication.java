package org.wyeworks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.wyeworks.exceptions.TrelloAPIException;
import org.wyeworks.model.Album;
import org.wyeworks.services.album.AlbumService;
import org.wyeworks.services.board.BoardService;
import org.wyeworks.services.picture.PictureService;

import java.util.List;

@Slf4j
public class DiscographyApplication {

    @Autowired
    AlbumService albumService;
    @Autowired
    PictureService pictureService;
    @Autowired
    BoardService boardService;

    public void startProcess() {

        try {
            //Get albums from file
            List<Album> partialAlbums = albumService.getAllAlbums();
            log.info("The albums have been recovered from the data source");

            //Get album pictures
            List<Album> albums = pictureService.getAlbumsWithPictures(partialAlbums);
            log.info("The albums were filled with images");

            //Creates a dashboard and push lists and albums information
            boardService.postAlbums(albums);
            log.info("The albums data was pushed into a Trello dashboard");

        } catch (TrelloAPIException e) {
            log.error("Couldn't create trello dashboard: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("The system execution has failed: " + e.getMessage());
        }
    }
}
