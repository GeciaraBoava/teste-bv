package com.bv.geciara.exception;

public class CorrentistaNaoEncontradoException extends RuntimeException {

    public CorrentistaNaoEncontradoException(Long id) {
        super("Correntista não encontrado com ID: " + id);
    }

    public CorrentistaNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
