package io.argorand.poc.dcpass.service.mapper;

import io.argorand.poc.dcpass.domain.PassContract;
import io.argorand.poc.dcpass.service.dto.PassContractDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PassContract} and its DTO {@link PassContractDTO}.
 */
@Mapper(componentModel = "spring")
public interface PassContractMapper extends EntityMapper<PassContractDTO, PassContract> {}
