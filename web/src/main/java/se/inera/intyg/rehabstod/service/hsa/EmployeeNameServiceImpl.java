/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.hsa;

import static se.inera.intyg.rehabstod.config.EmployeeNameCacheConfig.EMPLOYEE_NAME_CACHE_NAME;

import java.util.List;
import javax.xml.ws.WebServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsa.services.HsaEmployeeService;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.riv.infrastructure.directory.v1.PersonInformationType;


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
    @PrometheusTimeMethod
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
