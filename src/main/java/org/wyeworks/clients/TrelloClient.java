package org.wyeworks.clients;

import com.google.common.collect.Maps;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.wyeworks.dtos.TrelloBoardDTO;
import org.wyeworks.dtos.TrelloCardDTO;
import org.wyeworks.dtos.TrelloListDTO;
import org.wyeworks.exceptions.TrelloAPIException;
import org.wyeworks.exceptions.TrelloAPIThroughputException;
import org.wyeworks.model.Picture;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class TrelloClient {

    @Value("${api.trello.apikey}")
    private String apiKey;
    @Value("${api.trello.apitoken}")
    private String apiToken;
    @Value("${api.trello.host}")
    private String host;
    @Value("${api.trello.organizationid}")
    private String organizationId;
    @Value("${api.trello.boardname}")
    private String boardName;
    @Value("${api.trello.permissionlevel}")
    private String permissionLevel;


    public TrelloBoardDTO createBoard() {

        Map<String, Object> queryStringMap = Maps.newHashMap();
        queryStringMap.put("key", apiKey);
        queryStringMap.put("token", apiToken);
        queryStringMap.put("name", boardName);
        queryStringMap.put("prefs_permissionLevel", permissionLevel);
        queryStringMap.put("defaultLists", false);
        if(organizationId != null) {
            queryStringMap.put("idOrganization", organizationId);
        }

        return Unirest.post(host + "/1/boards")
                .queryString(queryStringMap)
                .asObject(TrelloBoardDTO.class)
                .ifFailure(response -> {
                    throw new TrelloAPIException("board", boardName, response.getStatus(), response.getStatusText());
                })
                .getBody();
    }


    @Retryable(value = TrelloAPIThroughputException.class, maxAttempts = 5, backoff = @Backoff(delay = 2000, multiplier = 2))
    public TrelloListDTO createList(String boardId, String name, Integer position) {
        return Unirest.post(host + "/1/lists")
                .queryString("key", apiKey)
                .queryString("token", apiToken)
                .queryString("idBoard", boardId)
                .queryString("name", name)
                .queryString("pos", position)
                .asObject(TrelloListDTO.class)
                .ifFailure(response -> {
                    if(response.getStatus() == 429) {
                        throw new TrelloAPIThroughputException("list", name, response.getStatus(), response.getStatusText());
                    }
                    throw new TrelloAPIException("list", name, response.getStatus(), response.getStatusText());
                })
                .getBody();
    }

    @Retryable(value = TrelloAPIThroughputException.class, maxAttempts = 5, backoff = @Backoff(delay = 2000, multiplier = 2))
    public TrelloCardDTO createCard(String listId, String name, Integer position, Optional<Picture> picture) {

        Map<String, Object> queryStringMap = Maps.newHashMap();
        queryStringMap.put("Accept", "application/json");
        queryStringMap.put("key", apiKey);
        queryStringMap.put("token", apiToken);
        queryStringMap.put("idList", listId);
        queryStringMap.put("name", name);
        queryStringMap.put("pos", position);
        picture.ifPresent(pic -> queryStringMap.put("urlSource", pic.getUrl()));

        return Unirest.post(host + "/1/cards")
                .header("Accept", "application/json")
                .queryString(queryStringMap)
                .asObject(TrelloCardDTO.class)
                .ifFailure(response -> {
                    if(response.getStatus() == 429) {
                        throw new TrelloAPIThroughputException("card", name, response.getStatus(), response.getStatusText());
                    }
                    throw new TrelloAPIException("card", name, response.getStatus(), response.getStatusText());
                })
                .getBody();
    }

    private String standardErrorMessage(String resource, String name, Integer status, String statusText) {
        return "The " + resource + " post for: " + name + " request failed with status: \"" + status + " and message: \"" + statusText + "\"";
    }
}
