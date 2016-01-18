package se.inera.privatlakarportal.service.postnummer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import se.inera.privatlakarportal.service.postnummer.model.Omrade;
import se.inera.privatlakarportal.service.postnummer.repo.PostnummerRepository;
import se.inera.privatlakarportal.service.postnummer.repo.PostnummerRepositoryFactory;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by pebe on 2015-08-12.
 */
@Service
public class PostnummerServiceImpl implements PostnummerService {

    private static final Logger LOG = LoggerFactory.getLogger(PostnummerRepositoryFactory.class);

    @Autowired
    private Environment env;

    @Autowired
    private PostnummerRepositoryFactory postnummerRepositoryFactory;

    private PostnummerRepository postnummerRepository;

    @PostConstruct
    public void init() {
        postnummerRepository = postnummerRepositoryFactory.createAndInitPostnummerRepository(env.getProperty("postnummer.file"));
    }

    @Override
    public List<Omrade> getOmradeByPostnummer(String postnummer) {
        LOG.debug("Lookup omrade by postnummer '{}'", postnummer);
        return postnummerRepository.getOmradeByPostnummer(postnummer);
    }

}
