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
package se.inera.intyg.rehabstod.service.export.pdf;

final class PdfConstants {

  static final String FILTER_TITLE_MAXANTAL_DAGAR_UPPEHALL_MELLAN_INTYG = "Max dagar mellan intyg";
  static final String FILTER_TITLE_AVSLUTADE_SJUKFALL = "Visa avslutade sjukfall";
  static final String FILTER_TITLE_KOMPLETTERINGSSTATUS = "Kompletteringsstatus";
  static final String FILTER_TITLE_KOMPLETTERINGSSTATUS_ALLA = "Visa alla";
  static final String FILTER_TITLE_KOMPLETTERINGSSTATUS_UTAN = "Visa sjukfall utan obesvarade kompletteringar";
  static final String FILTER_TITLE_KOMPLETTERINGSSTATUS_MED = "Visa sjukfall med obesvarade kompletteringar";
  static final String FILTER_TITLE_SJUKSKRIVNINGSLANGD = "Sjukskrivningslängd";
  static final String FILTER_TITLE_ALDERSPANN = "Åldersspann";
  static final String FILTER_TITLE_SLUTDATUM = "Slutdatum";
  static final String FILTER_TITLE_PATIENTUPPGIFTER = "Personuppgifter";
  static final String FILTER_TITLE_FRITEXT = "Fritextsök";
  static final String FILTER_SELECTION_VALUE_ALLA = "Alla";
  static final String FILTER_TITLE_LAKARE = "Läkare";
  static final String FILTER_TITLE_DIAGNOSER = "Diagnos(er)";
  static final String FORMAT_ANTAL_DAGAR = "%d dagar";
  static final String UNICODE_RIGHT_ARROW_SYMBOL = "\u2192";

  static final String TABLE_TITLE_PREFIX = "Sjukfall";
  static final String TABLE_TITLE_MINA_SJUKFALL = " - Pågående sjukfall där jag utfärdat det nuvarande intyget";
  static final String TABLE_TITLE_PA_ENHETEN = " - Pågående sjukfall på enheten";
  private PdfConstants() {
  }

}