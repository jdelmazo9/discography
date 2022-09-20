package org.wyeworks.helpers;

import org.wyeworks.model.Album;


import java.util.*;

public class AlbumSplitHelper {

    public static Map<Integer, List<Album>> splitByDecade(List<Album> albums) {

        Map<Integer, List<Album>> splittedAlbumMap = new LinkedHashMap<>();

        albums.forEach(album -> {
            Integer albumDecade = calculateDecade(album.getYear());
            splittedAlbumMap.putIfAbsent(albumDecade, new ArrayList<>());
            splittedAlbumMap.get(albumDecade).add(album);
        });

        return splittedAlbumMap;
    }

    private static Integer calculateDecade(Integer year) {
        return year - year % 10;
    }
}