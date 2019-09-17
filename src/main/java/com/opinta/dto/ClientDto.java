package com.opinta.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;


@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ClientDto {
    private long id;
    @Size(max = 255)
    private String name;
    @Size(max = 25)
    private String uniqueRegistrationNumber;
    private long counterpartyId;
    private long addressId;
}
