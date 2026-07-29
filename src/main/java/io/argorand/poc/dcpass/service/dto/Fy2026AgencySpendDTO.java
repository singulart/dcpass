package io.argorand.poc.dcpass.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * FY2026 IT spend aggregated by agency (from {@code mv_fy2026_it_spend_by_agency}).
 */
public class Fy2026AgencySpendDTO implements Serializable {

    private String agency;
    private String agencyAcronym;
    private BigDecimal spend;

    public Fy2026AgencySpendDTO() {}

    public Fy2026AgencySpendDTO(String agency, String agencyAcronym, BigDecimal spend) {
        this.agency = agency;
        this.agencyAcronym = agencyAcronym;
        this.spend = spend;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getAgencyAcronym() {
        return agencyAcronym;
    }

    public void setAgencyAcronym(String agencyAcronym) {
        this.agencyAcronym = agencyAcronym;
    }

    public BigDecimal getSpend() {
        return spend;
    }

    public void setSpend(BigDecimal spend) {
        this.spend = spend;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Fy2026AgencySpendDTO that)) {
            return false;
        }
        return Objects.equals(agencyAcronym, that.agencyAcronym);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agencyAcronym);
    }

    @Override
    public String toString() {
        return "Fy2026AgencySpendDTO{" + "agency='" + agency + "'" + ", agencyAcronym='" + agencyAcronym + "'" + ", spend=" + spend + "}";
    }
}
