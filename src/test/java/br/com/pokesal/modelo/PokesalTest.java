package br.com.pokesal.modelo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.TipoElemental;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PokesalTest {
  private Pokesal pokesal;

  @BeforeEach
  void setUp() {
    pokesal = new Pokesal("Teste", TipoElemental.FOGO, 100, 50, 30, 55, List.of());
  }

  @Test
  @DisplayName("RN02: o HP nunca fica abaixo de zero")
  void testHpNaoFicaAbaixoDeZero() {
    pokesal.receberDano(1000);

    assertAll(() -> assertEquals(0, pokesal.getHpAtual()), () -> assertFalse(pokesal.estaVivo()));
  }

  @Test
  @DisplayName("Valor-limite de HP: dano igual ao HP zera; dano de HP-1 deixa 1 ponto")
  void testValoresLimiteDeDano() {
    Pokesal outro = new Pokesal("Outro", TipoElemental.AGUA, 100, 50, 30, 55, List.of());

    pokesal.receberDano(100);
    outro.receberDano(99);

    assertAll(
            () -> assertEquals(0, pokesal.getHpAtual()),
            () -> assertEquals(1, outro.getHpAtual()),
            () -> assertTrue(outro.estaVivo()));
  }

  @Test
  @DisplayName("Dano zero não altera o HP")
  void testDanoZeroNaoAlteraHp() {
    pokesal.receberDano(0);

    assertEquals(100, pokesal.getHpAtual());
  }

  @Test
  @DisplayName("Dano negativo é rejeitado")
  void testDanoNegativoEhInvalido() {
    assertThrows(IllegalArgumentException.class, () -> pokesal.receberDano(-1));
  }

  @Test
  @DisplayName("RN02: o HP nunca ultrapassa o máximo")
  void testHpNaoUltrapassaMaximo() {
    pokesal.receberDano(10);

    pokesal.recuperarVida(50);

    assertEquals(100, pokesal.getHpAtual());
  }

  @Test
  @DisplayName("Cura exata até o HP máximo é aceita")
  void testCuraExataAteOMaximo() {
    pokesal.receberDano(10);

    pokesal.recuperarVida(10);

    assertEquals(100, pokesal.getHpAtual());
  }

  @Test
  @DisplayName("Cura negativa é rejeitada")
  void testCuraNegativaEhInvalida() {
    assertThrows(IllegalArgumentException.class, () -> pokesal.recuperarVida(-1));
  }

  @Test
  @DisplayName("Pokésal derrotado não recupera vida")
  void testPokesalDerrotadoNaoRecuperaVida() {
    pokesal.receberDano(100);

    pokesal.recuperarVida(40);

    assertEquals(0, pokesal.getHpAtual());
  }

  @Test
  @DisplayName("HP máximo menor ou igual a zero é rejeitado")
  void testHpMaximoInvalido() {
    assertThrows(
            IllegalArgumentException.class,
            () -> new Pokesal("X", TipoElemental.FOGO, 0, 10, 10, 10, List.of()));
  }

  @Test
  @DisplayName("ATK, DEF e SPD negativos são rejeitados; zero é aceito")
  void testValoresLimiteDeAtributos() {
    assertAll(
            () -> assertThrows(IllegalArgumentException.class,
                    () -> new Pokesal("X", TipoElemental.FOGO, 10, -1, 10, 10, List.of())),
            () -> assertThrows(IllegalArgumentException.class,
                    () -> new Pokesal("X", TipoElemental.FOGO, 10, 10, -1, 10, List.of())),
            () -> assertThrows(IllegalArgumentException.class,
                    () -> new Pokesal("X", TipoElemental.FOGO, 10, 10, 10, -1, List.of())),
            () -> assertDoesNotThrow(
                    () -> new Pokesal("X", TipoElemental.FOGO, 10, 0, 0, 0, List.of())));
  }

  @Test
  @DisplayName("Efeitos de status não se acumulam")
  void testStatusNaoAcumula() {
    boolean primeiro = pokesal.aplicarStatus(EfeitoStatus.QUEIMADO);
    boolean segundo = pokesal.aplicarStatus(EfeitoStatus.PARALISADO);

    assertAll(
            () -> assertTrue(primeiro),
            () -> assertFalse(segundo),
            () -> assertEquals(EfeitoStatus.QUEIMADO, pokesal.getEfeitoStatus()));
  }

  @Test
  @DisplayName("Aplicar NENHUM não conta como efeito")
  void testAplicarNenhumNaoFazNada() {
    assertAll(
            () -> assertFalse(pokesal.aplicarStatus(EfeitoStatus.NENHUM)),
            () -> assertEquals(EfeitoStatus.NENHUM, pokesal.getEfeitoStatus()));
  }

  @Test
  @DisplayName("O efeito termina após a duração e permite um novo efeito")
  void testStatusTerminaAposADuracao() {
    pokesal.aplicarStatus(EfeitoStatus.QUEIMADO);

    pokesal.avancarTurnoStatus();
    int restantes = pokesal.getTurnosStatus();
    pokesal.avancarTurnoStatus();

    assertAll(
            () -> assertEquals(1, restantes),
            () -> assertEquals(EfeitoStatus.NENHUM, pokesal.getEfeitoStatus()),
            () -> assertTrue(pokesal.aplicarStatus(EfeitoStatus.PARALISADO)));
  }

  @Test
  @DisplayName("Avançar turno sem efeito não faz nada")
  void testAvancarTurnoSemStatus() {
    pokesal.avancarTurnoStatus();

    assertEquals(0, pokesal.getTurnosStatus());
  }

  @Test
  @DisplayName("Queimadura reduz o ATK efetivo em 5")
  void testQueimaduraReduzAtaque() {
    pokesal.aplicarStatus(EfeitoStatus.QUEIMADO);

    assertEquals(45, pokesal.getAtaqueEfetivo());
  }

  @Test
  @DisplayName("Valor-limite de ATK: o ATK efetivo nunca fica abaixo de zero")
  void testAtaqueEfetivoNaoFicaNegativo() {
    Pokesal fraco = new Pokesal("Fraco", TipoElemental.FOGO, 10, 3, 0, 3, List.of());
    fraco.aplicarStatus(EfeitoStatus.QUEIMADO);

    assertEquals(0, fraco.getAtaqueEfetivo());
  }

  @Test
  @DisplayName("Paralisia reduz a velocidade efetiva em 10, sem ficar abaixo de zero")
  void testParalisiaReduzVelocidade() {
    Pokesal lento = new Pokesal("Lento", TipoElemental.AGUA, 10, 10, 10, 4, List.of());
    pokesal.aplicarStatus(EfeitoStatus.PARALISADO);
    lento.aplicarStatus(EfeitoStatus.PARALISADO);

    assertAll(
            () -> assertEquals(45, pokesal.getVelocidadeEfetiva()),
            () -> assertEquals(0, lento.getVelocidadeEfetiva()));
  }

  @Test
  @DisplayName("Restaurar devolve o HP máximo e remove o status")
  void testRestaurar() {
    pokesal.receberDano(70);
    pokesal.aplicarStatus(EfeitoStatus.ENVENENADO);

    pokesal.restaurar();

    assertAll(
            () -> assertEquals(100, pokesal.getHpAtual()),
            () -> assertEquals(EfeitoStatus.NENHUM, pokesal.getEfeitoStatus()));
  }

  @Test
  @DisplayName("A lista de ataques não pode ser modificada")
  void testListaDeAtaquesImutavel() {
    List<Ataque> ataques = pokesal.getAtaques();
    Ataque extra = new Ataque("X", TipoElemental.NORMAL, 1, 100, EfeitoStatus.NENHUM);

    assertThrows(UnsupportedOperationException.class, () -> ataques.add(extra));
  }

  @Test
  @DisplayName("Os getters devolvem os dados informados na criação")
  void testGetters() {
    assertAll(
            () -> assertEquals("Teste", pokesal.getNome()),
            () -> assertEquals(TipoElemental.FOGO, pokesal.getTipo()),
            () -> assertEquals(100, pokesal.getHpMaximo()),
            () -> assertEquals(30, pokesal.getDefesa()));
  }
}
