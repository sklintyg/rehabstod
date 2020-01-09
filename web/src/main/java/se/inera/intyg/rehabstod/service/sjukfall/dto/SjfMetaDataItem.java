/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.sjukfall.dto;

public class SjfMetaDataItem {

    private SjfMetaDataItemType itemType;

    private String itemId;
    private String itemName;

    private boolean includedInSjukfall;
    private boolean bidrarTillAktivtSjukfall;

    public SjfMetaDataItem(String itemId, String itemName, SjfMetaDataItemType itemType) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemType = itemType;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public SjfMetaDataItemType getItemType() {
        return itemType;
    }

    public boolean isIncludedInSjukfall() {
        return includedInSjukfall;
    }

    public void setIncludedInSjukfall(boolean includedInSjukfall) {
        this.includedInSjukfall = includedInSjukfall;
    }

    public boolean isBidrarTillAktivtSjukfall() {
        return bidrarTillAktivtSjukfall;
    }

    public void setBidrarTillAktivtSjukfall(boolean bidrarTillAktivtSjukfall) {
        this.bidrarTillAktivtSjukfall = bidrarTillAktivtSjukfall;
    }
}
