package org.wyeworks.clients;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wyeworks.exceptions.ClientCredentialsException;
import org.wyeworks.mappers.SpotifyImageMapper;
import org.wyeworks.model.Picture;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class SpotifyClient {

    SpotifyApi spotifyApi;

    @Autowired
    private SpotifyImageMapper mapper;

    public SpotifyClient(@Value("${api.spotify.clientid}") String clientId, @Value("${api.spotify.clientsecret}") String clientSecret) {
        spotifyApi = SpotifyApi.builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();

        this.setCredentialsRequest();
    }

    public Optional<Picture> getAlbumPicture(String name) {

        log.info("trying to get image from album " + name);
        try {
            Paging<AlbumSimplified> albumSimplifiedPaging = spotifyApi.searchAlbums(name).build().execute();
            AlbumSimplified albumSimplified = albumSimplifiedPaging.getItems()[0];
            if(albumSimplified != null) {
                Image image = albumSimplified.getImages()[0];
                if(image != null) {
                    return Optional.of(mapper.convert(image));
                }
                log.warn("Couldn't get an image for album: " + name + " because it doesn't have pictures");
            } else {
                log.warn("Couldn't get an image for album: " + name + " because it wasn't found");
            }
            return Optional.empty();

        } catch(IOException | ParseException | SpotifyWebApiException e) {
            log.warn("Couldn't get an image for album: " + name + ". The reason was: " + e.getMessage());
            return Optional.empty();
        }
    }

    private void setCredentialsRequest() {

        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();

        try {
            ClientCredentials clientCredentials = clientCredentialsRequest.execute();
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());

        } catch(IOException | ParseException | SpotifyWebApiException e) {
            log.error("Couldn't connect with the Spotify API. Message: " + e.getMessage());
            throw new ClientCredentialsException("unrecognized Spotify credentials", e);
        }
    }
}
