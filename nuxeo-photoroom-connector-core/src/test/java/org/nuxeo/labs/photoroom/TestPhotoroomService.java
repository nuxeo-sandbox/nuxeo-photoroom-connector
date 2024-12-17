package org.nuxeo.labs.photoroom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.labs.photoroom.client.ApiClient;
import org.nuxeo.labs.photoroom.client.Configuration;
import org.nuxeo.labs.photoroom.client.ServerConfiguration;
import org.nuxeo.labs.photoroom.service.PhotoroomService;
import org.nuxeo.labs.photoroom.service.model.ResultImage;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import jakarta.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.nuxeo.labs.photoroom.service.PhotoroomServiceImpl.*;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class })
@Deploy("nuxeo-photoroom-connector-core")
public class TestPhotoroomService {

    public final static String DUMMY_API_KEY = "mykey";

    @Inject
    protected PhotoroomService photoroomservice;

    protected MockWebServer mockWebServer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        Framework.getProperties().setProperty(API_KEY_PROPERTY, DUMMY_API_KEY);
        List< ServerConfiguration > servers = List.of(new ServerConfiguration(mockWebServer.url("/").toString(),TEST_SERVER_CONFIG,null));
        Configuration.setDefaultApiClient(new ApiClient().setServers(servers));
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void testServiceIsDeployed() {
        assertNotNull(photoroomservice);
    }

    @Test
    public void testRemoveBackground() throws IOException {
        final String dummyResult = "dummy";

        mockWebServer.setDispatcher(new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch (@NotNull RecordedRequest request) {
                if (request.getPath().startsWith("//v1/segment")) {
                    Assert.assertEquals(DUMMY_API_KEY, request.getHeader(API_KEY_HEADER));
                    try {
                        return new MockResponse().setResponseCode(200).setBody(
                                objectMapper.writeValueAsString(new ResultImage(
                                        Base64.getEncoder().encodeToString(
                                                dummyResult.getBytes(StandardCharsets.UTF_8)))));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    fail();
                    return null;
                }
            }
        });

        Blob input = new FileBlob(new File(getClass().getResource("/files/Kumamon.jpeg").getPath()), "image/jpeg");

        Blob result = photoroomservice.removeImageBackground(input);

        Assert.assertNotNull(result);
        Assert.assertEquals("image/png", result.getMimeType());
        Assert.assertEquals(dummyResult, result.getString());
    }

}
