package com.bv.geciara.util;

import com.bv.geciara.model.enums.ETipoIdentificador;

public final class ValidacaoUtil {

    private ValidacaoUtil() {
    }

    public static boolean isIdentificadorValid(ETipoIdentificador tipo, String identificador) {
        if (tipo == null || identificador == null || identificador.isBlank()) {
            return false;
        }

        if (ETipoIdentificador.CPF.equals(tipo)
                && identificador.length() != 11
                && identificador.matches("\\d+")) {
            return false;
        } else if (ETipoIdentificador.CNPJ.equals(tipo) && identificador.length() != 14) {
            return false;
        } else {
            return true;
        }
    }
}
