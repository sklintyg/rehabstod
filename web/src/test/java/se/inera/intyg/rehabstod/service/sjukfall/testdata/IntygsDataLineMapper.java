/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.service.sjukfall.testdata;

import java.time.LocalDate;
import java.time.LocalDateTime;
import se.inera.intyg.rehabstod.service.sjukfall.testdata.builders.ArbetsformagaT;
import se.inera.intyg.rehabstod.service.sjukfall.testdata.builders.EnhetT;
import se.inera.intyg.rehabstod.service.sjukfall.testdata.builders.FormagaT;
import se.inera.intyg.rehabstod.service.sjukfall.testdata.builders.HosPersonalT;
import se.inera.intyg.rehabstod.service.sjukfall.testdata.builders.IdT;
import se.inera.intyg.rehabstod.service.sjukfall.testdata.builders.IntygsDataT;
import se.inera.intyg.rehabstod.service.sjukfall.testdata.builders.PatientT;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.HsaId;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.IIType;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.IntygId;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.PersonId;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Arbetsformaga;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Enhet;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Formaga;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.HosPersonal;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Patient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Magnus Ekstrand on 2016-02-11.
 */
public class IntygsDataLineMapper {

    Set<String[]> fields;


    public IntygsDataLineMapper() {
        fields = new HashSet();
    }

    public static List<IntygsData> map(List<String> lines) {
        IntygsDataLineMapper mapper = new IntygsDataLineMapper();

        for (String line : lines) {
            String[] splitData = mapper.toArray(line);
            if (splitData != null) {
                mapper.fields.add(splitData);
            }
        }

        return mapper.map(mapper.fields);
    }

    private List<IntygsData> map(Set<String[]> fields) {

        List<IntygsData> intygsData = new ArrayList();
        FormagaFieldSetMapper ffsm = new FormagaFieldSetMapper();

        // CHECKSTYLE:OFF MagicNumber
        Iterator<String[]> iter = fields.iterator();
        while (iter.hasNext()) {
            String[] data = iter.next();

            // Map data to objects
            Patient patient = patient(data[1], data[2], data[3], data[4]);
            Enhet enhet = enhet(data[6], data[7]);
            HosPersonal skapadAv = personal(data[8], data[9], enhet);
            Arbetsformaga arbetsformaga = arbetsformaga(ffsm.map(data[10]));
            Boolean enkeltIntyg = Boolean.valueOf(data[11]);
            LocalDateTime signeringsTidpunkt = LocalDateTime.parse(data[12]);

            // Add to intygsData list
            intygsData.add(intygsData(data[0], patient, skapadAv, data[5], arbetsformaga, enkeltIntyg, signeringsTidpunkt));
        }
        // CHECKSTYLE:ON MagicNumber

        return intygsData;
    }

    private IntygsData intygsData(String intygsId, Patient patient, HosPersonal skapadAv, String diagnoskod, Arbetsformaga arbetsformaga, boolean enkeltIntyg, LocalDateTime signeringsTidpunkt) {
        return new IntygsDataT.IntygsDataBuilder()
                .intygsId(intygsId)
                .patient(patient)
                .skapadAv(skapadAv)
                .diagnoskod(diagnoskod)
                .arbetsformaga(arbetsformaga)
                .enkeltIntyg(enkeltIntyg)
                .signeringsTidpunkt(signeringsTidpunkt)
                .build();
    }

    private IIType id() {
        return new IdT.IITypeBuilder().extension(UUID.randomUUID().toString()).build();
    }

    private IIType id(String id) {
        return new IdT.IITypeBuilder().extension(id).build();
    }

    private IntygId intygId(IIType type) {
        IntygId obj = new IntygId();
        obj.setExtension(type.getExtension());
        return obj;
    }

    private PersonId personId(IIType type) {
        PersonId obj = new PersonId();
        obj.setExtension(type.getExtension());
        return obj;
    }

    private HsaId hsaId(IIType type) {
        HsaId obj = new HsaId();
        obj.setExtension(type.getExtension());
        return obj;
    }

    private Patient patient(String pid, String fnamn, String mnamn, String enamn) {
        String pnamn = "";

        if (fnamn != null) {
            pnamn = fnamn;
        }

        if (mnamn != null) {
            pnamn = pnamn.isEmpty() ? mnamn : pnamn + " " + mnamn;
        }

        if (enamn != null) {
            pnamn = pnamn.isEmpty() ? enamn : pnamn + " " + enamn;
        }

        return patient(pid, pnamn);
    }

    private Patient patient(String pid, String pnamn) {
        return new PatientT.PatientBuilder().personId(personId(id(pid))).namn(pnamn).build();
    }

    private Enhet enhet(String eid, String namn) {
        return new EnhetT.EnhetBuilder().enhetsId(hsaId(id(eid))).enhetsnamn(namn).build();
    }

    private HosPersonal personal(String hsaId, String namn, Enhet enhet) {
        return new HosPersonalT.HosPersonalBuilder().personalId(hsaId(id(hsaId))).fullstandigtNamn(namn).enhet(enhet).build();
    }

    private Arbetsformaga arbetsformaga(List<Formaga> formagaList) {
        return new ArbetsformagaT.ArbetsformagaBuilder().formaga(formagaList).build();
    }

    private String[] toArray(String csv) {
        if (csv != null) {
            return csv.split("\\s*,\\s*");
        }

        return null;
    }

    class FormagaFieldSetMapper {

        public List<Formaga> map(String arbetsformaga) {
            List<Formaga> formagaList = new ArrayList();
            String[] formagor = arbetsformaga.replace("[", "").replace("]", "").split("\\|");

            for (String formaga : formagor) {
                String[] fields = formaga.split(";");
                formagaList.add(formaga(LocalDate.parse(fields[0]), LocalDate.parse(fields[1]), Integer.parseInt(fields[2])));
            }

            return formagaList;
        }

        private Formaga formaga(LocalDate start, LocalDate slut, int nedsatthet) {
            return new FormagaT.FormagaBuilder().startdatum(start).slutdatum(slut).nedsattning(nedsatthet).build();
        }
    }

}
