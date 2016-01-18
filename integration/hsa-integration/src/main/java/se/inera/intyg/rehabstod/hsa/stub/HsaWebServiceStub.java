package se.inera.intyg.rehabstod.hsa.stub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3.wsaddressing10.AttributedURIType;

import se.inera.ifv.hsaws.v3.HsaWsFault;
import se.inera.ifv.hsaws.v3.HsaWsResponderInterface;
import se.inera.ifv.hsawsresponder.v3.*;

public class HsaWebServiceStub implements HsaWsResponderInterface {

    private static final Logger LOG = LoggerFactory.getLogger(HsaWebServiceStub.class);

    @Autowired
    private HsaServiceStub hsaServiceStub;

    @Override
    public VpwGetPublicUnitsResponseType vpwGetPublicUnits(AttributedURIType logicalAddress, AttributedURIType id, VpwGetPublicUnitsType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public GetCareUnitResponseType getCareUnit(AttributedURIType logicalAddress, AttributedURIType id, LookupHsaObjectType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public GetStatisticsPersonResponseType getStatisticsPerson(AttributedURIType logicalAddress, AttributedURIType id, GetStatisticsPersonType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public IsAuthorizedToSystemResponseType isAuthorizedToSystem(AttributedURIType logicalAddress, AttributedURIType id, IsAuthorizedToSystemType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public GetCareUnitListResponseType getCareUnitList(AttributedURIType logicalAddress, AttributedURIType id, LookupHsaObjectType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public GetHospLastUpdateResponseType getHospLastUpdate(AttributedURIType logicalAddress, AttributedURIType id, GetHospLastUpdateType parameters) throws HsaWsFault {
        GetHospLastUpdateResponseType response = new GetHospLastUpdateResponseType();
        response.setLastUpdate(hsaServiceStub.getHospLastUpdate());
        return response;
    }

    @Override
    public GetHsaUnitResponseType getHsaUnit(AttributedURIType logicalAddress, AttributedURIType id, LookupHsaObjectType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public GetPriceUnitsForAuthResponseType getPriceUnitsForAuth(AttributedURIType logicalAddress, AttributedURIType id, GetPriceUnitsForAuthType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public GetHsaPersonResponseType getHsaPerson(AttributedURIType logicalAddress, AttributedURIType id, GetHsaPersonType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public GetStatisticsNamesResponseType getStatisticsNames(AttributedURIType logicalAddress, AttributedURIType id, GetStatisticsNamesType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public PingResponseType ping(AttributedURIType logicalAddress, AttributedURIType id, PingType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public GetMiuForPersonResponseType getMiuForPerson(AttributedURIType logicalAddress, AttributedURIType id, GetMiuForPersonType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public GetStatisticsCareGiverResponseType getStatisticsCareGiver(AttributedURIType logicalAddress, AttributedURIType id, GetStatisticsCareGiverType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public HsawsSimpleLookupResponseType hsawsSimpleLookup(AttributedURIType logicalAddress, AttributedURIType id, HsawsSimpleLookupType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public GetStatisticsHsaUnitResponseType getStatisticsHsaUnit(AttributedURIType logicalAddress, AttributedURIType id, GetStatisticsHsaUnitType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public GetCareUnitMembersResponseType getCareUnitMembers(AttributedURIType logicalAddress, AttributedURIType id, LookupHsaObjectType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public GetHospPersonResponseType getHospPerson(AttributedURIType logicalAddress, AttributedURIType id, GetHospPersonType parameters) throws HsaWsFault {

        String personId = parameters.getPersonalIdentityNumber();
        HsaHospPerson hospPerson = hsaServiceStub.getHospPerson(personId);
        if (hospPerson == null) {
            return null;
        }

        GetHospPersonResponseType response = new GetHospPersonResponseType();
        response.setPersonalIdentityNumber(hospPerson.getPersonalIdentityNumber());
        response.setPersonalPrescriptionCode(hospPerson.getPersonalPrescriptionCode());

        EducationCodesType educationCodes = new EducationCodesType();
        educationCodes.getEducationCode().addAll(hospPerson.getEducationCodes());
        response.setEducationCodes(educationCodes);

        HsaTitlesType hsaTitles = new HsaTitlesType();
        hsaTitles.getHsaTitle().addAll(hospPerson.getHsaTitles());
        response.setHsaTitles(hsaTitles);

        RestrictionCodesType restrictionCodes = new RestrictionCodesType();
        restrictionCodes.getRestrictionCode().addAll(hospPerson.getRestrictionCodes());
        response.setRestrictionCodes(restrictionCodes);

        RestrictionsType restrictions = new RestrictionsType();
        restrictions.getRestriction().addAll(hospPerson.getRestrictions());
        response.setRestrictions(restrictions);

        SpecialityCodesType specialityCodes = new SpecialityCodesType();
        specialityCodes.getSpecialityCode().addAll(hospPerson.getSpecialityCodes());
        response.setSpecialityCodes(specialityCodes);

        SpecialityNamesType specialityNames = new SpecialityNamesType();
        specialityNames.getSpecialityName().addAll(hospPerson.getSpecialityNames());
        response.setSpecialityNames(specialityNames);

        TitleCodesType titleCodes = new TitleCodesType();
        titleCodes.getTitleCode().addAll(hospPerson.getTitleCodes());
        response.setTitleCodes(titleCodes);

        return response;
    }

    @Override
    public GetInformationListResponseType getInformationList(AttributedURIType logicalAddress, AttributedURIType id, GetInformationListType parameters) throws HsaWsFault {
        return null;
    }

    @Override
    public HandleCertifierResponseType handleCertifier(AttributedURIType logicalAddress, AttributedURIType id, HandleCertifierType parameters) throws HsaWsFault {

        LOG.debug("handleCertifier was called with personId '{}' certifierId '{}'", parameters.getPersonalIdentityNumber(), parameters.getCertifierId());

        HandleCertifierResponseType response = new HandleCertifierResponseType();
        response.setResult("OK");
        return response;
    }
}
