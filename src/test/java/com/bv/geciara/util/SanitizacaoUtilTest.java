package com.bv.geciara.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SanitizacaoUtilTest {

    @Test
    void sanitizar_deveRemoverPontosTraçosEBarras() {
        assertEquals("12345678909", SanitizacaoUtil.sanitizar("123.456.789-09"));
    }

    @Test
    void sanitizar_deveRemoverTraçosDoCEP() {
        assertEquals("01310100", SanitizacaoUtil.sanitizar("01310-100"));
    }

    @Test
    void sanitizar_deveRemoverPontosTraçosEBarrasDoCNPJ() {
        assertEquals("12345678000190", SanitizacaoUtil.sanitizar("12.345.678/0001-90"));
    }

    @Test
    void sanitizar_deveRetornarNull_QuandoValorNull() {
        assertNull(SanitizacaoUtil.sanitizar(null));
    }

    @Test
    void sanitizar_deveRetornarVazio_QuandoValorVazio() {
        assertEquals("", SanitizacaoUtil.sanitizar(""));
    }

    @Test
    void sanitizar_deveManterValor_QuandoJaLimpo() {
        assertEquals("12345678909", SanitizacaoUtil.sanitizar("12345678909"));
    }

    @Test
    void sanitizar_deveRemoverEspacos() {
        assertEquals("12345678909", SanitizacaoUtil.sanitizar("123 456 789 09"));
    }

    @Test
    void sanitizar_deveRemoverCaracteresEspeciais() {
        assertEquals("AB1234567", SanitizacaoUtil.sanitizar("AB-123.456/7"));
    }

    @Test
    void sanitizarDocumento_deveDelegarParaSanitizar() {
        assertEquals("12345678909", SanitizacaoUtil.sanitizarDocumento("123.456.789-09"));
    }

    @Test
    void sanitizarDocumento_deveRetornarNull_QuandoValorNull() {
        assertNull(SanitizacaoUtil.sanitizarDocumento(null));
    }

    @Test
    void sanitizarCep_deveDelegarParaSanitizar() {
        assertEquals("01310100", SanitizacaoUtil.sanitizarCep("01310-100"));
    }

    @Test
    void sanitizarCep_deveRetornarNull_QuandoValorNull() {
        assertNull(SanitizacaoUtil.sanitizarCep(null));
    }

    @Test
    void sanitizar_deveManterApenasAlfanuméricos() {
        assertEquals("abc123", SanitizacaoUtil.sanitizar("!@#abc$%^123&*()"));
    }

    @Test
    void sanitizar_deveTratarRGComPontos() {
        assertEquals("123456789", SanitizacaoUtil.sanitizar("12.345.678-9"));
    }

    @Test
    void sanitizar_deveTratarPassaporte() {
        assertEquals("AB1234567", SanitizacaoUtil.sanitizar("AB1234567"));
    }
}
