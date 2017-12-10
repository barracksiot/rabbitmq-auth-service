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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.barracks.commons.util.Endpoint;
import io.barracks.rabbitmqauth.client.entity.ApiKeyEntity;
import io.barracks.rabbitmqauth.client.exception.AuthorizationServiceClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthorizationServiceClient extends HateoasRestClient {

    static final Endpoint CHECK_APIKEY_ENDPOINT = Endpoint.from(
            HttpMethod.POST,
            "/device/authenticate"
    );

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public AuthorizationServiceClient(
            ObjectMapper objectMapper,
            @Value("${io.barracks.authorizationservice.base_url}") String baseUrl,
            RestTemplateBuilder restTemplateBuilder
    ) {
        this.baseUrl = baseUrl;
        this.restTemplate = prepareRestTemplateBuilder(objectMapper, restTemplateBuilder).build();
    }

    public boolean apiKeyExists(String apiKey) {
        try {
            final ApiKeyEntity apiKeyEntity = ApiKeyEntity.builder().apiKey(apiKey).build();
            final ResponseEntity<Void> response = restTemplate.exchange(
                    CHECK_APIKEY_ENDPOINT.withBase(baseUrl).body(apiKeyEntity).getRequestEntity(),
                    Void.class
            );
            return response.getStatusCode().equals(HttpStatus.OK);
        } catch (HttpStatusCodeException e) {
            throw new AuthorizationServiceClientException(e);
        }
    }
}
