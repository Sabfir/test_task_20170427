package com.opinta.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class PostOfficeDto {
    private long id;
    @Size(max = 255)
    private String name;
    private long addressId;
    private long postcodePoolId;
}
