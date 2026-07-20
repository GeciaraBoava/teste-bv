package com.bv.geciara.util;

import com.bv.geciara.model.enums.ETipoIdentificador;

public final class ValidacaoUtil {

    private ValidacaoUtil() {
    }

    public static boolean isIdentificadorValid(ETipoIdentificador tipo, String identificador) {
        if (tipo == null || identificador == null || identificador.isBlank()) {
            return false;
        }

        if (ETipoIdentificador.CPF.equals(tipo)) {
            return identificador.length() == 11 && identificador.matches("\\d+");
        } else if (ETipoIdentificador.CNPJ.equals(tipo)) {
            return identificador.length() == 14 && identificador.matches("[a-zA-Z0-9]+");
        }
        return true;
    }
}
