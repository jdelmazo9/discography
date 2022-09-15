package org.wyeworks.service;

import lombok.extern.slf4j.Slf4j;
import org.wyeworks.client.TrelloClient;
import org.wyeworks.dto.TrelloBoardDTO;
import org.wyeworks.dto.TrelloListDTO;
import org.wyeworks.exception.TrelloAPIException;
import org.wyeworks.helper.AlbumSplitHelper;
import org.wyeworks.model.Album;

import java.util.*;
import java.util.stream.IntStream;

@Slf4j
public class TrelloService {

    private final TrelloClient trelloClient;

    public TrelloService(TrelloClient trelloClient) {
        this.trelloClient = trelloClient;
    }

    /***
     * This method creates a board, the decade lists and the album cards.
     * The creation of these resources with parallelism can't be possible because the Trello API
     * does not support a high throughput.
     *
     * Marginal note: The trelloPosition must be greater than 0. That's why we use index + 1
     *
     * @param albums
     */
    public void postAlbums(List<Album> albums) {

        Map<Integer, List<Album>> albumsByDecade = AlbumSplitHelper.splitByDecade(albums);

        TrelloBoardDTO trelloBoard = trelloClient.createBoard("Discography-test");

        albumsByDecade.forEach((decade, orderedAlbums) -> {

            orderedAlbums.sort(Comparator.comparing(Album::getYear).thenComparing(Album::getName));

            String decadeName = "Hits of: " + decade + "s";
            try {
                TrelloListDTO trelloList = trelloClient.createList(trelloBoard.getId(), decadeName, decade);

                IntStream.range(0, orderedAlbums.size()).forEach(index -> {
                    Album album = orderedAlbums.get(index);
                    Integer trelloPosition = index + 1;
                    String customAlbumName = album.getYear() + " - " + album.getName();

                    trelloClient.createCard(trelloList.getId(), customAlbumName, trelloPosition, album.getPicture());
                });

            } catch (TrelloAPIException e) {
                log.warn("The List " + decadeName + " couldn't be created");
            }
        });
    }
}
