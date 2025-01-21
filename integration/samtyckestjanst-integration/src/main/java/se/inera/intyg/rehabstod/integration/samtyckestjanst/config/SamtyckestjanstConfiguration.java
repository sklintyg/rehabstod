/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.samtyckestjanst.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import se.inera.intyg.rehabstod.integration.samtyckestjanst.stub.SamtyckestjanstStubConfiguration;

/**
 * Java config entry for Samtyckestjanst
 * Created by Magnus Ekstrand on 2018-10-16.
 */
@Configuration
@ComponentScan({
    "se.inera.intyg.rehabstod.integration.samtyckestjanst.client",
    "se.inera.intyg.rehabstod.integration.samtyckestjanst.service"
})
@ImportResource("classpath:samtyckestjanst-services-config.xml")
@Import(SamtyckestjanstStubConfiguration.class)
public class SamtyckestjanstConfiguration {

    public SamtyckestjanstConfiguration() { // NOSONAR
    }

}
