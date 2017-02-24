package se.inera.intyg.rehabstod.service.hsa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by eriklupander on 2017-02-24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:EmployeeNameServiceTest/test-context.xml")
public class EmployeeNameServiceTest {

    @Autowired
    private EmployeeNameService testee;

    @Test
    public void testGetNameWithResult() {
        String employeeHsaName = testee.getEmployeeHsaName("TSTNMT2321000156-105R");
        assertEquals("Emma Nilsson", employeeHsaName);
    }
}
