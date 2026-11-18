package com.example.roleslz_backend.Utills;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrlUtilsTest {

    @Test
    void vazioOuNuloViraNull() {
        assertNull(UrlUtils.normalizar(null));
        assertNull(UrlUtils.normalizar("   "));
        assertNull(UrlUtils.normalizar(""));
    }

    @Test
    void prependeHttpsQuandoNaoTemEsquema() {
        assertEquals("https://sympla.com.br/evento", UrlUtils.normalizar("sympla.com.br/evento"));
        assertEquals("https://www.instagram.com/rolezinho", UrlUtils.normalizar("  www.instagram.com/rolezinho  "));
    }

    @Test
    void mantemEsquemaExistente() {
        assertEquals("http://exemplo.com", UrlUtils.normalizar("http://exemplo.com"));
        assertEquals("https://exemplo.com/x", UrlUtils.normalizar("https://exemplo.com/x"));
    }

    @Test
    void rejeitaLixo() {
        assertThrows(IllegalArgumentException.class, () -> UrlUtils.normalizar("isso não é link"));
        assertThrows(IllegalArgumentException.class, () -> UrlUtils.normalizar("semponto"));
    }
}
