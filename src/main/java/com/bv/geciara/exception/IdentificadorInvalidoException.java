package com.bv.geciara.exception;

public class IdentificadorInvalidoException extends RuntimeException {

    public IdentificadorInvalidoException(String tipo, String identificador) {
        super("Formato inválido para %s %s".formatted(tipo, identificador));
    }
}