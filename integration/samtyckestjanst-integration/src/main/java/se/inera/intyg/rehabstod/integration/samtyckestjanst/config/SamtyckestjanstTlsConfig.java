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
package se.inera.intyg.rehabstod.integration.samtyckestjanst.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.transport.http.HTTPConduit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@Profile("!rhs-samtyckestjanst-stub")
public class SamtyckestjanstTlsConfig {

  @Value("${ntjp.ws.certificate.file:}")
  private Resource certFile;

  @Value("${ntjp.ws.certificate.password:}")
  private String certPassword;

  @Value("${ntjp.ws.certificate.type:JKS}")
  private String certType;

  @Value("${ntjp.ws.key.manager.password:}")
  private String keyManagerPassword;

  @Value("${ntjp.ws.truststore.file:}")
  private Resource truststoreFile;

  @Value("${ntjp.ws.truststore.password:}")
  private String truststorePassword;

  @Value("${ntjp.ws.truststore.type:JKS}")
  private String truststoreType;

  public void configure(HTTPConduit conduit) {
    if (certFile == null || !certFile.exists()) {
      return;
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

      conduit.setTlsClientParameters(tlsParams);
    } catch (GeneralSecurityException | IOException e) {
      throw new IllegalStateException("Failed to configure TLS for samtyckestjanst client", e);
    }
  }
}
