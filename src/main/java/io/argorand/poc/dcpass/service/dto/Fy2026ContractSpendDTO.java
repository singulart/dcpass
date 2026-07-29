package io.argorand.poc.dcpass.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * FY2026 IT spend aggregated by contract for a single agency.
 */
public class Fy2026ContractSpendDTO implements Serializable {

    private String contractTitle;
    private String contractNumber;
    private BigDecimal spend;

    public Fy2026ContractSpendDTO() {}

    public Fy2026ContractSpendDTO(String contractTitle, String contractNumber, BigDecimal spend) {
        this.contractTitle = contractTitle;
        this.contractNumber = contractNumber;
        this.spend = spend;
    }

    public String getContractTitle() {
        return contractTitle;
    }

    public void setContractTitle(String contractTitle) {
        this.contractTitle = contractTitle;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
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
        if (!(o instanceof Fy2026ContractSpendDTO that)) {
            return false;
        }
        return Objects.equals(contractNumber, that.contractNumber) && Objects.equals(contractTitle, that.contractTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contractNumber, contractTitle);
    }

    @Override
    public String toString() {
        return (
            "Fy2026ContractSpendDTO{" +
            "contractTitle='" +
            contractTitle +
            "'" +
            ", contractNumber='" +
            contractNumber +
            "'" +
            ", spend=" +
            spend +
            "}"
        );
    }
}
