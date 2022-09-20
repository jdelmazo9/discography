package org.wyeworks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.wyeworks.model.Album;
import org.wyeworks.model.Picture;
import org.wyeworks.services.album.AlbumService;
import org.wyeworks.services.board.BoardService;
import org.wyeworks.services.picture.PictureService;

import java.util.List;
import java.util.Optional;

public class DiscographyApplicationTest {

    @Mock
    AlbumService albumServiceMock;
    @Mock
    PictureService pictureServiceMock;
    @Mock
    BoardService boardServiceMock;

    @InjectMocks
    DiscographyApplication application;

    List<Album> albums;

    List<Album> albumsWithPicture;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);

        albums = List.of(
                Album.builder().name("album").year(2015).build(),
                Album.builder().name("album2").year(1970).build()
        );

        albumsWithPicture = albums.stream().map(
                album -> album.toBuilder()
                        .picture(Optional.of(Picture.builder().build()))
                        .build()
        ).toList();

        when(albumServiceMock.getAllAlbums()).thenReturn(albums);
        when(pictureServiceMock.getAlbumsWithPictures(albums)).thenReturn(albumsWithPicture);
        doNothing().when(boardServiceMock).postAlbums(albumsWithPicture);
    }

    @Test
    public void whenStartProjectAndEverythingRunsOk_ThenDontThrowException() {
        assertDoesNotThrow(() -> application.startProcess());
    }

    @Test
    public void whenStartProjectAndAlbumServiceThrowsRuntimeException_ThenDontThrowException() {
        when(albumServiceMock.getAllAlbums()).thenThrow(new RuntimeException("I'm an exception"));
        assertDoesNotThrow(() -> application.startProcess());
    }

    @Test
    public void whenStartProjectAndPictureServiceThrowsRuntimeException_ThenDontThrowException() {
        when(pictureServiceMock.getAlbumsWithPictures(albums)).thenThrow(new RuntimeException("I'm an exception"));
        assertDoesNotThrow(() -> application.startProcess());
    }

    @Test
    public void whenStartProjectAndBoardServiceThrowsRuntimeException_ThenDontThrowException() {
        doThrow(new RuntimeException("I'm an exception")).when(boardServiceMock).postAlbums(albumsWithPicture);
        assertDoesNotThrow(() -> application.startProcess());
    }
}
