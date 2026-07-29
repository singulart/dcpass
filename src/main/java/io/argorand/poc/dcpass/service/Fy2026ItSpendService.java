package io.argorand.poc.dcpass.service;

import io.argorand.poc.dcpass.repository.Fy2026ReportRepository;
import io.argorand.poc.dcpass.repository.Fy2026ReportRepository.AgencySpendRow;
import io.argorand.poc.dcpass.repository.Fy2026ReportRepository.ContractSpendRow;
import io.argorand.poc.dcpass.repository.Fy2026ReportRepository.PoSpendRow;
import io.argorand.poc.dcpass.service.dto.Fy2026AgencySpendDTO;
import io.argorand.poc.dcpass.service.dto.Fy2026ContractSpendDTO;
import io.argorand.poc.dcpass.service.dto.Fy2026PoSpendDTO;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * FY2026 IT spend report data for the interactive drill-down charts.
 */
@Service
@Transactional(readOnly = true)
public class Fy2026ItSpendService {

    private static final Logger LOG = LoggerFactory.getLogger(Fy2026ItSpendService.class);

    private final Fy2026ReportRepository fy2026ReportRepository;

    public Fy2026ItSpendService(Fy2026ReportRepository fy2026ReportRepository) {
        this.fy2026ReportRepository = fy2026ReportRepository;
    }

    public List<Fy2026AgencySpendDTO> getSpendByAgency() {
        LOG.debug("Request to get FY2026 IT spend by agency");
        List<Fy2026AgencySpendDTO> result = new ArrayList<>();
        for (AgencySpendRow row : fy2026ReportRepository.mapAgencySpend()) {
            String agencyRaw = row.agencyRaw();
            String acronym = agencyRaw;
            String display = agencyRaw;
            int pipe = agencyRaw.indexOf('|');
            if (pipe >= 0) {
                acronym = agencyRaw.substring(0, pipe);
                String name = agencyRaw.substring(pipe + 1);
                display = name.isBlank() ? acronym : (acronym.isBlank() ? name : acronym + " — " + name);
            }
            result.add(new Fy2026AgencySpendDTO(display, acronym, row.spend()));
        }
        return result;
    }

    public List<Fy2026ContractSpendDTO> getSpendByContract(String agencyAcronym) {
        LOG.debug("Request to get FY2026 IT spend by contract for agency {}", agencyAcronym);
        List<Fy2026ContractSpendDTO> result = new ArrayList<>();
        for (ContractSpendRow row : fy2026ReportRepository.mapContractSpend(agencyAcronym)) {
            result.add(new Fy2026ContractSpendDTO(row.contractTitle(), row.contractNumber(), row.spend()));
        }
        return result;
    }

    public List<Fy2026PoSpendDTO> getSpendByPo(String agencyAcronym, String contractNumber) {
        LOG.debug("Request to get FY2026 IT spend by PO for agency {} contract {}", agencyAcronym, contractNumber);
        List<Fy2026PoSpendDTO> result = new ArrayList<>();
        for (PoSpendRow row : fy2026ReportRepository.mapPoSpend(agencyAcronym, contractNumber)) {
            result.add(new Fy2026PoSpendDTO(row.purchaseOrderId(), row.poNumber(), row.poTitle(), row.spend()));
        }
        return result;
    }
}
