/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.export;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.infra.security.common.model.Feature;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.integration.srs.model.RiskSignal;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosKapitelService;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.LangdIntervall;

/**
 * Created by eriklupander on 2016-02-26.
 */
public abstract class BaseExportService {

  public static final String FILTER_TITLE_VALDA_DIAGNOSER = "Diagnoser";
  public static final String SELECTION_VALUE_ALLA = "Alla";
  public static final String FILTER_TITLE_VALDA_LAKARE = "Läkare";
  public static final String FILTER_TITLE_KOMPLETTERINGSSTATUS = "Kompletteringsstatus";
  public static final String FILTER_TITLE_VALD_SJUKSKRIVNINGSLANGD = "Sjukskrivningslängd";
  public static final String FILTER_TITLE_VALD_ALDER = "Åldersspann";
  public static final String FILTER_TITLE_VALD_SLUTDATUM = "Slutdatum";
  public static final String FILTER_TITLE_FRITEXTFILTER = "Fritextsök";
  public static final String FILTER_TITLE_VISAPATIENTUPPGIFTER = "Visa personuppgifter";
  public static final String MAXANTAL_DAGAR_UPPEHALL_MELLAN_INTYG = "Max dagar mellan intyg";
  public static final String UNICODE_RIGHT_ARROW_SYMBOL = "\u2192";
  protected static final String MINA_PAGAENDE_SJUKFALL = "Sjukfall";
  protected static final String PA_ENHETEN = "- Pågående sjukfall där jag utfärdat det nuvarande intyget";
  protected static final String ALLA_SJUKFALL = "Sjukfall";
  protected static final String SAMTLIGA_PAGAENDE_FALL_PA_ENHETEN = "Pågående sjukfall på enheten";
  protected static final String FILTER_TITLE_KOMPLETTERINGSSTATUS_ALLA = "Visa alla";
  protected static final String FILTER_TITLE_KOMPLETTERINGSSTATUS_UTAN = "Inga obesvarade kompletteringar";
  protected static final String FILTER_TITLE_KOMPLETTERINGSSTATUS_MED = "Med obesvarade kompletteringar";
  protected static final String VALDA_FILTER = "Valda filter";
  protected static final String H2_SJUKFALLSINSTALLNING = "Sjukfallsinställning";
  protected static final String VALD_SORTERING_PA_TABELLEN = "Vald sortering";
  protected static final String SORTERING_KOLUMN = "Kolumn: ";
  protected static final String SORTERING_RIKTNING = "Riktning: ";
  protected static final String SORTERING_INGEN = "Ingen";
  protected static final String ANTAL_VISAR_ANTAL_PAGAENDE_SJUKFALL = "Antal pågående sjukfall";
  protected static final String ANTAL_EXPORTEN_VISAR = "Tabellen visar: ";
  protected static final String ANTAL_TOTALT_MINA = "Totalt: ";
  protected static final String ANTAL_TOTALT_PA_ENHETEN = "Totalt på enheten: ";
  protected static final String TABLEHEADER_NR = "#";
  protected static final String TABLEHEADER_PERSONNUMMER = "Personnummer";
  protected static final String TABLEHEADER_ALDER = "Ålder";
  protected static final String TABLEHEADER_NAMN = "Namn";
  protected static final String TABLEHEADER_KON = "Kön";
  protected static final String TABLEHEADER_NUVARANDE_DIAGNOS = "Diagnos/diagnoser";
  protected static final String TABLEHEADER_STARTDATUM = "Startdatum";
  protected static final String TABLEHEADER_SLUTDATUM = "Slutdatum";
  protected static final String TABLEHEADER_SJUKSKRIVNINGSLANGD = "Längd";
  protected static final String TABLEHEADER_ANTAL = "Antal";
  protected static final String TABLEHEADER_SJUKSKRIVNINGSGRAD = "Grad";
  protected static final String TABLEHEADER_KOMPLETTERINGSSTATUS = "Kompletteringar";
  protected static final String TABLEHEADER_NUVARANDE_LAKARE = "Läkare";
  protected static final String TABLEHEADER_SRS_RISK = "Risk";
  public static final String FORMAT_ANTAL_DAGAR = "%d dagar";
  private static final int SRS_RISK_LOW = 1;
  private static final String SRS_RISK_LOW_DESC = "Måttlig";
  private static final int SRS_RISK_MED = 2;
  private static final String SRS_RISK_MED_DESC = "Hög";
  private static final int SRS_RISK_HIGH = 3;
  private static final String SRS_RISK_HIGH_DESC = "Mycket hög";

  @Autowired
  protected DiagnosKapitelService diagnosKapitelService;

  public static String diagnoseListToString(List<Diagnos> biDiagnoser) {
    if (biDiagnoser != null && !biDiagnoser.isEmpty()) {
      return biDiagnoser.stream()
          .map(Diagnos::getIntygsVarde)
          .collect(Collectors.joining(", ", ", ", ""));
    } else {
      return "";
    }

  }

  public static String getFilterDate(LangdIntervall dateIntervall) {
    String max = dateIntervall.getMax();
    String min = dateIntervall.getMin();

    if (max != null && !max.isEmpty() && min != null && !min.isEmpty()) {
      if (max.equals(min)) {
        return max;
      } else {
        return min + " - " + max;
      }
    }

    return "-";
  }

  public static String getKompletteringFilterDisplayValue(Integer komplettering) {
    if (komplettering == null) {
      return FILTER_TITLE_KOMPLETTERINGSSTATUS_ALLA;
    } else {
      return komplettering == 0 ? FILTER_TITLE_KOMPLETTERINGSSTATUS_UTAN : FILTER_TITLE_KOMPLETTERINGSSTATUS_MED;
    }
  }

  protected boolean notEmpty(PrintSjukfallRequest req) {
    return req.getFritext() != null && req.getFritext().trim().length() > 0;
  }

  protected String getKompletteringStatusFormat(int obesvaradeKompl) {
    if (obesvaradeKompl == 0) {
      return "-";
    } else {
      return "Obesvarade(" + obesvaradeKompl + ")";
    }
  }

  protected boolean isSrsFeatureActive(RehabstodUser user) {
    return Optional.ofNullable(user.getFeatures())
        .map(features -> features.get(AuthoritiesConstants.FEATURE_SRS))
        .map(Feature::getGlobal).orElse(false);
  }

  protected String getRiskKategoriDesc(RiskSignal risksignal) {
    if (risksignal != null) {
      switch (risksignal.getRiskKategori()) {
        case SRS_RISK_LOW:
          return SRS_RISK_LOW_DESC;
        case SRS_RISK_MED:
          return SRS_RISK_MED_DESC;
        case SRS_RISK_HIGH:
          return SRS_RISK_HIGH_DESC;
      }

    }
    return "Ej beräknad";
  }

}
