package org.wyeworks.services.picture;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wyeworks.clients.SpotifyClient;
import org.wyeworks.model.Album;

import java.util.*;

@Slf4j
@Component
public class SpotifyPictureService implements PictureService {

    private final SpotifyClient spotifyClient;

    public SpotifyPictureService(@Autowired SpotifyClient spotifyClient) {
        this.spotifyClient = spotifyClient;
    }

    /**
     * Uses the Spotify API service applying parallelism to have a better performance
     *
     * @param albums
     * @return
     */
    public List<Album> getAlbumsWithPictures(List<Album> albums) {

        return albums.parallelStream()
                .map(album -> album.toBuilder().picture(spotifyClient.getAlbumPicture(album.getName())).build())
                .toList();
    }
}
