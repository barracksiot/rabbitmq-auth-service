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

package io.barracks.rabbitmqauth.manager;

import io.barracks.rabbitmqauth.client.AuthorizationServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQAuthBackendHttpManager {

    private final AuthorizationServiceClient authorizationServiceClient;

    private static final String MQTT_TOPIC_PREFIX = "mqtt-subscription-";

    @Autowired
    public RabbitMQAuthBackendHttpManager(AuthorizationServiceClient authorizationServiceClient) {
        this.authorizationServiceClient = authorizationServiceClient;
    }

    public boolean checkResourceName(String name, String resource) {
        String apiKey = null;
        if (resource.equals("topic")) {
            apiKey = name.split("/")[0];
        } else if (resource.equals("queue")) {
            final String mqttTopic = name.replace(MQTT_TOPIC_PREFIX, "");
            apiKey = mqttTopic.split("\\.")[0];
        }
        return authorizationServiceClient.apiKeyExists(apiKey);
    }
}
