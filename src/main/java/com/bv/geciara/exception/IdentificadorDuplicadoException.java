package com.bv.geciara.exception;

public class IdentificadorDuplicadoException extends RuntimeException {

    public IdentificadorDuplicadoException(String tipo) {
        super("Já existe um correntista cadastrado com este tipo e número de identificador (" + tipo + ")");
    }
}
