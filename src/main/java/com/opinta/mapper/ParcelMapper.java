package com.opinta.mapper;

import com.opinta.dto.ParcelDto;
import com.opinta.entity.Parcel;
import org.mapstruct.Mapper;

/**
 * Created by Dmytro Kushnir on 13.05.17.
 */
@Mapper(componentModel = "spring")
public interface ParcelMapper extends BaseMapper<ParcelDto, Parcel> {

}
