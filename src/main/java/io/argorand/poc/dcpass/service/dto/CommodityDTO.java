package io.argorand.poc.dcpass.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A commodity code/description pair associated with a pass contract.
 */
public class CommodityDTO implements Serializable {

    private String commodityCode;

    private String commodityDescription;

    public CommodityDTO() {}

    public CommodityDTO(String commodityCode, String commodityDescription) {
        this.commodityCode = commodityCode;
        this.commodityDescription = commodityDescription;
    }

    public String getCommodityCode() {
        return commodityCode;
    }

    public void setCommodityCode(String commodityCode) {
        this.commodityCode = commodityCode;
    }

    public String getCommodityDescription() {
        return commodityDescription;
    }

    public void setCommodityDescription(String commodityDescription) {
        this.commodityDescription = commodityDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommodityDTO)) {
            return false;
        }
        CommodityDTO that = (CommodityDTO) o;
        return Objects.equals(commodityCode, that.commodityCode) && Objects.equals(commodityDescription, that.commodityDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commodityCode, commodityDescription);
    }

    @Override
    public String toString() {
        return "CommodityDTO{" + "commodityCode='" + commodityCode + "'" + ", commodityDescription='" + commodityDescription + "'" + "}";
    }
}
