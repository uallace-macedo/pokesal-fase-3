package br.com.pokesal.enums;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TipoElementalTest {
    private static final double DELTA = 0.0001;

    @Test
    @DisplayName("Fogo, Planta e Água têm vantagem (x2) sobre o tipo que vencem")
    void testVantagemElemental() {
        assertAll(
                () -> assertEquals(
                        2.0, TipoElemental.FOGO.multiplicadorContra(TipoElemental.PLANTA), DELTA),
                () -> assertEquals(
                        2.0, TipoElemental.PLANTA.multiplicadorContra(TipoElemental.AGUA), DELTA),
                () -> assertEquals(2.0, TipoElemental.AGUA.multiplicadorContra(TipoElemental.FOGO), DELTA));
    }

    @Test
    @DisplayName("Fogo, Planta e Água têm desvantagem (x0,5) contra o tipo que os vence")
    void testDesvantagemElemental() {
        assertAll(
                () -> assertEquals(0.5, TipoElemental.FOGO.multiplicadorContra(TipoElemental.AGUA), DELTA),
                () -> assertEquals(
                        0.5, TipoElemental.PLANTA.multiplicadorContra(TipoElemental.FOGO), DELTA),
                () -> assertEquals(
                        0.5, TipoElemental.AGUA.multiplicadorContra(TipoElemental.PLANTA), DELTA));
    }

    @Test
    @DisplayName("Ataques do mesmo tipo causam dano neutro (x1)")
    void testMesmoTipoEhNeutro() {
        assertAll(
                () -> assertEquals(1.0, TipoElemental.FOGO.multiplicadorContra(TipoElemental.FOGO), DELTA),
                () -> assertEquals(1.0, TipoElemental.AGUA.multiplicadorContra(TipoElemental.AGUA), DELTA),
                () -> assertEquals(
                        1.0, TipoElemental.PLANTA.multiplicadorContra(TipoElemental.PLANTA), DELTA));
    }

    @Test
    @DisplayName("O tipo Normal não tem vantagem nem desvantagem contra nenhum tipo")
    void testTipoNormalEhNeutro() {
        assertAll(
                () -> assertEquals(
                        1.0, TipoElemental.NORMAL.multiplicadorContra(TipoElemental.FOGO), DELTA),
                () -> assertEquals(
                        1.0, TipoElemental.NORMAL.multiplicadorContra(TipoElemental.AGUA), DELTA),
                () -> assertEquals(
                        1.0, TipoElemental.NORMAL.multiplicadorContra(TipoElemental.PLANTA), DELTA),
                () -> assertEquals(
                        1.0, TipoElemental.FOGO.multiplicadorContra(TipoElemental.NORMAL), DELTA));
    }
}
