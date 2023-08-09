/*
 * Copyright 2016 OmniFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.security;

import jakarta.enterprise.inject.spi.CDI;

import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityNotification;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public final class AuthEventHandler implements HttpHandler {

	private final HttpHandler next;

	public AuthEventHandler(final HttpHandler next) {
		this.next = next;
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {

		exchange.getSecurityContext().registerNotificationReceiver(new SecurityNotificationReceiver());
		next.handleRequest(exchange);
	}

	private static class SecurityNotificationReceiver implements NotificationReceiver {

		@Override
		public void handleNotification(final SecurityNotification notification) {

            switch (notification.getEventType()) {
                case AUTHENTICATED:
					CDI.current().getBeanManager().getEvent().fire(new AuthenticatedEvent(notification, notification.getAccount().getPrincipal()));
                	break;
                case LOGGED_OUT:
					CDI.current().getBeanManager().getEvent().fire(new LoggedOutEvent(notification, notification.getAccount().getPrincipal()));
                	break;
                default:
                	break;
            }
		}
	}

}