package se.inera.privatlakarportal.service.postnummer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.inera.privatlakarportal.service.postnummer.model.Omrade;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Created by pebe on 2015-08-12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(initializers = PostnummerServiceTest.PropertyMockingApplicationContextInitializer.class, classes = PostnummerServiceTest.TestConfiguration.class)
public class PostnummerServiceTest {

    public static class PropertyMockingApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
            MockPropertySource mockEnvVars = new MockPropertySource()
                .withProperty("postnummer.file", "classpath:/rec2LK_example.csv")
                .withProperty("postnummer.encoding", "ISO-8859-1");
            propertySources.replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, mockEnvVars);
        }
    }

    @ComponentScan("se.inera.privatlakarportal.service.postnummer")
    @Configuration
    public static class TestConfiguration {
    }

    @Autowired
    PostnummerService postnummerService;

    @Test
    public void testGetPostnummer() {

        List<Omrade> omrade13061 = Arrays.asList(new Omrade("13061", "HÅRSFJÄRDEN", "HANINGE", "STOCKHOLM"));
        List<Omrade> omrade13100 = Arrays.asList(new Omrade("13100", "NACKA", "NACKA", "STOCKHOLM"));
        List<Omrade> omrade13155 = Arrays.asList(new Omrade("13155", "NACKA", "STOCKHOLM", "STOCKHOLM"),
                                                 new Omrade("13155", "NACKA", "NACKA", "STOCKHOLM"));

        assertNull(postnummerService.getOmradeByPostnummer(null));
        assertNull(postnummerService.getOmradeByPostnummer(""));
        assertNull(postnummerService.getOmradeByPostnummer("xxyy"));
        assertThat(postnummerService.getOmradeByPostnummer("13061"), is(omrade13061));
        assertThat(postnummerService.getOmradeByPostnummer("13100"), is(omrade13100));
        assertThat(postnummerService.getOmradeByPostnummer("13155"), is(omrade13155));
        assertThat(postnummerService.getOmradeByPostnummer("13155"), not(omrade13061));
    }

}
