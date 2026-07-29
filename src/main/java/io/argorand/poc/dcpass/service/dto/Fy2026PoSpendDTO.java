package io.argorand.poc.dcpass.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * FY2026 IT spend aggregated by purchase order for a contract within an agency.
 */
public class Fy2026PoSpendDTO implements Serializable {

    private String poNumber;
    private String poTitle;
    private BigDecimal spend;

    public Fy2026PoSpendDTO() {}

    public Fy2026PoSpendDTO(String poNumber, String poTitle, BigDecimal spend) {
        this.poNumber = poNumber;
        this.poTitle = poTitle;
        this.spend = spend;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getPoTitle() {
        return poTitle;
    }

    public void setPoTitle(String poTitle) {
        this.poTitle = poTitle;
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
        if (!(o instanceof Fy2026PoSpendDTO that)) {
            return false;
        }
        return Objects.equals(poNumber, that.poNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(poNumber);
    }

    @Override
    public String toString() {
        return "Fy2026PoSpendDTO{" + "poNumber='" + poNumber + "'" + ", poTitle='" + poTitle + "'" + ", spend=" + spend + "}";
    }
}
