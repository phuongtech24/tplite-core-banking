package com.tplite.core_banking.common.validation;

import java.util.Locale;

public final class EnumParser {
    private EnumParser() {
    }

    public static <E extends Enum<E>> E parse(Class<E> enumClass, String value) {
        if (value == null) {
            return null;
        }
        return Enum.valueOf(enumClass, value.trim().toUpperCase(Locale.ROOT));
    }
}