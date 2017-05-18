package com.opinta.mapper;

import org.mapstruct.InheritInverseConfiguration;

import java.util.List;

/**
 * Base dto mapper
 *
 * @param <DTO>    type of Dto
 * @param <ENTITY> type of Entity
 */
public interface BaseMapper<DTO, ENTITY> {

    DTO toDto(ENTITY entity);

    List<DTO> toDto(List<ENTITY> entities);

    @InheritInverseConfiguration
    ENTITY toEntity(DTO dto);

    List<ENTITY> toEntity(List<DTO> dtos);
}
