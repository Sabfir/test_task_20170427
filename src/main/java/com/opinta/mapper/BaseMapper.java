package com.opinta.mapper;

import org.mapstruct.InheritInverseConfiguration;

import java.util.List;

/**
 * Base dto mapper
 *
 * @param <D> type of Dto
 * @param <E> type of Entity
 */
public interface BaseMapper<D, E> {

    D toDto(E entity);

    List<D> toDto(List<E> entities);

    @InheritInverseConfiguration
    E toEntity(D dto);

    List<E> toEntity(List<D> dtos);
}
