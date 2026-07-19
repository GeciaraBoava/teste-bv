package com.bv.geciara.util;

import com.bv.geciara.model.enums.ETipoIdentificador;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ValidacaoUtilTest {

    @Test
    void isIdentificadorValid_deveRetornarTrue_CpfValido() {
        assertTrue(ValidacaoUtil.isIdentificadorValid(ETipoIdentificador.CPF, "12345678900"));
    }

    @Test
    void isIdentificadorValid_deveRetornarFalse_CpfComTamanhoInvalido() {
        assertFalse(ValidacaoUtil.isIdentificadorValid(ETipoIdentificador.CPF, "12345"));
    }

    @Test
    void isIdentificadorValid_deveRetornarTrue_CpfComLetras() {
        assertTrue(ValidacaoUtil.isIdentificadorValid(ETipoIdentificador.CPF, "12345abcde"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void isIdentificadorValid_deveRetornarFalse_NullOuVazio(String valor) {
        assertFalse(ValidacaoUtil.isIdentificadorValid(ETipoIdentificador.CPF, valor));
    }

    @Test
    void isIdentificadorValid_deveRetornarFalse_TipoNulo() {
        assertFalse(ValidacaoUtil.isIdentificadorValid(null, "12345678900"));
    }

    @Test
    void isIdentificadorValid_deveRetornarTrue_CnpjValido() {
        assertTrue(ValidacaoUtil.isIdentificadorValid(ETipoIdentificador.CNPJ, "12345678000190"));
    }

    @Test
    void isIdentificadorValid_deveRetornarFalse_CnpjTamanhoInvalido() {
        assertFalse(ValidacaoUtil.isIdentificadorValid(ETipoIdentificador.CNPJ, "12345"));
    }

    @Test
    void isIdentificadorValid_deveRetornarTrue_PassaporteValido() {
        assertTrue(ValidacaoUtil.isIdentificadorValid(ETipoIdentificador.PASSAPORTE, "AB1234567"));
    }

    @Test
    void isIdentificadorValid_deveRetornarTrue_RgValido() {
        assertTrue(ValidacaoUtil.isIdentificadorValid(ETipoIdentificador.RG, "123456789"));
    }
}
