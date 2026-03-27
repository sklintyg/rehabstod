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
package se.inera.intyg.rehabstod.infrastructure.integration.sparrtjanst.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.List;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.configuration.security.FiltersType;
import org.apache.cxf.transport.http.HTTPConduit;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import se.inera.intyg.rehabstod.infrastructure.config.properties.AppProperties;

@Component
@Profile("!rhs-sparrtjanst-stub")
public class SparrtjanstTlsConfig {

  private final Resource certFile;
  private final String certPassword;
  private final String certType;
  private final String keyManagerPassword;
  private final Resource truststoreFile;
  private final String truststorePassword;
  private final String truststoreType;

  public SparrtjanstTlsConfig(AppProperties appProperties) {
    final var loader = new DefaultResourceLoader();
    this.certFile = loader.getResource(appProperties.ntjp().certificate().file());
    this.certPassword = appProperties.ntjp().certificate().password();
    this.certType = appProperties.ntjp().certificate().type();
    this.keyManagerPassword = appProperties.ntjp().keyManagerPassword();
    this.truststoreFile = loader.getResource(appProperties.ntjp().truststore().file());
    this.truststorePassword = appProperties.ntjp().truststore().password();
    this.truststoreType = appProperties.ntjp().truststore().type();
  }

  public void configure(HTTPConduit conduit) {
    if (certFile == null || !certFile.exists()) {
      throw new IllegalArgumentException("cert file or cert password not provided");
    }
    try {
      TLSClientParameters tlsParams = new TLSClientParameters();
      tlsParams.setDisableCNCheck(true);

      KeyStore keyStore = KeyStore.getInstance(certType);
      try (FileInputStream fis = new FileInputStream(certFile.getFile())) {
        keyStore.load(fis, certPassword.toCharArray());
      }
      KeyManagerFactory kmf =
          KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(keyStore, keyManagerPassword.toCharArray());
      tlsParams.setKeyManagers(kmf.getKeyManagers());

      KeyStore trustStore = KeyStore.getInstance(truststoreType);
      try (FileInputStream fis = new FileInputStream(truststoreFile.getFile())) {
        trustStore.load(fis, truststorePassword.toCharArray());
      }
      TrustManagerFactory tmf =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(trustStore);
      tlsParams.setTrustManagers(tmf.getTrustManagers());

      final var cipherFilter = new FiltersType();
      cipherFilter
          .getInclude()
          .addAll(
              List.of(
                  ".*_EXPORT_.*",
                  ".*_EXPORT1024_.*",
                  ".*_WITH_DES_.*",
                  ".*_WITH_AES_.*",
                  ".*_WITH_NULL_.*"));
      cipherFilter.getExclude().add(".*_DH_anon_.*");
      tlsParams.setCipherSuitesFilter(cipherFilter);

      conduit.setTlsClientParameters(tlsParams);
    } catch (GeneralSecurityException | IOException e) {
      throw new IllegalStateException("Failed to configure TLS for sparrtjanst client", e);
    }
  }
}
