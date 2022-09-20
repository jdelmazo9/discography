package org.wyeworks.helpers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.wyeworks.model.Album;

import java.util.List;
import java.util.Map;


public class AlbumSplitHelperTest {


    @Test
    public void whenSplitByDecadeWithEmptyList_ThenReturnEmptyMap() {
        assertEquals(Map.of(), AlbumSplitHelper.splitByDecade(List.of()));
    }

    @Test
    public void whenSplitByDecade_ThenReturnCorrectMap() {
        List<Album> albums = List.of(
                Album.builder().name("album1").year(1990).build(),
                Album.builder().name("album2").year(1970).build(),
                Album.builder().name("album2").year(1995).build(),
                Album.builder().name("album3").year(1977).build(),
                Album.builder().name("album4").year(1979).build(),
                Album.builder().name("album5").year(1985).build());

        Map<Integer, List<Album>> decadeAlbumsMap = AlbumSplitHelper.splitByDecade(albums);

        assertEquals(3, decadeAlbumsMap.get(1970).size());
        assertEquals(1, decadeAlbumsMap.get(1980).size());
        assertEquals(2, decadeAlbumsMap.get(1990).size());

        assertEquals(3, decadeAlbumsMap.keySet().size());
        assertTrue(decadeAlbumsMap.keySet().containsAll(List.of(1970, 1980, 1990)));
    }
}
