package org.wyeworks.client;

import com.google.common.collect.Maps;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.wyeworks.dto.TrelloBoardDTO;
import org.wyeworks.dto.TrelloCardDTO;
import org.wyeworks.dto.TrelloListDTO;
import org.wyeworks.exception.TrelloAPIException;
import org.wyeworks.model.Picture;

import java.util.Map;
import java.util.Optional;

@Slf4j
public class TrelloClient {

    private final String apiKey;
    private final String apiToken;
    private final String host;

    public TrelloClient(String apiKey, String apiToken, String host) {
        this.apiKey = apiKey;
        this.apiToken = apiToken;
        this.host = host;
    }

    public TrelloBoardDTO createBoard(String name) {
        return Unirest.post(host + "/1/boards")
                .queryString("key", apiKey)
                .queryString("token", apiToken)
                .queryString("name", name)
                .queryString("defaultLists", false)
                .queryString("prefs_permissionLevel", "public")
                .asObject(TrelloBoardDTO.class)
                .ifFailure(response -> {
                    String message = this.standardErrorMessage("board", name, response.getStatus(), response.getStatusText());
                    log.error(message);
                    throw new TrelloAPIException(message);
                })
                .getBody();
    }

    public TrelloListDTO createList(String boardId, String name, Integer position) {
        return Unirest.post(host + "/1/lists")
                .queryString("key", apiKey)
                .queryString("token", apiToken)
                .queryString("idBoard", boardId)
                .queryString("name", name)
                .queryString("pos", position)
                .asObject(TrelloListDTO.class)
                .ifFailure(response -> {
                    String message = this.standardErrorMessage("list", name, response.getStatus(), response.getStatusText());
                    log.warn(message);
                    throw new TrelloAPIException(message);
                })
                .getBody();
    }

    public void createCard(String listId, String name, Integer position, Optional<Picture> picture) {

        Map<String, Object> queryStringMap = Maps.newHashMap();
        queryStringMap.put("Accept", "application/json");
        queryStringMap.put("key", apiKey);
        queryStringMap.put("token", apiToken);
        queryStringMap.put("idList", listId);
        queryStringMap.put("name", name);
        queryStringMap.put("pos", position);
        picture.ifPresent(pic -> queryStringMap.put("urlSource", pic.getUrl()));

        Unirest.post(host + "/1/cards")
                .header("Accept", "application/json")
                .queryString(queryStringMap)
                .asObject(TrelloCardDTO.class)
                .ifFailure(response ->
                    log.warn(this.standardErrorMessage("card", name, response.getStatus(), response.getStatusText())))
                .ifSuccess(trelloCard -> log.info(trelloCard.getBody().toString()));
    }

    private String standardErrorMessage(String resource, String name, Integer status, String statusText) {
        return "The " + resource + " post for: " + name + " request failed with status: \"" + status + " and message: \"" + statusText + "\"";
    }
}
