package org.wyeworks.services.board;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wyeworks.clients.TrelloClient;
import org.wyeworks.dtos.TrelloBoardDTO;
import org.wyeworks.dtos.TrelloCardDTO;
import org.wyeworks.dtos.TrelloListDTO;
import org.wyeworks.exceptions.TrelloAPIException;
import org.wyeworks.helpers.AlbumSplitHelper;
import org.wyeworks.model.Album;

import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Component
public class TrelloBoardService implements BoardService {

    private final TrelloClient trelloClient;

    public TrelloBoardService(@Autowired TrelloClient trelloClient) {
        this.trelloClient = trelloClient;
    }

    /**
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

        TrelloBoardDTO trelloBoard = trelloClient.createBoard();
        log.info("The board " + trelloBoard.getName() + " has been created");

        albumsByDecade.forEach((decade, orderedAlbums) -> {

            orderedAlbums.sort(Comparator.comparing(Album::getYear).thenComparing(Album::getName));

            String decadeName = "Hits of: " + decade + "s";
            try {
                TrelloListDTO trelloList = trelloClient.createList(trelloBoard.getId(), decadeName, decade);
                log.info("The list " + trelloList.getName() + " has been created");

                IntStream.range(0, orderedAlbums.size()).forEach(index -> {
                    Album album = orderedAlbums.get(index);
                    Integer trelloPosition = index + 1;
                    String customAlbumName = album.getYear() + " - " + album.getName();

                    try {
                        TrelloCardDTO trelloCard = trelloClient.createCard(trelloList.getId(), customAlbumName, trelloPosition, album.getPicture());
                        log.info("The card " + trelloCard.getName() + " has been created");
                    } catch (TrelloAPIException e) {
                        log.warn(e.getMessage());
                    }
                });

            } catch (TrelloAPIException e) {
                log.warn(e.getMessage());
            }
        });
    }
}
