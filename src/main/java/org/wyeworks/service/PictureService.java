package org.wyeworks.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.wyeworks.client.SpotifyClient;
import org.wyeworks.exception.ProcessingPictureException;
import org.wyeworks.model.Album;
import org.wyeworks.model.Picture;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class PictureService {

    private final SpotifyClient spotifyClient;

    public PictureService(SpotifyClient spotifyClient) {
        this.spotifyClient = spotifyClient;
    }

    public List<Album> getAlbumsWithPictures(List<Album> albums) {

        return albums.parallelStream().map(album ->
                album.toBuilder().picture(spotifyClient.getAlbumPicture(album.getName())).build())
                .collect(Collectors.toList());
    }
}
