/*
 * MIT License
 *
 * Copyright (c) 2017 Barracks Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.barracks.rabbitmqauth.client;

import io.barracks.commons.util.Endpoint;
import io.barracks.rabbitmqauth.client.entity.ApiKeyEntity;
import io.barracks.rabbitmqauth.client.exception.AuthorizationServiceClientException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@RestClientTest(AuthorizationServiceClient.class)
public class AuthorizationServiceClientTest {

    @Value("${io.barracks.authorizationservice.base_url}")
    private String baseUrl;
    @Autowired
    private AuthorizationServiceClient authorizationServiceClient;
    @Autowired
    private MockRestServiceServer mockServer;

    @Value("classpath:io/barracks/rabbitmqauth/client/response.json")
    private Resource response;

    @Test
    public void checkTopic_shouldReturnTrue_whenApiKeyExists() {
        // Given
        final Endpoint endpoint = AuthorizationServiceClient.CHECK_APIKEY_ENDPOINT;
        final String apiKey = "apiKey";
        final ApiKeyEntity apiKeyEntity = ApiKeyEntity.builder().apiKey(apiKey).build();

        mockServer.expect(method(endpoint.getMethod()))
                .andExpect(requestTo(endpoint.withBase(baseUrl).body(apiKeyEntity).getURI()))
                .andRespond(withStatus(HttpStatus.OK).body(response));

        // When
        final boolean result = authorizationServiceClient.apiKeyExists(apiKey);

        // Then
        mockServer.verify();
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void checkTopic_shouldReturnFalse_whenApiKeyDoesNot() {
        // Given
        final Endpoint endpoint = AuthorizationServiceClient.CHECK_APIKEY_ENDPOINT;
        final String apiKey = "apiKey";
        final ApiKeyEntity apiKeyEntity = ApiKeyEntity.builder().apiKey(apiKey).build();

        mockServer.expect(method(endpoint.getMethod()))
                .andExpect(requestTo(endpoint.withBase(baseUrl).body(apiKeyEntity).getURI()))
                .andRespond(withStatus(HttpStatus.FORBIDDEN));

        // When / Then
        assertThatExceptionOfType(AuthorizationServiceClientException.class)
                .isThrownBy(() -> authorizationServiceClient.apiKeyExists(apiKey));

        mockServer.verify();
    }
}
