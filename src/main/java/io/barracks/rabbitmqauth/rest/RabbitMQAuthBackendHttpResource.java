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

package io.barracks.rabbitmqauth.rest;

import io.barracks.rabbitmqauth.manager.RabbitMQAuthBackendHttpManager;
import io.barracks.rabbitmqauth.model.ResourceCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.StringUtils.collectionToDelimitedString;

@RestController
@RequestMapping(path = "/auth")
public class RabbitMQAuthBackendHttpResource {

    static final String ALLOW = "allow";

    static final String DENY = "deny";

    private final RabbitMQAuthBackendHttpManager rabbitMQAuthBackendHttpManager;

    @Autowired
    public RabbitMQAuthBackendHttpResource(RabbitMQAuthBackendHttpManager rabbitMQAuthBackendHttpManager) {
        this.rabbitMQAuthBackendHttpManager = rabbitMQAuthBackendHttpManager;
    }

    @RequestMapping("/user")
    public String user(@RequestParam("username") String username,
                       @RequestParam("password") String password) {
        return ALLOW;
    }

    @RequestMapping("/vhost")
    public String vhost() {
        return ALLOW;
    }

    @RequestMapping("/resource")
    public String resource(ResourceCheck check) {
        if (check.getVhost().equals("/") && (check.getResource().equals("topic") || check.getResource().equals("queue"))) {
            return rabbitMQAuthBackendHttpManager.checkResourceName(check.getName(), check.getResource()) ? ALLOW : DENY;
        }
        return ALLOW;
    }

    @RequestMapping("/topic")
    public String topic(String topic) {
        return ALLOW;
    }
}