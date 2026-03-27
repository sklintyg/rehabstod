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
package se.inera.intyg.rehabstod.infrastructure.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record AppProperties(
    String domainName,
    @NotNull @Valid Security security,
    @NotNull @Valid Pdl pdl,
    @NotNull @Valid Saml saml,
    @NotNull @Valid Ntjp ntjp,
    @NotNull @Valid Cache cache,
    @NotNull @Valid Resources resources,
    @NotNull @Valid Integration integration) {

  public record Security(@NotBlank String hashSalt, @NotBlank String aesEncryptionKey) {}

  public record Pdl(
      @NotBlank String systemId, String systemName, @NotBlank String loggingQueueName) {}

  public record Saml(
      String baseUrl,
      @NotBlank String idpMetadataLocation,
      String loginSuccessUrl,
      boolean loginSuccessUrlAlwaysUse,
      String logoutSuccessUrl,
      @NotNull @Valid SamlSp sp,
      @NotNull @Valid Keystore keystore) {

    public record SamlSp(
        String entityId,
        String assertionConsumerServiceLocation,
        String singleLogoutServiceLocation,
        String singleLogoutServiceResponseLocation) {}

    public record Keystore(
        @NotBlank String type,
        @NotBlank String file,
        @NotBlank String alias,
        @NotBlank String password) {}
  }

  public record Ntjp(
      String baseUrl,
      @NotNull @Valid NtjpCertificate certificate,
      @NotBlank String keyManagerPassword,
      @NotNull @Valid NtjpTruststore truststore) {

    public record NtjpCertificate(String file, String type, @NotBlank String password) {}

    public record NtjpTruststore(String file, String type, @NotBlank String password) {}
  }

  public record Cache(
      long defaultExpirySeconds,
      long employeeNameExpirySeconds,
      long intygsadminExpirySeconds,
      long hsaEmployeeExpirySeconds,
      long hsaHealthCareUnitExpirySeconds,
      long hsaHealthCareUnitMembersExpirySeconds,
      long hsaUnitExpirySeconds,
      long hsaHealthCareProviderExpirySeconds) {}

  public record Resources(
      String authoritiesConfigurationFile,
      String dynamicLinksFile,
      String featuresConfigurationFile,
      String diagnoskapitelFile,
      String diagnosgrupperFile,
      String diagnoskodKsh97pFile,
      String diagnosisCodeIcd10seFile,
      int maxAliasesForCollections) {}

  public record Integration(
      @NotNull @Valid IntygstjanstIntegration intygstjanst,
      @NotNull @Valid WebcertIntegration webcert,
      @NotNull @Valid SrsIntegration srs,
      @NotNull @Valid SparrtjanstIntegration sparrtjanst,
      @NotNull @Valid SamtyckestjanstIntegration samtyckestjanst,
      @NotNull @Valid IntygsadminIntegration intygsadmin,
      @NotNull @Valid IntygProxyServiceIntegration intygProxyService) {

    public record IntygstjanstIntegration(
        String scheme,
        String baseUrl,
        int port,
        String hostUrl,
        String logicalAddress,
        String listSickLeavesForPersonUrl,
        @NotNull @Valid ServiceTimeouts service,
        @NotNull @Valid RestTimeouts rest) {

      public record ServiceTimeouts(int connectionTimeout, int receiveTimeout) {}

      public record RestTimeouts(int connectionRequestTimeout, int connectionTimeout) {}
    }

    public record WebcertIntegration(
        String scheme,
        String baseUrl,
        int port,
        String launchUrlTemplate,
        String sithsIdpUrl,
        int getAdditionsMaxAgeDays) {}

    public record SrsIntegration(
        String logicalAddress,
        String getRiskPredictionUrl,
        String getDiagnosisCodesUrl,
        int connectionTimeout,
        int receiveTimeout) {}

    public record SparrtjanstIntegration(
        String logicalAddress, String checkBlocksUrl, int connectionTimeout, int receiveTimeout) {}

    public record SamtyckestjanstIntegration(
        String logicalAddress,
        String checkConsentUrl,
        String registerExtendedConsentUrl,
        int connectionTimeout,
        int receiveTimeout) {}

    public record IntygsadminIntegration(String url, String cron, long cacheExpirySeconds) {}

    public record IntygProxyServiceIntegration(
        String baseUrl,
        String credentialInformationForPersonEndpoint,
        String employeeEndpoint,
        String healthCareUnitEndpoint,
        String healthCareUnitMembersEndpoint,
        String unitEndpoint,
        String personEndpoint,
        String personsEndpoint) {}
  }
}
