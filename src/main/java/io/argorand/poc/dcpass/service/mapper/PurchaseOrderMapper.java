package io.argorand.poc.dcpass.service.mapper;

import io.argorand.poc.dcpass.domain.PurchaseOrder;
import io.argorand.poc.dcpass.service.dto.PurchaseOrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PurchaseOrder} and its DTO {@link PurchaseOrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper extends EntityMapper<PurchaseOrderDTO, PurchaseOrder> {}
