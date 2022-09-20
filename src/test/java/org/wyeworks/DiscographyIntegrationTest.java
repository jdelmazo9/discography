package org.wyeworks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.wyeworks.clients.SpotifyClient;
import org.wyeworks.model.Picture;

import java.util.Map;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class DiscographyIntegrationTest {

    @MockBean
    SpotifyClient spotifyClientMock;

    @Autowired
    DiscographyApplication discographyApplication;

    WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(9290).bindAddress("localhost")
                .extensions(new ResponseTemplateTransformer.Builder().global(false).build()));


    @BeforeEach
    public void setup() {
        Mockito.reset(spotifyClientMock);

        when(spotifyClientMock.getAlbumPicture(any()))
                .thenReturn(Optional.of(Picture.builder().url("http://testurl.net").build()));

        Map<String, StringValuePattern> boardParams =
                Map.of( "key",equalTo("12345"),
                        "token",equalTo("67891"),
                        "defaultLists", equalTo("false"),
                        "prefs_permissionLevel", equalTo("public"));

        Map<String, StringValuePattern> listParams =
                Map.of( "key",equalTo("12345"),
                        "token",equalTo("67891")
                );

        Map<String, StringValuePattern> cardParams =
                Map.of( "key",equalTo("12345"),
                        "token",equalTo("67891")
                );

        wireMockServer.stubFor(WireMock.post(WireMock.urlMatching("/1/boards?(.*)")).withQueryParams(boardParams)
                .willReturn(aResponse().withStatus(200).withHeader("Accept","application/json")
                        .withTransformers("response-template")
                        .withBodyFile("trello/responses/boardSuccessResponse.json")
                        .withHeader(HttpHeaders.CONNECTION, "close")
                ));

        wireMockServer.stubFor(WireMock.post(WireMock.urlMatching("/1/lists?(.*)")).withQueryParams(listParams).atPriority(2)
                .willReturn(aResponse().withStatus(200).withHeader("Accept","application/json")
                        .withTransformers("response-template")
                        .withBodyFile("trello/responses/listSuccessResponse.json")
                        .withHeader(HttpHeaders.CONNECTION, "close")
                ));

        wireMockServer.stubFor(WireMock.post(WireMock.urlMatching("/1/cards?(.*)")).withQueryParams(cardParams).atPriority(2)
                .willReturn(aResponse().withStatus(200).withHeader("Accept","application/json")
                        .withTransformers("response-template")
                        .withBodyFile("trello/responses/cardSuccessResponse.json")
                        .withHeader(HttpHeaders.CONNECTION, "close")
                ));

        wireMockServer.start();
    }

    @AfterEach
    public void afterEachSetup() {
        wireMockServer.stop();
    }

    @Test
    public void whenStartProcessAndEverythingRunsOk_ThenDontThrowException() {

        //Asserts that the application does not fail
        assertDoesNotThrow(() -> discographyApplication.startProcess());

        //Asserts that we call the Spotify Client 47 times to get album images (because we have 47 albums in our file)
        Mockito.verify(spotifyClientMock, times(47)).getAlbumPicture(any());

    }

    @Test
    public void whenStartProcessAndSpotifyPictureClientReturnsEmptyWhenFails_ThenTheProcessContinuesWorking() {

        when(spotifyClientMock.getAlbumPicture(any()))
                .thenReturn(Optional.empty());

        //Asserts that the application does not fail
        assertDoesNotThrow(() -> discographyApplication.startProcess());

        //Asserts that we continue posting cards even if spotify service fails
        wireMockServer.verify(exactly(47), postRequestedFor(urlMatching("/1/cards?(.*)")));
    }

    @Test
    public void whenStartProcessAndBoardCreationFails_ThenTheProcessFinish() {

        wireMockServer.stubFor(WireMock.post(WireMock.urlMatching("/1/boards?(.*)"))
                .willReturn(aResponse().withStatus(500).withStatusMessage("CAN NOT CREATE BOARD").withHeader(HttpHeaders.CONNECTION, "close")));

        //Asserts that the application does not fail
        assertDoesNotThrow(() -> discographyApplication.startProcess());

        //Asserts that we finish the process
        wireMockServer.verify(exactly(1), postRequestedFor(urlMatching("/1/boards?(.*)")));
        wireMockServer.verify(exactly(0), postRequestedFor(urlMatching("/1/lists?(.*)")));
        wireMockServer.verify(exactly(0), postRequestedFor(urlMatching("/1/cards?(.*)")));
    }

    @Test
    public void whenStartProcessAndOneListCreationFails_ThenTheProcessDontCreateThatSongsButStillWorking() {

        wireMockServer.stubFor(WireMock.post(WireMock.urlMatching("/1/lists?(.*)"))
                        .withQueryParams(Map.of("name", equalTo("Hits of: 1980s"))).atPriority(1)
                .willReturn(aResponse().withStatus(500).withStatusMessage("CAN NOT CREATE 1980 LIST").withHeader(HttpHeaders.CONNECTION, "close")));

        //Asserts that the application does not fail
        assertDoesNotThrow(() -> discographyApplication.startProcess());

        //Asserts that the 1980 songs are not created
        wireMockServer.verify(exactly(38), postRequestedFor(urlMatching("/1/cards?(.*)")));
    }

    @Test
    public void whenStartProcessAndOneCardCreationFails_ThenTheProcessDontCreateThatCardButStillWorking() {

        wireMockServer.stubFor(WireMock.post(WireMock.urlMatching("/1/cards?(.*)"))
                .withQueryParams(Map.of("name", equalTo("1967 - John Wesley Harding"))).atPriority(1)
                .willReturn(aResponse().withStatus(500).withStatusMessage("CAN NOT CREATE JOHN WESLEY CARD").withHeader(HttpHeaders.CONNECTION, "close")));

        //Asserts that the application does not fail
        assertDoesNotThrow(() -> discographyApplication.startProcess());

        //Asserts that if one card fails, still continue creating the others
        wireMockServer.verify(exactly(47), postRequestedFor(urlMatching("/1/cards?(.*)")));
    }
}
