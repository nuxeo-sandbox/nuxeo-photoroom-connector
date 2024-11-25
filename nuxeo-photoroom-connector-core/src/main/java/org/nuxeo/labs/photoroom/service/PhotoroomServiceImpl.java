package org.nuxeo.labs.photoroom.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.ByteArrayBlob;
import org.nuxeo.labs.photoroom.client.ApiException;
import org.nuxeo.labs.photoroom.client.ServerConfiguration;
import org.nuxeo.labs.photoroom.client.api.DefaultApi;
import org.nuxeo.labs.photoroom.client.auth.ApiKeyAuth;
import org.nuxeo.labs.photoroom.service.model.ResultImage;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.DefaultComponent;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

public class PhotoroomServiceImpl extends DefaultComponent implements PhotoroomService {

    public static final String API_KEY_PROPERTY = "photoroom.apikey";
    public static final String API_KEY_HEADER ="x-api-key";

    public static final String TEST_SERVER_CONFIG ="test";

    protected static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * volatile on purpose to allow for the double-checked locking idiom
     */
    protected volatile DefaultApi client;


    @Override
    public Blob removeImageBackground(Blob source) {
        DefaultApi client = getClient();
        try {
            File response = client.removeBackground(
                    source.getCloseableFile().file, "png", "rgba", null,
                    "full", "false", "false"
            );

            // Auto generated client always defaults to json payload for the result
            ResultImage resultObj = objectMapper.readValue(response, ResultImage.class);
            byte[] bytes = Base64.getDecoder().decode(resultObj.getResult_b64());
            Blob noBgBlob =  new ByteArrayBlob(bytes,"image/png");

            noBgBlob.setFilename(source.getFilename());
            return noBgBlob;
        } catch (ApiException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getApiKey() {
        return Framework.getProperty(API_KEY_PROPERTY);
    }

    protected DefaultApi getClient() {
        DefaultApi result = client;
        if (result == null) {
            synchronized (this) {
                result = client;
                if (result == null) {
                    result = client = new DefaultApi();
                    ((ApiKeyAuth)client.getApiClient().getAuthentication(API_KEY_HEADER)).setApiKey(getApiKey());

                    // for unit tests
                    Optional<ServerConfiguration> testServer = client.getApiClient().getServers().stream()
                            .filter(server -> TEST_SERVER_CONFIG.equals(server.description)).findFirst();
                    testServer.ifPresent(serverConfiguration -> client.setCustomBaseUrl(serverConfiguration.URL));
                }
            }
        }
        return result;
    }


}
