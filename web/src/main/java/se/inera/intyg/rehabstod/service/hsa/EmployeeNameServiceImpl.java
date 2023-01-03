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
package se.inera.intyg.rehabstod.service.hsa;

import static se.inera.intyg.rehabstod.config.EmployeeNameCacheConfig.EMPLOYEE_NAME_CACHE_NAME;

import java.util.List;
import javax.xml.ws.WebServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.model.PersonInformation;
import se.inera.intyg.infra.integration.hsatk.services.legacy.HsaEmployeeService;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;

@Service
public class EmployeeNameServiceImpl implements EmployeeNameService {

    @Autowired
    private HsaEmployeeService employeeService;

    @Override
    @PrometheusTimeMethod
    @Cacheable(value = EMPLOYEE_NAME_CACHE_NAME, key = "#employeeHsaId")
    public String getEmployeeHsaName(String employeeHsaId) {
        final var employeeInfo = getEmployee(employeeHsaId);
        if (isEmpty(employeeInfo)) {
            return employeeHsaId;
        }
        return getName(employeeInfo);
    }

    private List<PersonInformation> getEmployee(String employeeHsaId) {
        try {
            return employeeService.getEmployee(employeeHsaId, null, null);
        } catch (WebServiceException e) {
            return null;
        }
    }

    private boolean isEmpty(List<PersonInformation> employeeInfo) {
        return employeeInfo == null || employeeInfo.isEmpty();
    }

    private String getName(List<PersonInformation> employeeInfo) {
        return employeeInfo.get(0).getGivenName() + " " + employeeInfo.get(0).getMiddleAndSurName();
    }
}
