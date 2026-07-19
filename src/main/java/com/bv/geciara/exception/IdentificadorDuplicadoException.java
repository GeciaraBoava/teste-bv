package com.bv.geciara.exception;

public class IdentificadorDuplicadoException extends RuntimeException {

    public IdentificadorDuplicadoException(String tipo, String identificador) {
        super("Já existe um correntista cadastrado para %s %s".formatted(tipo, identificador));
    }
}
