package modelo;

import enums.EfeitoStatus;
import enums.TipoElemental;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

class PokesalTest {

    private Pokesal criarPokesal(int hp, int ataque, int defesa, int velocidade) {

        List<Ataque> ataques =
                List.of(
                        new Ataque("Investida", TipoElemental.FOGO, 15, EfeitoStatus.NENHUM, false),
                        new Ataque("Brasas", TipoElemental.FOGO, 25, EfeitoStatus.NENHUM),
                        new Ataque("Chama", TipoElemental.FOGO, 15, EfeitoStatus.QUEIMADO));

        return new Pokesal("CharSal", TipoElemental.FOGO, hp, ataque, defesa, velocidade, ataques);
    }

    @Test
    void hpNaoDeveUltrapassarValorMaximo() {
        Pokesal pokesal = criarPokesal(100, 50, 30, 40);

        pokesal.receberDano(20);
        pokesal.recuperarVida(Integer.MAX_VALUE);

        assertEquals(100, pokesal.getHpAtual());
    }

    @Test
    void hpNaoDeveFicarNegativo() {
        Pokesal pokesal = criarPokesal(100, 50, 30, 40);

        pokesal.receberDano(Integer.MAX_VALUE);

        assertEquals(0, pokesal.getHpAtual());
        assertFalse(pokesal.estaVivo());
    }

    @Test
    void recuperacaoNaoDeveProvocarOverflow() {
        Pokesal pokesal = criarPokesal(Integer.MAX_VALUE, 50, 30, 40);

        pokesal.receberDano(1);
        pokesal.recuperarVida(Integer.MAX_VALUE);

        assertEquals(Integer.MAX_VALUE, pokesal.getHpAtual());
    }

    @Test
    void queimaduraDeveReduzirAtaqueEmCinco() {
        Pokesal pokesal = criarPokesal(100, 50, 30, 40);

        pokesal.aplicarStatus(EfeitoStatus.QUEIMADO, 2);

        assertEquals(45, pokesal.getAtaqueEfetivo());
        assertEquals(50, pokesal.getAtaque());
    }

    @Test
    void paralisiaDeveReduzirVelocidadeEmDez() {
        Pokesal pokesal = criarPokesal(100, 50, 30, 40);

        pokesal.aplicarStatus(EfeitoStatus.PARALISADO, 2);

        assertEquals(30, pokesal.getVelocidadeEfetiva());
        assertEquals(40, pokesal.getVelocidade());
    }

    @Test
    void queimaduraDeveDeixarAtaqueNoMinimoZero() {
        Pokesal pokesal = criarPokesal(100, 3, 30, 40);

        pokesal.aplicarStatus(EfeitoStatus.QUEIMADO, 2);

        assertEquals(0, pokesal.getAtaqueEfetivo());
    }

    @Test
    void paralisiaDeveDeixarVelocidadeNoMinimoZero() {
        Pokesal pokesal = criarPokesal(100, 50, 30, 5);

        pokesal.aplicarStatus(EfeitoStatus.PARALISADO, 2);

        assertEquals(0, pokesal.getVelocidadeEfetiva());
    }

    @Test
    void atributosDevemVoltarAoNormalQuandoStatusTerminar() {
        Pokesal pokesal = criarPokesal(100, 50, 30, 40);

        pokesal.aplicarStatus(EfeitoStatus.QUEIMADO, 1);
        assertEquals(45, pokesal.getAtaqueEfetivo());

        pokesal.avancarTurnoStatus();

        assertEquals(EfeitoStatus.NENHUM, pokesal.getEfeitoStatus());
        assertEquals(50, pokesal.getAtaqueEfetivo());
        assertEquals(40, pokesal.getVelocidadeEfetiva());
    }

    @Test
    void deveImpedirAcumuloDeStatus() {
        Pokesal pokesal = criarPokesal(100, 50, 30, 40);

        assertTrue(pokesal.aplicarStatus(EfeitoStatus.QUEIMADO, 2));
        assertFalse(pokesal.aplicarStatus(EfeitoStatus.ENVENENADO, 3));

        assertEquals(EfeitoStatus.QUEIMADO, pokesal.getEfeitoStatus());
    }
}
