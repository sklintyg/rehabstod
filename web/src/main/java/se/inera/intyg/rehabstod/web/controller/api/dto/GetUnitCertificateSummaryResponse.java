package se.inera.intyg.rehabstod.web.controller.api.dto;


public class GetUnitCertificateSummaryResponse {
    private int total;
    private int men;
    private int women;


    public GetUnitCertificateSummaryResponse(int total, int men, int women) {
        this.total = total;
        this.men = men;
        this.women = women;
    }

    public int getTotal() {
        return total;
    }

    public int getMen() {
        return men;
    }

    public int getWomen() {
        return women;
    }


}
