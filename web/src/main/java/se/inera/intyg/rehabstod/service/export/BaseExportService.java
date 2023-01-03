/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
import se.inera.intyg.rehabstod.common.util.StringUtil;
import se.inera.intyg.rehabstod.integration.srs.model.RiskSignal;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosKapitelService;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.LangdIntervall;

/**
 * Created by eriklupander on 2016-02-26.
 */
public abstract class BaseExportService {

    protected static final String FILTER_TITLE_VALDA_DIAGNOSER = "Valda diagnoser";
    protected static final String SELECTION_VALUE_ALLA = "Alla";
    protected static final String FILTER_TITLE_VALDA_LAKARE = "Valda läkare";
    protected static final String FILTER_TITLE_ARENDESTATUS = "Ärendestatus";
    protected static final String FILTER_TITLE_ARENDESTATUS_ALLA = "Visa alla";
    protected static final String FILTER_TITLE_ARENDESTATUS_UTAN = "Visa enbart sjukfall utan obesvarade ärenden";
    protected static final String FILTER_TITLE_ARENDESTATUS_MED = "Visa enbart sjukfall med obesvarade ärenden";
    protected static final String FILTER_TITLE_ARENDESTATUS_MED_KOMPLETTERING = "Visa sjukfall med obesvarade kompletteringar";
    protected static final String FILTER_TITLE_ARENDESTATUS_MED_FRAGOR = "Visa sjukfall med obesvarade administrativa frågor och svar";
    protected static final String FILTER_TITLE_VALD_SJUKSKRIVNINGSLANGD = "Sjukskrivningslängd";
    protected static final String FILTER_TITLE_VALD_ALDER = "Åldersspann";
    protected static final String FILTER_TITLE_VALD_SLUTDATUM = "Slutdatum";
    protected static final String FILTER_TITLE_FRITEXTFILTER = "Fritextfilter";
    protected static final String FILTER_TITLE_VISAPATIENTUPPGIFTER = "Visa personuppgifter";
    protected static final String VALDA_FILTER = "Valda filter";
    protected static final String H2_SJUKFALLSINSTALLNING = "Sjukfallsinställning";
    protected static final String MAXANTAL_DAGAR_UPPEHALL_MELLAN_INTYG = "Max dagar mellan intyg";
    protected static final String FILTER_TITLE_AVSLUTADE_SJUKFALL = "Visa avslutade sjukfall";
    protected static final String TEMPLATESTRING_GLAPPDAGAR = "%s dagar";
    protected static final String TEMPLATESTRING_DAGAR_AVSLUTADE = "%s dagar";
    protected static final String VALD_SORTERING_PA_TABELLEN = "Vald sortering";
    protected static final String SORTERING_KOLUMN = "Kolumn: ";
    protected static final String SORTERING_RIKTNING = "Riktning: ";
    protected static final String ANTAL_VISAR_ANTAL_PAGAENDE_SJUKFALL = "Antal pågående sjukfall";
    protected static final String ANTAL_EXPORTEN_VISAR = "Tabellen visar: ";
    protected static final String ANTAL_TOTALT_MINA = "Totalt: ";
    protected static final String ANTAL_TOTALT_PA_ENHETEN = "Totalt på enheten: ";

    protected static final String FORMAT_ANTAL_DAGAR = "%d dagar";
    protected static final String UNICODE_RIGHT_ARROW_SYMBOL = "\u2192";
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

    protected boolean notEmpty(PrintSjukfallRequest req) {
        return req.getFritext() != null && req.getFritext().trim().length() > 0;
    }

    protected boolean shouldShowSortering(PrintSjukfallRequest req, List<ExportField> displayedFields) {
        if (req.getSortering() == null || StringUtil.isNullOrEmpty(req.getSortering().getKolumn())) {
            return false;
        }
        final Optional<ExportField> sortColumn = ExportField.fromJsonId(req.getSortering().getKolumn());
        return sortColumn.isPresent() && displayedFields.contains(sortColumn.get());

    }

    public static String getQAFilterDisplayValue(Integer qaID) {
        String[] qaStatuses = { FILTER_TITLE_ARENDESTATUS_UTAN, FILTER_TITLE_ARENDESTATUS_MED,
            FILTER_TITLE_ARENDESTATUS_MED_KOMPLETTERING, FILTER_TITLE_ARENDESTATUS_MED_FRAGOR};
        if (qaID == null) {
            return FILTER_TITLE_ARENDESTATUS_ALLA;
        }
        for (int i = 0; i < qaStatuses.length; i++) {
            if (qaID == i) {
                return qaStatuses[i];
            }
        }
        return FILTER_TITLE_ARENDESTATUS_ALLA;
    }

    public static String getQAStatusFormat(int unansweredComplement, int unansweredOther) {
        if (unansweredComplement == 0 && unansweredOther == 0) {
            return "-";
        } else {
            String s = "";
            if (unansweredComplement != 0) {
                s = "Komplettering (" + unansweredComplement + ")\n";
            }
            if (unansweredOther != 0) {
                s = s + "Administrativ fråga (" + unansweredOther + ")";
            }
            return s;
        }
    }

    protected boolean isSrsFeatureActive(RehabstodUser user) {
        return Optional.ofNullable(user.getFeatures())
            .map(features -> features.get(AuthoritiesConstants.FEATURE_SRS))
            .map(Feature::getGlobal).orElse(false);
    }

    public static String getRiskKategoriDesc(RiskSignal risksignal) {
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
