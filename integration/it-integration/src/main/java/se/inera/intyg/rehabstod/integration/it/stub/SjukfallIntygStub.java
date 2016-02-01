package se.inera.intyg.rehabstod.integration.it.stub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitResponseType;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitType;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ResultCodeEnum;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.HsaId;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsLista;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Vardgivare;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eriklupander on 2016-01-29.
 */
@Service
@Profile({"dev", "rhs-hsa-stub"})
public class SjukfallIntygStub implements ListActiveSickLeavesForCareUnitResponderInterface {

    private List<IntygsData> intygsData = new ArrayList<>();

    @Autowired
    private SjukfallIntygDataGenerator sjukfallIntygDataGenerator;

    @Value("${rhs.sjukfall.stub.numberOfPatients}")
    private Integer numberOfPatients;

    @Value("${rhs.sjukfall.stub.intygPerPatient}")
    private Integer intygPerPatient;

    private Vardgivare fakedVardgivare;

    @PostConstruct
    public void init() {
        fakedVardgivare = initFakedVardgivare();
        intygsData = sjukfallIntygDataGenerator.generateIntygsData(numberOfPatients, intygPerPatient);
    }

    private Vardgivare initFakedVardgivare() {
        Vardgivare vg = new Vardgivare();
        HsaId hsaId = new HsaId();
        hsaId.setExtension("IFV1239877878-1041");
        vg.setVardgivareId(hsaId);
        vg.setVardgivarnamn("WebCert-Vårdgivare1");
        return vg;
    }


    @Override
    public ListActiveSickLeavesForCareUnitResponseType listActiveSickLeavesForCareUnit(String logicalAddress, ListActiveSickLeavesForCareUnitType parameters) {
        ListActiveSickLeavesForCareUnitResponseType resp = new ListActiveSickLeavesForCareUnitResponseType();
        resp.setResultCode(ResultCodeEnum.OK);

        IntygsLista intygsLista = new IntygsLista();
        intygsLista.getIntygsData().addAll(intygsData);
        resp.setIntygsLista(intygsLista);

        resp.setVardgivare(fakedVardgivare);
        return resp;
    }
}
