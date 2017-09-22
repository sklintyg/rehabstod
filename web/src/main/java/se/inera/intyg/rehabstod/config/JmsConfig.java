/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.TransactionAwareConnectionFactoryProxy;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

/**
 * Created by eriklupander on 2016-02-18.
 */
@Configuration
public class JmsConfig {

    @Bean
    public JndiObjectFactoryBean queue() {
        JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
        jndiObjectFactoryBean.setJndiName("java:comp/env/jms/Queue");
        return jndiObjectFactoryBean;
    }

    @Bean
    public JndiObjectFactoryBean aggregatedQueue() {
        JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
        jndiObjectFactoryBean.setJndiName("java:comp/env/jms/AggregatedLogSenderQueue");
        return jndiObjectFactoryBean;
    }

    @Bean
    public JndiObjectFactoryBean jmsFactory() {
        JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
        jndiObjectFactoryBean.setJndiName("java:comp/env/jms/ConnectionFactory");
        return jndiObjectFactoryBean;
    }

    @Bean
    public JmsTemplate jmsPDLLogTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination((Destination) queue().getObject());
        jmsTemplate.setConnectionFactory(connectionFactory());
        return jmsTemplate;
    }

    @Bean
    public JmsTemplate jmsAggregatedPDLLogTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination((Destination) aggregatedQueue().getObject());
        jmsTemplate.setConnectionFactory(connectionFactory());
        return jmsTemplate;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        TransactionAwareConnectionFactoryProxy activeMQConnectionFactory = new TransactionAwareConnectionFactoryProxy();
        activeMQConnectionFactory.setTargetConnectionFactory((ConnectionFactory) jmsFactory().getObject());
        activeMQConnectionFactory.setSynchedLocalTransactionAllowed(true);
        return activeMQConnectionFactory;
    }

}
