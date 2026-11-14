package br.com.pokesal.console;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.pokesal.batalha.Batalha;
import br.com.pokesal.catalogo.CatalogoPokesal;
import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.Terreno;
import br.com.pokesal.enums.TipoItem;
import br.com.pokesal.modelo.Pokesal;
import br.com.pokesal.modelo.Treinador;
import java.util.Random;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

class InterfaceConsoleTest {

  @Test
  void deveCriarInterfaceComConstrutorPadrao() {
    assertDoesNotThrow(() -> new InterfaceConsole());
  }

  @Test
  void deveCriarInterfaceComDependenciasValidas() {
    assertDoesNotThrow(() -> new InterfaceConsole(new Scanner(""), new Random(42)));
  }

  @Test
  void deveRejeitarScannerNulo() {
    assertThrows(IllegalArgumentException.class, () -> new InterfaceConsole(null, new Random(42)));
  }

  @Test
  void deveRejeitarRandomNulo() {
    assertThrows(IllegalArgumentException.class, () -> new InterfaceConsole(new Scanner(""), null));
  }

  @Test
  void deveSolicitarNomeNovamenteQuandoEstiverVazio() throws Exception {
    InterfaceConsole console = criarConsole("\nAsh\n1\n");

    Treinador treinador =
        (Treinador) invocar(console, "selecionarTreinador", new Class<?>[] {int.class}, 1);

    assertEquals("Ash", treinador.getNome());
  }

  @Test
  void deveRejeitarOpcaoInvalidaNaSelecaoDePokesal() throws Exception {
    InterfaceConsole console = criarConsole("Ash\n9\n1\n");

    Treinador treinador =
        (Treinador) invocar(console, "selecionarTreinador", new Class<?>[] {int.class}, 1);

    assertEquals("Ash", treinador.getNome());
    assertEquals("BulbaSal", treinador.getPokesal().getNome());
  }

  @Test
  void deveExibirEstadoDosTreinadores() throws Exception {
    InterfaceConsole console = criarConsole("");
    Treinador jogador1 = criarTreinador("Ash", 0);
    Treinador jogador2 = criarTreinador("Misty", 1);

    assertDoesNotThrow(
        () ->
            invocar(
                console,
                "exibirEstado",
                new Class<?>[] {Treinador.class, Treinador.class},
                jogador1,
                jogador2));
  }

  @Test
  void deveIgnorarTurnoQuandoPokesalEstiverDerrotado() throws Exception {
    InterfaceConsole console = criarConsole("");
    Treinador jogador1 = criarTreinador("Ash", 0);
    Treinador jogador2 = criarTreinador("Misty", 1);

    jogador1.getPokesal().receberDano(Integer.MAX_VALUE);

    Batalha batalha = new Batalha(Terreno.ASFALTO_QUENTE, new Random(42));

    assertDoesNotThrow(
        () ->
            invocar(
                console,
                "executarTurno",
                new Class<?>[] {Batalha.class, Treinador.class, Treinador.class},
                batalha,
                jogador1,
                jogador2));
  }

  @Test
  void deveIgnorarTurnoQuandoAdversarioEstiverDerrotado() throws Exception {
    InterfaceConsole console = criarConsole("");
    Treinador jogador1 = criarTreinador("Ash", 0);
    Treinador jogador2 = criarTreinador("Misty", 1);

    jogador2.getPokesal().receberDano(Integer.MAX_VALUE);

    Batalha batalha = new Batalha(Terreno.ASFALTO_QUENTE, new Random(42));

    assertDoesNotThrow(
        () ->
            invocar(
                console,
                "executarTurno",
                new Class<?>[] {Batalha.class, Treinador.class, Treinador.class},
                batalha,
                jogador1,
                jogador2));
  }

  @Test
  void devePermitirVoltarDoMenuDeAtaques() throws Exception {
    InterfaceConsole console = criarConsole("0\n");
    Treinador atacante = criarTreinador("Ash", 0);
    Treinador defensor = criarTreinador("Misty", 1);

    Batalha batalha = new Batalha(Terreno.ASFALTO_QUENTE, new Random(42));

    assertDoesNotThrow(
        () ->
            invocar(
                console,
                "executarAtaque",
                new Class<?>[] {Batalha.class, Treinador.class, Treinador.class},
                batalha,
                atacante,
                defensor));
  }

  @Test
  void deveExecutarAtaqueSemEfeitoDeStatus() throws Exception {
    InterfaceConsole console = criarConsole("1\n");
    Treinador atacante = criarTreinador("Ash", 0);
    Treinador defensor = criarTreinador("Misty", 1);

    Batalha batalha = new Batalha(Terreno.ASFALTO_QUENTE, new Random(42));

    int hpInicial = defensor.getPokesal().getHpAtual();

    invocar(
        console,
        "executarAtaque",
        new Class<?>[] {Batalha.class, Treinador.class, Treinador.class},
        batalha,
        atacante,
        defensor);

    assertTrue(defensor.getPokesal().getHpAtual() < hpInicial);
  }

  @Test
  void deveUsarPocao() throws Exception {
    InterfaceConsole console = criarConsole("1\n");
    Treinador treinador = criarTreinador("Ash", 0);

    treinador.getPokesal().receberDano(30);

    invocar(console, "usarItem", new Class<?>[] {Treinador.class}, treinador);

    assertEquals(0, treinador.buscarItem(TipoItem.POCAO).getQuantidade());

    assertEquals(100, treinador.getPokesal().getHpAtual());
  }

  @Test
  void deveUsarSuperPocao() throws Exception {
    InterfaceConsole console = criarConsole("2\n");
    Treinador treinador = criarTreinador("Ash", 0);

    treinador.getPokesal().receberDano(50);

    invocar(console, "usarItem", new Class<?>[] {Treinador.class}, treinador);

    assertEquals(0, treinador.buscarItem(TipoItem.SUPER_POCAO).getQuantidade());

    assertEquals(100, treinador.getPokesal().getHpAtual());
  }

  @Test
  void deveCancelarUsoDeItem() throws Exception {
    InterfaceConsole console = criarConsole("0\n");
    Treinador treinador = criarTreinador("Ash", 0);

    invocar(console, "usarItem", new Class<?>[] {Treinador.class}, treinador);

    assertEquals(1, treinador.buscarItem(TipoItem.POCAO).getQuantidade());
  }

  @Test
  void deveRejeitarPocaoQuandoHpEstiverCheio() throws Exception {
    InterfaceConsole console = criarConsole("1\n");
    Treinador treinador = criarTreinador("Ash", 0);

    invocar(console, "usarItem", new Class<?>[] {Treinador.class}, treinador);

    assertEquals(treinador.getPokesal().getHpMaximo(), treinador.getPokesal().getHpAtual());

    assertEquals(1, treinador.buscarItem(TipoItem.POCAO).getQuantidade());
  }

  @Test
  void deveRejeitarAntidotoSemEnvenenamento() throws Exception {
    InterfaceConsole console = criarConsole("3\n");
    Treinador treinador = criarTreinador("Ash", 0);

    invocar(console, "usarItem", new Class<?>[] {Treinador.class}, treinador);

    assertEquals(1, treinador.buscarItem(TipoItem.ANTIDOTO).getQuantidade());
  }

  @Test
  void deveUsarAntidotoContraEnvenenamento() throws Exception {
    InterfaceConsole console = criarConsole("3\n");
    Treinador treinador = criarTreinador("Ash", 0);

    treinador.getPokesal().aplicarStatus(EfeitoStatus.ENVENENADO, 3);

    invocar(console, "usarItem", new Class<?>[] {Treinador.class}, treinador);

    assertEquals(EfeitoStatus.NENHUM, treinador.getPokesal().getEfeitoStatus());
  }

  @Test
  void deveInformarQuandoItemJaFoiUtilizado() throws Exception {
    InterfaceConsole console = criarConsole("1\n1\n");
    Treinador treinador = criarTreinador("Ash", 0);

    treinador.getPokesal().receberDano(30);

    invocar(console, "usarItem", new Class<?>[] {Treinador.class}, treinador);

    invocar(console, "usarItem", new Class<?>[] {Treinador.class}, treinador);

    assertEquals(0, treinador.buscarItem(TipoItem.POCAO).getQuantidade());
  }

  @Test
  void deveInformarLimiteDeItensAtingido() throws Exception {
    InterfaceConsole console = criarConsole("1\n2\n");
    Treinador treinador = criarTreinador("Ash", 0);

    treinador.getPokesal().receberDano(80);

    invocar(console, "usarItem", new Class<?>[] {Treinador.class}, treinador);

    invocar(console, "usarItem", new Class<?>[] {Treinador.class}, treinador);

    invocar(console, "usarItem", new Class<?>[] {Treinador.class}, treinador);

    assertFalse(treinador.podeUsarItem());
  }

  @Test
  void deveProcessarDanoDeQueimaduraNoFimDoTurno() throws Exception {
    InterfaceConsole console = criarConsole("");
    Pokesal pokesal = CatalogoPokesal.criarIniciais().get(0);

    pokesal.aplicarStatus(EfeitoStatus.QUEIMADO, 2);

    Batalha batalha = new Batalha(Terreno.ASFALTO_QUENTE, new Random(42));

    int hpInicial = pokesal.getHpAtual();

    invocar(
        console,
        "processarFimDoTurno",
        new Class<?>[] {Batalha.class, Pokesal.class},
        batalha,
        pokesal);

    assertTrue(pokesal.getHpAtual() < hpInicial);
  }

  @Test
  void deveProcessarDanoDeEnvenenamentoNoFimDoTurno() throws Exception {
    InterfaceConsole console = criarConsole("");
    Pokesal pokesal = CatalogoPokesal.criarIniciais().get(0);

    pokesal.aplicarStatus(EfeitoStatus.ENVENENADO, 3);

    Batalha batalha = new Batalha(Terreno.ASFALTO_QUENTE, new Random(42));

    int hpInicial = pokesal.getHpAtual();

    invocar(
        console,
        "processarFimDoTurno",
        new Class<?>[] {Batalha.class, Pokesal.class},
        batalha,
        pokesal);

    assertTrue(pokesal.getHpAtual() < hpInicial);
  }

  @Test
  void deveAvancarTurnoDeStatusSemDano() throws Exception {
    InterfaceConsole console = criarConsole("");
    Pokesal pokesal = CatalogoPokesal.criarIniciais().get(0);

    pokesal.aplicarStatus(EfeitoStatus.PARALISADO, 2);

    Batalha batalha = new Batalha(Terreno.ASFALTO_QUENTE, new Random(42));

    assertDoesNotThrow(
        () ->
            invocar(
                console,
                "processarFimDoTurno",
                new Class<?>[] {Batalha.class, Pokesal.class},
                batalha,
                pokesal));
  }

  @Test
  void deveIgnorarFimDoTurnoDePokesalDerrotado() throws Exception {
    InterfaceConsole console = criarConsole("");
    Pokesal pokesal = CatalogoPokesal.criarIniciais().get(0);

    pokesal.receberDano(Integer.MAX_VALUE);

    Batalha batalha = new Batalha(Terreno.ASFALTO_QUENTE, new Random(42));

    assertDoesNotThrow(
        () ->
            invocar(
                console,
                "processarFimDoTurno",
                new Class<?>[] {Batalha.class, Pokesal.class},
                batalha,
                pokesal));
  }

  @Test
  void deveIniciarEFinalizarBatalha() {
    StringBuilder entradas = new StringBuilder();

    entradas.append("Ash\n");
    entradas.append("2\n");
    entradas.append("Misty\n");
    entradas.append("1\n");

    for (int indice = 0; indice < 100; indice++) {
      entradas.append("1\n");
      entradas.append("2\n");
    }

    InterfaceConsole console =
        new InterfaceConsole(new Scanner(entradas.toString()), new Random(42));

    assertDoesNotThrow(console::iniciar);
  }

  @Test
  void devePermitirUsarItemDuranteOTurno() throws Exception {
    InterfaceConsole console = criarConsole("2\n1\n");

    Treinador jogador = criarTreinador("Ash", 0);
    Treinador adversario = criarTreinador("Misty", 1);

    jogador.getPokesal().receberDano(30);

    Batalha batalha = new Batalha(Terreno.ASFALTO_QUENTE, new Random(42));

    invocar(
        console,
        "executarTurno",
        new Class<?>[] {Batalha.class, Treinador.class, Treinador.class},
        batalha,
        jogador,
        adversario);

    assertEquals(0, jogador.buscarItem(TipoItem.POCAO).getQuantidade());
  }

  @Test
  void cancelarUsoDeItemDeveManterTurnoDoJogador() throws Exception {
    InterfaceConsole console = criarConsole("2\n0\n1\n1\n");

    Treinador jogador = criarTreinador("Ash", 0);
    Treinador adversario = criarTreinador("Misty", 1);

    jogador.getPokesal().receberDano(30);

    int hpInicialAdversario = adversario.getPokesal().getHpAtual();

    Batalha batalha = new Batalha(Terreno.ASFALTO_QUENTE, new Random(42));

    invocar(
        console,
        "executarTurno",
        new Class<?>[] {Batalha.class, Treinador.class, Treinador.class},
        batalha,
        jogador,
        adversario);

    assertEquals(1, jogador.buscarItem(TipoItem.POCAO).getQuantidade());

    assertTrue(adversario.getPokesal().getHpAtual() < hpInicialAdversario);
  }

  private InterfaceConsole criarConsole(String entradas) {
    return new InterfaceConsole(new Scanner(entradas), new Random(42));
  }

  private Treinador criarTreinador(String nome, int indicePokesal) {
    return new Treinador(nome, CatalogoPokesal.criarIniciais().get(indicePokesal));
  }

  private Object invocar(
      Object alvo, String nomeMetodo, Class<?>[] tiposParametros, Object... argumentos)
      throws Exception {

    var metodo = alvo.getClass().getDeclaredMethod(nomeMetodo, tiposParametros);

    metodo.setAccessible(true);

    return metodo.invoke(alvo, argumentos);
  }
}
