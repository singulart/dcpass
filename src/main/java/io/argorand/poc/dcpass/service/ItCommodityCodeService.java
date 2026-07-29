package io.argorand.poc.dcpass.service;

import io.argorand.poc.dcpass.repository.ItCommodityCodeRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Shared access to the IT commodity-code allowlist loaded by Liquibase.
 */
@Service
@Transactional(readOnly = true)
public class ItCommodityCodeService {

    /**
     * Join {@code purchase_order} rows whose numeric {@code commoditycode} is on the IT allowlist.
     * Use in native queries with alias {@code po} for purchase_order.
     */
    public static final String JOIN_PURCHASE_ORDER =
        "JOIN it_commodity_code icc ON po.commoditycode ~ '^[0-9]+$' AND po.commoditycode::bigint = icc.code";

    private final ItCommodityCodeRepository itCommodityCodeRepository;

    private volatile List<Long> cachedCodes;

    public ItCommodityCodeService(ItCommodityCodeRepository itCommodityCodeRepository) {
        this.itCommodityCodeRepository = itCommodityCodeRepository;
    }

    public List<Long> getCodes() {
        List<Long> codes = cachedCodes;
        if (codes == null) {
            synchronized (this) {
                codes = cachedCodes;
                if (codes == null) {
                    cachedCodes = codes = List.copyOf(itCommodityCodeRepository.findAllCodes());
                }
            }
        }
        return codes;
    }
}
