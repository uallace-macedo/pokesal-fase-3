package br.com.pokesal.batalha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContextoBatalhaTest {

    @Test
    @DisplayName("O contexto começa sem pedido de parada e pode ser alterado")
    void testParadaPodeSerAlterada() {
        ContextoBatalha contexto = new ContextoBatalha();
        assertFalse(contexto.isParada());

        contexto.setParada(true);

        assertTrue(contexto.isParada());
    }

    @Test
    @DisplayName("A exceção de interrupção usa a mensagem padrão do requisito")
    void testMensagemPadraoDaExcecao() {
        InterrompeBatalhaException excecao = new InterrompeBatalhaException();

        assertEquals("Equipe Pokemal interrompeu a batalha", excecao.getMessage());
    }
}
