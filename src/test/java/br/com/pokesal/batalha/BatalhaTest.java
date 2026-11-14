package br.com.pokesal.batalha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.Terreno;
import br.com.pokesal.enums.TipoElemental;
import br.com.pokesal.modelo.Ataque;
import br.com.pokesal.modelo.Pokesal;
import br.com.pokesal.modelo.Treinador;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BatalhaTest {

  private Batalha batalha;
  private Pokesal planta;
  private Pokesal fogo;
  private Pokesal agua;

  @BeforeEach
  void setUp() {
    batalha = new Batalha(Terreno.CANTEIRO_CENTRAL, semCritico());
    planta = criarPokesal("BulbaSal", TipoElemental.PLANTA, 110, 45, 50, 40);
    fogo = criarPokesal("CharSal", TipoElemental.FOGO, 95, 55, 35, 55);
    agua = criarPokesal("SquirtSal", TipoElemental.AGUA, 105, 40, 55, 35);
  }

  private Pokesal criarPokesal(
      String nome, TipoElemental tipo, int hp, int ataque, int defesa, int velocidade) {

    List<Ataque> ataques =
        List.of(
            new Ataque("Investida", tipo, 15, EfeitoStatus.NENHUM, false),
            new Ataque("Ataque Elemental", tipo, 25, EfeitoStatus.NENHUM),
            new Ataque("Ataque de Status", tipo, 10, EfeitoStatus.ENVENENADO));

    return new Pokesal(nome, tipo, hp, ataque, defesa, velocidade, ataques);
  }

  private Random semCritico() {
    return new Random(1) {
      @Override
      public int nextInt(int bound) {
        return 99;
      }
    };
  }

  private Random comCritico() {
    return new Random(1) {
      @Override
      public int nextInt(int bound) {
        return 0;
      }
    };
  }

  private Ataque criarAtaque(String nome, int dano, TipoElemental tipo, EfeitoStatus efeito) {
    return new Ataque(nome, tipo, dano, efeito);
  }

  @Test
  void deveRetornarTerrenoDaBatalha() {
    assertEquals(Terreno.CANTEIRO_CENTRAL, batalha.getTerreno());
  }

  @Test
  void deveRejeitarTerrenoNulo() {
    assertThrows(NullPointerException.class, () -> new Batalha(null));
  }

  @Test
  void deveRejeitarGeradorAleatorioNulo() {
    assertThrows(NullPointerException.class, () -> new Batalha(Terreno.ASFALTO_QUENTE, null));
  }

  @Test
  void deveRetornarMultiplicadorNeutroParaTiposIguais() {
    assertEquals(
        1.0, batalha.calcularMultiplicadorElemental(TipoElemental.FOGO, TipoElemental.FOGO));
  }

  @Test
  void deveCalcularTodasAsVantagensElementais() {
    assertEquals(
        2.0, batalha.calcularMultiplicadorElemental(TipoElemental.FOGO, TipoElemental.PLANTA));

    assertEquals(
        2.0, batalha.calcularMultiplicadorElemental(TipoElemental.PLANTA, TipoElemental.AGUA));

    assertEquals(
        2.0, batalha.calcularMultiplicadorElemental(TipoElemental.AGUA, TipoElemental.FOGO));
  }

  @Test
  void deveCalcularTodasAsDesvantagensElementais() {
    assertEquals(
        0.5, batalha.calcularMultiplicadorElemental(TipoElemental.PLANTA, TipoElemental.FOGO));

    assertEquals(
        0.5, batalha.calcularMultiplicadorElemental(TipoElemental.AGUA, TipoElemental.PLANTA));

    assertEquals(
        0.5, batalha.calcularMultiplicadorElemental(TipoElemental.FOGO, TipoElemental.AGUA));
  }

  @Test
  void deveRejeitarTipoDeAtaqueNuloNoCalculoElemental() {
    assertThrows(
        NullPointerException.class,
        () -> batalha.calcularMultiplicadorElemental(null, TipoElemental.FOGO));
  }

  @Test
  void deveRejeitarTipoDeDefesaNuloNoCalculoElemental() {
    assertThrows(
        NullPointerException.class,
        () -> batalha.calcularMultiplicadorElemental(TipoElemental.FOGO, null));
  }

  @Test
  void deveAplicarBonusDeFogoNoAsfaltoQuente() {
    Batalha batalhaAsfalto = new Batalha(Terreno.ASFALTO_QUENTE);

    assertEquals(1.15, batalhaAsfalto.calcularMultiplicadorTerreno(TipoElemental.FOGO));
  }

  @Test
  void deveAplicarBonusDeAguaNaPocaDagua() {
    Batalha batalhaPoca = new Batalha(Terreno.POCA_DAGUA);

    assertEquals(1.10, batalhaPoca.calcularMultiplicadorTerreno(TipoElemental.AGUA));
  }

  @Test
  void naoDeveAplicarBonusQuandoTipoNaoCombinaComTerreno() {
    assertEquals(
        1.0, new Batalha(Terreno.ASFALTO_QUENTE).calcularMultiplicadorTerreno(TipoElemental.AGUA));

    assertEquals(
        1.0, new Batalha(Terreno.POCA_DAGUA).calcularMultiplicadorTerreno(TipoElemental.FOGO));

    assertEquals(
        1.0,
        new Batalha(Terreno.CANTEIRO_CENTRAL).calcularMultiplicadorTerreno(TipoElemental.PLANTA));
  }

  @Test
  void deveRejeitarTipoNuloNoCalculoDoTerreno() {
    assertThrows(NullPointerException.class, () -> batalha.calcularMultiplicadorTerreno(null));
  }

  @Test
  void deveCalcularDanoMinimoDeUm() {
    Pokesal atacante = criarPokesal("Fraco", TipoElemental.FOGO, 50, 1, 1, 10);
    Pokesal defensor = criarPokesal("Forte", TipoElemental.AGUA, 100, 1, 100, 10);

    Ataque ataque = criarAtaque("Ataque Fraco", 1, TipoElemental.FOGO, EfeitoStatus.NENHUM);

    assertEquals(
        1,
        new Batalha(Terreno.CANTEIRO_CENTRAL, semCritico())
            .calcularDano(ataque, atacante, defensor));
  }

  @Test
  void deveCalcularDanoComVantagemElemental() {
    Ataque ataque = criarAtaque("Brasas", 25, TipoElemental.FOGO, EfeitoStatus.NENHUM);

    assertEquals(
        60, new Batalha(Terreno.CANTEIRO_CENTRAL, semCritico()).calcularDano(ataque, fogo, planta));
  }

  @Test
  void deveCalcularDanoCritico() {
    Ataque ataque = criarAtaque("Brasas", 10, TipoElemental.FOGO, EfeitoStatus.NENHUM);

    Pokesal atacante = criarPokesal("Atacante", TipoElemental.FOGO, 100, 10, 10, 10);

    Pokesal defensor = criarPokesal("Defensor", TipoElemental.PLANTA, 100, 10, 10, 10);

    assertEquals(
        30,
        new Batalha(Terreno.CANTEIRO_CENTRAL, comCritico())
            .calcularDano(ataque, atacante, defensor));
  }

  @Test
  void investidaNaoDeveReceberMultiplicadoresElementaisOuDeTerreno() {
    Pokesal atacante = criarPokesal("CharSal", TipoElemental.FOGO, 95, 55, 35, 55);

    Pokesal defensor = criarPokesal("BulbaSal", TipoElemental.PLANTA, 110, 45, 50, 40);

    Ataque investida = atacante.getAtaques().get(0);

    assertEquals(
        20,
        new Batalha(Terreno.ASFALTO_QUENTE, semCritico())
            .calcularDano(investida, atacante, defensor));
  }

  @Test
  void deveRejeitarAtaqueNuloNoCalculoDeDano() {
    assertThrows(NullPointerException.class, () -> batalha.calcularDano(null, fogo, planta));
  }

  @Test
  void deveRejeitarAtacanteNuloNoCalculoDeDano() {
    Ataque ataque = criarAtaque("Investida", 15, TipoElemental.FOGO, EfeitoStatus.NENHUM);

    assertThrows(NullPointerException.class, () -> batalha.calcularDano(ataque, null, planta));
  }

  @Test
  void deveRejeitarDefensorNuloNoCalculoDeDano() {
    Ataque ataque = criarAtaque("Investida", 15, TipoElemental.FOGO, EfeitoStatus.NENHUM);

    assertThrows(NullPointerException.class, () -> batalha.calcularDano(ataque, fogo, null));
  }

  @Test
  void jogadorMaisRapidoDeveAtacarPrimeiro() {
    Treinador jogador1 = new Treinador("Jogador 1", fogo);
    Treinador jogador2 = new Treinador("Jogador 2", planta);

    assertTrue(batalha.jogador1AtacaPrimeiro(jogador1, jogador2));
  }

  @Test
  void jogadorMaisLentoNaoDeveAtacarPrimeiro() {
    Treinador jogador1 = new Treinador("Jogador 1", planta);
    Treinador jogador2 = new Treinador("Jogador 2", fogo);

    assertFalse(batalha.jogador1AtacaPrimeiro(jogador1, jogador2));
  }

  @Test
  void jogador1DeveAtacarPrimeiroEmCasoDeEmpate() {
    Pokesal pokesal1 = criarPokesal("Um", TipoElemental.FOGO, 100, 10, 10, 40);

    Pokesal pokesal2 = criarPokesal("Dois", TipoElemental.AGUA, 100, 10, 10, 40);

    assertTrue(
        batalha.jogador1AtacaPrimeiro(
            new Treinador("Jogador 1", pokesal1), new Treinador("Jogador 2", pokesal2)));
  }

  @Test
  void jogadorParalisadoDevePerderVelocidadeNaIniciativa() {
    Pokesal pokesal1 = criarPokesal("Paralisado", TipoElemental.FOGO, 100, 10, 10, 50);

    Pokesal pokesal2 = criarPokesal("Normal", TipoElemental.AGUA, 100, 10, 10, 45);

    pokesal1.aplicarStatus(EfeitoStatus.PARALISADO, 2);

    assertFalse(
        batalha.jogador1AtacaPrimeiro(
            new Treinador("Jogador 1", pokesal1), new Treinador("Jogador 2", pokesal2)));
  }

  @Test
  void deveRejeitarTreinadoresNulosNaVerificacaoDeIniciativa() {
    Treinador treinador = new Treinador("Jogador", fogo);

    assertThrows(NullPointerException.class, () -> batalha.jogador1AtacaPrimeiro(null, treinador));

    assertThrows(NullPointerException.class, () -> batalha.jogador1AtacaPrimeiro(treinador, null));
  }

  @Test
  void deveAplicarStatusQueimado() {
    Ataque ataque = criarAtaque("Chama", 1, TipoElemental.FOGO, EfeitoStatus.QUEIMADO);

    batalha.executarAtaque(ataque, fogo, planta);

    assertEquals(EfeitoStatus.QUEIMADO, planta.getEfeitoStatus());
  }

  @Test
  void deveAplicarStatusEnvenenado() {
    Ataque ataque = criarAtaque("Veneno", 1, TipoElemental.FOGO, EfeitoStatus.ENVENENADO);

    batalha.executarAtaque(ataque, fogo, planta);

    assertEquals(EfeitoStatus.ENVENENADO, planta.getEfeitoStatus());
  }

  @Test
  void deveAplicarStatusParalisado() {
    Ataque ataque = criarAtaque("Paralisia", 1, TipoElemental.FOGO, EfeitoStatus.PARALISADO);

    batalha.executarAtaque(ataque, fogo, planta);

    assertEquals(EfeitoStatus.PARALISADO, planta.getEfeitoStatus());
  }

  @Test
  void ataqueSemStatusNaoDeveAplicarEfeito() {
    Ataque ataque = criarAtaque("Investida", 1, TipoElemental.FOGO, EfeitoStatus.NENHUM);

    batalha.executarAtaque(ataque, fogo, planta);

    assertEquals(EfeitoStatus.NENHUM, planta.getEfeitoStatus());
  }

  @Test
  void naoDeveAplicarStatusSeDefensorForDerrotado() {
    Pokesal defensor = criarPokesal("Fraco", TipoElemental.PLANTA, 1, 1, 1, 1);

    Ataque ataque = criarAtaque("Veneno", 100, TipoElemental.FOGO, EfeitoStatus.ENVENENADO);

    batalha.executarAtaque(ataque, fogo, defensor);

    assertFalse(defensor.estaVivo());
    assertEquals(EfeitoStatus.NENHUM, defensor.getEfeitoStatus());
  }

  @Test
  void deveRejeitarPokesalNuloAoAplicarEfeitoDoTerreno() {
    assertThrows(NullPointerException.class, () -> batalha.aplicarEfeitoTerrenoNoFimDoTurno(null));
  }

  @Test
  void naoDeveAplicarEfeitoDoTerrenoEmPokesalDerrotado() {
    Pokesal derrotado = criarPokesal("Derrotado", TipoElemental.PLANTA, 100, 10, 10, 10);

    derrotado.receberDano(100);

    batalha.aplicarEfeitoTerrenoNoFimDoTurno(derrotado);

    assertEquals(0, derrotado.getHpAtual());
  }

  @Test
  void deveRecuperarVidaDePlantaNoCanteiroCentral() {
    Pokesal pokesal = criarPokesal("Planta", TipoElemental.PLANTA, 100, 10, 10, 10);

    pokesal.receberDano(20);

    batalha.aplicarEfeitoTerrenoNoFimDoTurno(pokesal);

    assertEquals(85, pokesal.getHpAtual());
  }

  @Test
  void naoDeveRecuperarVidaDeOutroTipoNoCanteiroCentral() {
    Pokesal pokesal = criarPokesal("Fogo", TipoElemental.FOGO, 100, 10, 10, 10);

    pokesal.receberDano(20);

    batalha.aplicarEfeitoTerrenoNoFimDoTurno(pokesal);

    assertEquals(80, pokesal.getHpAtual());
  }

  @Test
  void naoDeveRecuperarVidaForaDoCanteiroCentral() {
    Pokesal pokesal = criarPokesal("Planta", TipoElemental.PLANTA, 100, 10, 10, 10);

    pokesal.receberDano(20);

    new Batalha(Terreno.POCA_DAGUA).aplicarEfeitoTerrenoNoFimDoTurno(pokesal);

    assertEquals(80, pokesal.getHpAtual());
  }

  @Test
  void deveAplicarDanoPeriodicoDeQueimadura() {
    Pokesal pokesal = criarPokesal("Queimado", TipoElemental.PLANTA, 100, 10, 10, 10);

    pokesal.aplicarStatus(EfeitoStatus.QUEIMADO, 2);

    batalha.processarStatusNoFimDoTurno(pokesal);

    assertEquals(95, pokesal.getHpAtual());
    assertEquals(1, pokesal.getTurnosStatus());
  }

  @Test
  void deveAplicarDanoPeriodicoDeVeneno() {
    Pokesal pokesal = criarPokesal("Envenenado", TipoElemental.PLANTA, 100, 10, 10, 10);

    pokesal.aplicarStatus(EfeitoStatus.ENVENENADO, 3);

    batalha.processarStatusNoFimDoTurno(pokesal);

    assertEquals(95, pokesal.getHpAtual());
    assertEquals(2, pokesal.getTurnosStatus());
  }

  @Test
  void paralisiaNaoDeveCausarDanoPeriodico() {
    Pokesal pokesal = criarPokesal("Paralisado", TipoElemental.PLANTA, 100, 10, 10, 10);

    pokesal.aplicarStatus(EfeitoStatus.PARALISADO, 2);

    batalha.processarStatusNoFimDoTurno(pokesal);

    assertEquals(100, pokesal.getHpAtual());
    assertEquals(1, pokesal.getTurnosStatus());
  }

  @Test
  void nenhumStatusNaoDeveCausarDanoPeriodico() {
    Pokesal pokesal = criarPokesal("Normal", TipoElemental.PLANTA, 100, 10, 10, 10);

    batalha.processarStatusNoFimDoTurno(pokesal);

    assertEquals(100, pokesal.getHpAtual());
    assertEquals(0, pokesal.getTurnosStatus());
  }

  @Test
  void naoDeveProcessarStatusDePokesalDerrotado() {
    Pokesal pokesal = criarPokesal("Derrotado", TipoElemental.PLANTA, 100, 10, 10, 10);

    pokesal.aplicarStatus(EfeitoStatus.ENVENENADO, 3);
    pokesal.receberDano(100);

    batalha.processarStatusNoFimDoTurno(pokesal);

    assertEquals(0, pokesal.getHpAtual());
    assertEquals(3, pokesal.getTurnosStatus());
  }

  @Test
  void deveRejeitarPokesalNuloAoProcessarStatus() {
    assertThrows(NullPointerException.class, () -> batalha.processarStatusNoFimDoTurno(null));
  }

  @Test
  void deveVerificarVencedorQuandoUmPokesalForDerrotado() {
    Treinador jogador1 = new Treinador("Jogador 1", fogo);
    Treinador jogador2 = new Treinador("Jogador 2", planta);

    Batalha batalhaCompleta =
        new Batalha(jogador1, jogador2, Terreno.CANTEIRO_CENTRAL, semCritico());

    planta.receberDano(planta.getHpMaximo());

    assertTrue(batalhaCompleta.verificarVencedor().isPresent());
    assertEquals(jogador1, batalhaCompleta.verificarVencedor().orElseThrow());
  }
}
