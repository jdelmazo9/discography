package org.wyeworks;

import lombok.extern.slf4j.Slf4j;
import org.wyeworks.client.SpotifyClient;
import org.wyeworks.client.TrelloClient;
import org.wyeworks.exception.TrelloAPIException;
import org.wyeworks.model.Album;
import org.wyeworks.service.AlbumService;
import org.wyeworks.service.FileAlbumService;
import org.wyeworks.service.PictureService;
import org.wyeworks.service.TrelloService;

import java.util.List;

@Slf4j
public class App {
    public static void main( String[] args ) {

        //Start project configuration
        AlbumService albumService = new FileAlbumService("src/main/resources/textfiles/discography.txt");
        SpotifyClient spotifyClient = new SpotifyClient("", "");
        PictureService pictureService = new PictureService(spotifyClient);
        TrelloClient trelloClient = new TrelloClient("", "", "https://api.trello.com");
        TrelloService trelloService = new TrelloService(trelloClient);

        //Get albums from file
        List<Album> partialAlbums = albumService.getAllAlbums();
        partialAlbums.forEach(album -> log.info(album.toString()));
        log.info("The albums have been recovered from the data source");

        //Get album pictures
        List<Album> albums = pictureService.getAlbumsWithPictures(partialAlbums);
        log.info("The albums were filled with images");

        //Creates a dashboard and push lists and albums information
        try {
            trelloService.postAlbums(albums);
            log.info("The albums data was pushed into a Trello dashboard");
        } catch (TrelloAPIException e) {
            log.error("Couldn't create trello dashboard");
        }
    }
}
