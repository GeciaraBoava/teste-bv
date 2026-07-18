package com.bv.geciara.util;

public final class SanitizacaoUtil {

    private SanitizacaoUtil() {
    }

    public static String sanitizar(String valor) {
        if (valor == null) {
            return null;
        }
        return valor.replaceAll("[^a-zA-Z0-9]", "");
    }

    public static String sanitizarDocumento(String documento) {
        return sanitizar(documento);
    }

    public static String sanitizarCep(String cep) {
        return sanitizar(cep);
    }

}
