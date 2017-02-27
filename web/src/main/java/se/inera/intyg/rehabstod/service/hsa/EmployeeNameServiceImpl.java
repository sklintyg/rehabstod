package se.inera.intyg.rehabstod.service.hsa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsa.services.HsaEmployeeService;
import se.riv.infrastructure.directory.v1.PersonInformationType;

import javax.xml.ws.WebServiceException;
import java.util.List;

import static se.inera.intyg.rehabstod.config.EmployeeNameCacheConfig.EMPLOYEE_NAME_CACHE_NAME;


/**
 * Class that provides a cachable facade for calls to HSA getEmployeeIncludingProtectedPerson.
 *
 * The purpose is to allow calls to determine doctor current name in HSA to be cached without
 * introducing caching in the hsa-integration {@link se.inera.intyg.infra.integration.hsa.client.EmployeeService} itself.
 *
 * Created by eriklupander on 2017-02-23.
 */
@Service
public class EmployeeNameServiceImpl implements EmployeeNameService {

    @Autowired
    private HsaEmployeeService employeeService;

    @Override
    @Cacheable(value = EMPLOYEE_NAME_CACHE_NAME, key = "#employeeHsaId", unless = "#result == null")
    public String getEmployeeHsaName(String employeeHsaId) {
        try {
            List<PersonInformationType> employeeInfo = employeeService.getEmployee(employeeHsaId, null, null);
            if (employeeInfo.size() > 0) {
                return employeeInfo.get(0).getGivenName() + " " + employeeInfo.get(0).getMiddleAndSurName();
            } else {
                return null;
            }
        } catch (WebServiceException e) {
            // If there are problems calling HSA, we'll return null which indicates that the name couldn't be fetched.
            // Nulls shouldn't be cached, e.g. forces new retry.
            return null;
        }
    }
}
