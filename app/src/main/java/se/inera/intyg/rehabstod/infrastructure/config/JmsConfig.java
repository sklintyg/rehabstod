/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.infrastructure.config;

import jakarta.jms.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import se.inera.intyg.rehabstod.config.properties.AppProperties;

/** Created by eriklupander on 2016-02-18. */
@Configuration
public class JmsConfig {

  private final ConnectionFactory connectionFactory;
  private final String loggingQueueName;

  public JmsConfig(ConnectionFactory connectionFactory, AppProperties appProperties) {
    this.connectionFactory = connectionFactory;
    this.loggingQueueName = appProperties.pdl().loggingQueueName();
  }

  @Bean
  public JmsTemplate jmsPDLLogTemplate() {
    JmsTemplate jmsTemplate = new JmsTemplate();
    jmsTemplate.setDefaultDestinationName(loggingQueueName);
    jmsTemplate.setConnectionFactory(connectionFactory);
    jmsTemplate.setSessionTransacted(true);
    return jmsTemplate;
  }
}
