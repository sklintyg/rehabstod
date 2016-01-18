package se.inera.privatlakarportal.service.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import se.inera.privatlakarportal.common.integration.json.CustomObjectMapper;
import se.inera.privatlakarportal.persistence.model.MedgivandeText;
import se.inera.privatlakarportal.persistence.repository.MedgivandeTextRepository;

@Service
@DependsOn("dbUpdate")
public class MedgivandeBootstrapBean {
    private static final Logger LOG = LoggerFactory.getLogger(MedgivandeBootstrapBean.class);

    @Autowired
    private MedgivandeTextRepository medgivandeTextRepository;

    @PostConstruct
    public void initData() {

        List<Resource> files = getResourceListing("bootstrap-medgivande/*.json");
        for (Resource res : files) {
            LOG.debug("Loading resource " + res.getFilename());
            addMedgivandeText(res);
        }
    }

    private void addMedgivandeText(Resource res) {

        try {
            MedgivandeText medgivandeText = new CustomObjectMapper().readValue(res.getInputStream(), MedgivandeText.class);
            if (!medgivandeTextRepository.exists(medgivandeText.getVersion())) {
                medgivandeTextRepository.save(medgivandeText);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private List<Resource> getResourceListing(String classpathResourcePath) {
        try {
            PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver();
            return Arrays.asList(r.getResources(classpathResourcePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
