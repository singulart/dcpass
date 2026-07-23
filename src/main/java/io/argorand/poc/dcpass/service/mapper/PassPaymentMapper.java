package io.argorand.poc.dcpass.service.mapper;

import io.argorand.poc.dcpass.domain.PassPayment;
import io.argorand.poc.dcpass.service.dto.PassPaymentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PassPayment} and its DTO {@link PassPaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface PassPaymentMapper extends EntityMapper<PassPaymentDTO, PassPayment> {}
