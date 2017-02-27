package se.inera.intyg.rehabstod.service.hsa;

import org.apache.ignite.cache.CacheMetrics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.intyg.infra.cache.metrics.CacheStatisticsService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static se.inera.intyg.rehabstod.config.EmployeeNameCacheConfig.EMPLOYEE_NAME_CACHE_NAME;

/**
 * Tests the {@link EmployeeNameService} with a loaded spring-context, i.e. so we can verify that the cache is functioning
 * as expected.
 *
 * Since the asserts uses the {@link CacheMetrics}, all tests goes into the same test method as resetting of Ignite
 * CacheMetrics between tests doesn't seem to be supported by its API.
 *
 * Created by eriklupander on 2017-02-24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:EmployeeNameServiceTest/test-context.xml")
public class EmployeeNameServiceTest {

    @Autowired
    private EmployeeNameService testee;

    @Autowired
    private CacheStatisticsService cacheStatisticsService;

    /**
     * Note that we execute a flow of tests here since resetting the CacheMetrics between tests isn't possible through
     * the Ignite API.
     */
    @Test
    public void testGetNameWithResult() {

        // Start by querying for a know user.
        String employeeHsaName = testee.getEmployeeHsaName("TSTNMT2321000156-105R");
        assertEquals("Emma Nilsson", employeeHsaName);

        CacheMetrics nameCacheMetrics = cacheStatisticsService.getCacheStatistics().getCacheMetrics().get(EMPLOYEE_NAME_CACHE_NAME);

        // Expect one item in cache, one miss and zero hits. (Since Emma Nilsson wasn't in the cache on first request)
        assertEquals(1, nameCacheMetrics.getSize());
        assertEquals(1, nameCacheMetrics.getCacheMisses());
        assertEquals(0L, nameCacheMetrics.getCacheHits());

        // Make three additional requests for Emma.
        testee.getEmployeeHsaName("TSTNMT2321000156-105R");
        testee.getEmployeeHsaName("TSTNMT2321000156-105R");
        testee.getEmployeeHsaName("TSTNMT2321000156-105R");

        // Re-read the cache metrics and assert that we've gotten three hits but no new items or misses.
        nameCacheMetrics = cacheStatisticsService.getCacheStatistics().getCacheMetrics().get(EMPLOYEE_NAME_CACHE_NAME);
        assertEquals(1, nameCacheMetrics.getSize());
        assertEquals(1, nameCacheMetrics.getCacheMisses());
        assertEquals(3L, nameCacheMetrics.getCacheHits());

        // Try to load an unknown user twice.
        employeeHsaName = testee.getEmployeeHsaName("inte-ett-hsa-id");
        assertNull(employeeHsaName);
        employeeHsaName = testee.getEmployeeHsaName("inte-ett-hsa-id");
        assertNull(employeeHsaName);

        nameCacheMetrics = cacheStatisticsService.getCacheStatistics().getCacheMetrics().get(EMPLOYEE_NAME_CACHE_NAME);
        assertEquals("Expect 1 item in cache, nulls should not be cached", 1, nameCacheMetrics.getSize());
        assertEquals("Expect two new misses as nulls shouldn't be cached", 3, nameCacheMetrics.getCacheMisses());
        assertEquals("No new hits are expected", 3L, nameCacheMetrics.getCacheHits());
    }
}
