package se.inera.intyg.rehabstod.service.sjukfall.nameresolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.service.hsa.EmployeeNameService;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Lakare;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by eriklupander on 2017-02-23.
 */
@Service
public class SjukfallEmployeeNameResolverImpl implements SjukfallEmployeeNameResolver {

    @Autowired
    private EmployeeNameService employeeNameService;

    @Override
    public void enrichWithHsaEmployeeNames(List<InternalSjukfall> sjukfallList) {
        sjukfallList.stream().forEach(sf -> {
            String employeeHsaName = employeeNameService.getEmployeeHsaName(sf.getLakare().getHsaId());
            if (employeeHsaName != null) {
                sf.getLakare().setNamn(employeeHsaName);
            } else {
                sf.getLakare().setNamn(sf.getLakare().getHsaId());
            }
        });
    }

    @Override
    public void updateDuplicateDoctorNamesWithHsaId(List<InternalSjukfall> sjukfallList) {
        // Get number of unique lakare hsaIds
        double numberOfHsaIds = sjukfallList.stream().map(sf -> sf.getLakare().getHsaId()).distinct().count();
        double numberOfLakareNames = sjukfallList.stream().map(sf -> sf.getLakare().getNamn()).distinct().count();

        // If these are identical we can safely return.
        if (numberOfHsaIds == numberOfLakareNames) {
            return;
        }

        // Make sure there are no two doctors in the list with the same name, but different hsaId's
        Map<String, List<Lakare>> collect = sjukfallList.stream()
                .map(sf -> sf.getLakare())
                .collect(Collectors.groupingBy(lakare -> lakare.getNamn()));

        for (Map.Entry<String, List<Lakare>> entry : collect.entrySet()) {

            double numberOfUnique = entry.getValue().stream().map(l -> l.getHsaId()).distinct().count();
            if (numberOfUnique > 1) {
                entry.getValue().stream().forEach(lakare -> {
                    lakare.setNamn(lakare.getNamn() + " (" + lakare.getHsaId() + ")");
                });
            }

        }
    }
}
