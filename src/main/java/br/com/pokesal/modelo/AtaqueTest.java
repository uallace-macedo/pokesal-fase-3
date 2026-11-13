package modelo;

import enums.EfeitoStatus;
import enums.TipoElemental;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/** Testes unitários para a classe Ataque */
class AtaqueTest {

    @Test
    void deveCriarAtaqueValido() {
        Ataque ataque = new Ataque("Folha Navalha", TipoElemental.PLANTA, 25, EfeitoStatus.NENHUM);

        assertEquals("Folha Navalha", ataque.getNome());
        assertEquals(TipoElemental.PLANTA, ataque.getTipo());
        assertEquals(25, ataque.getDanoBase());
        assertEquals(EfeitoStatus.NENHUM, ataque.getEfeitoStatus());
    }

    @Test
    void deveRejeitarNomeNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Ataque(null, TipoElemental.PLANTA, 10, EfeitoStatus.NENHUM));
    }

    @Test
    void deveRejeitarNomeVazio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Ataque(" ", TipoElemental.PLANTA, 10, EfeitoStatus.NENHUM));
    }

    @Test
    void deveRejeitarTipoNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Ataque("Investida", null, 10, EfeitoStatus.NENHUM));
    }

    @Test
    void deveRejeitarEfeitoNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Ataque("Investida", TipoElemental.PLANTA, 10, null));
    }

    @Test
    void deveRejeitarDanoNegativo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Ataque("Investida", TipoElemental.PLANTA, -1, EfeitoStatus.NENHUM));
    }

    @Test
    void deveAceitarDanoZero() {
        Ataque ataque = new Ataque("Investida", TipoElemental.PLANTA, 0, EfeitoStatus.NENHUM);

        assertEquals(0, ataque.getDanoBase());
    }
}

