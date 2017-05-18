package com.opinta.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EnumValidator implements ConstraintValidator<EnumString, String> {
    private Set<String> AVAILABLE_ENUM_NAMES;

    private Set<String> getNamesSet(Class<? extends Enum<?>> e) {
        Enum<?>[] enums = e.getEnumConstants();
        String[] names = Arrays.stream(enums).map(Enum::name).toArray(String[]::new);
        return new HashSet<>(Arrays.asList(names));
    }

    @Override
    public void initialize(EnumString enumString) {
        AVAILABLE_ENUM_NAMES = getNamesSet(enumString.source());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || AVAILABLE_ENUM_NAMES.contains(value);
    }
}
