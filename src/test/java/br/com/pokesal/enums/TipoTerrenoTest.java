package br.com.pokesal.enums;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TipoTerrenoTest {
    private static final double DELTA = 0.0001;

    @Test
    @DisplayName("Asfalto Quente aumenta em 15% o dano de Fogo e não afeta os demais tipos")
    void testAsfaltoQuenteBeneficiaFogo() {
        assertAll(
                () -> assertEquals(
                        1.15, TipoTerreno.ASFALTO_QUENTE.multiplicadorDano(TipoElemental.FOGO), DELTA),
                () -> assertEquals(
                        1.0, TipoTerreno.ASFALTO_QUENTE.multiplicadorDano(TipoElemental.AGUA), DELTA),
                () -> assertEquals(
                        1.0, TipoTerreno.ASFALTO_QUENTE.multiplicadorDano(TipoElemental.NORMAL), DELTA));
    }

    @Test
    @DisplayName("Poça d'água aumenta em 10% o dano de Água e não afeta os demais tipos")
    void testPocaDAguaBeneficiaAgua() {
        assertAll(
                () -> assertEquals(
                        1.10, TipoTerreno.POCA_DAGUA.multiplicadorDano(TipoElemental.AGUA), DELTA),
                () -> assertEquals(
                        1.0, TipoTerreno.POCA_DAGUA.multiplicadorDano(TipoElemental.FOGO), DELTA));
    }

    @Test
    @DisplayName("Canteiro Central e Sem Efeito não alteram o dano de nenhum tipo")
    void testTerrenosSemBonusDeDano() {
        for (TipoElemental tipo : TipoElemental.values()) {
            assertEquals(1.0, TipoTerreno.CANTEIRO_CENTRAL.multiplicadorDano(tipo), DELTA);
            assertEquals(1.0, TipoTerreno.SEM_EFEITO.multiplicadorDano(tipo), DELTA);
        }
    }

    @Test
    @DisplayName("Cada terreno tem um nome de exibição")
    void testNomeDoTerreno() {
        assertEquals("Asfalto Quente", TipoTerreno.ASFALTO_QUENTE.getNome());
    }
}
