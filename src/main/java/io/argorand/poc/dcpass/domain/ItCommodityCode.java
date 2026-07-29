package io.argorand.poc.dcpass.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * NIGP commodity codes that count as IT spend for reporting.
 * Loaded from {@code config/liquibase/data/it_commodity_codes.csv}.
 */
@Entity
@Table(name = "it_commodity_code")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ItCommodityCode implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "code", nullable = false)
    private Long code;

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItCommodityCode)) {
            return false;
        }
        return getCode() != null && getCode().equals(((ItCommodityCode) o).getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCode());
    }

    @Override
    public String toString() {
        return "ItCommodityCode{" + "code=" + getCode() + "}";
    }
}
