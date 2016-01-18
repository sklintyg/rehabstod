package se.inera.privatlakarportal.pu.config;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;

import se.riv.population.residentmaster.lookupresidentforfullprofileresponder.v11.LookupResidentForFullProfileResponderInterface;

/**
 * Created by pebe on 2015-08-26.
 */
@Configuration
@ComponentScan("se.inera.privatlakarportal.pu.services")
@EnableCaching
@Import(PUStubConfiguration.class)
@ImportResource("classpath:pu-services-config.xml")
public class PUConfiguration {

    @Value("${putjanst.endpoint.url}")
    String puWsUrl;

    @Bean
    public LookupResidentForFullProfileResponderInterface puWebServiceClient() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setAddress(puWsUrl);
        proxyFactoryBean.setServiceClass(LookupResidentForFullProfileResponderInterface.class);
        return (LookupResidentForFullProfileResponderInterface) proxyFactoryBean.create();
    }

    @Bean
    public EhCacheCacheManager cacheManager() {
        EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager();
        ehCacheCacheManager.setCacheManager(ehCacheManagerFactory().getObject());
        return ehCacheCacheManager;
    }

    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactory() {
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        return ehCacheManagerFactoryBean;
    }

}
