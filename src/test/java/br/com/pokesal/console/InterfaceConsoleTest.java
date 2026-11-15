package br.com.pokesal.console;

import static org.junit.jupiter.api.Assertions.*;

import br.com.pokesal.batalha.InterrompeBatalhaException;
import br.com.pokesal.modelo.Treinador;
import br.com.pokesal.resultado.RepositorioResultados;
import br.com.pokesal.resultado.ResultadoBatalha;
import br.com.pokesal.torneio.ExecutorBatalha;
import br.com.pokesal.torneio.Torneio;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InterfaceConsoleTest {

  private final ByteArrayOutputStream saida = new ByteArrayOutputStream();
  private PrintStream saidaOriginal;

  @BeforeEach
  void setUp() {
    saidaOriginal = System.out;
    System.setOut(new PrintStream(saida, true, StandardCharsets.UTF_8));
  }

  @AfterEach
  void tearDown() {
    System.setOut(saidaOriginal);
  }

  @Test
  @DisplayName("Torneio com quatro treinadores apresenta o campeão")
  void testTorneioComQuatroTreinadores() {
    RepositorioMemoria repositorio = new RepositorioMemoria();
    InterfaceConsole interfaceConsole =
        criarInterface(
            "1\nAsh\n1\nMisty\n2\nBrock\n3\nGary\n4\n",
            repositorio,
            new ExecutorVencedorPrimeiro(repositorio));

    interfaceConsole.iniciar();

    String resultado = saida.toString();

    assertAll(
        () -> assertTrue(resultado.contains("CAMPEÃO: Ash")),
        () -> assertEquals(3, repositorio.listar().size()),
        () -> assertTrue(resultado.contains("Histórico de batalhas")));
  }

  @Test
  @DisplayName("Torneio com oito treinadores executa sete batalhas")
  void testTorneioComOitoTreinadores() {
    RepositorioMemoria repositorio = new RepositorioMemoria();

    String dados =
        "2\n"
            + "Ash\n1\n"
            + "Misty\n2\n"
            + "Brock\n3\n"
            + "Gary\n4\n"
            + "Red\n5\n"
            + "Blue\n6\n"
            + "Green\n1\n"
            + "Yellow\n2\n";

    InterfaceConsole interfaceConsole =
        criarInterface(dados, repositorio, new ExecutorVencedorPrimeiro(repositorio));

    interfaceConsole.iniciar();

    assertAll(
        () -> assertTrue(saida.toString().contains("CAMPEÃO: Ash")),
        () -> assertEquals(7, repositorio.listar().size()));
  }

  @Test
  @DisplayName("Nome duplicado é rejeitado e solicitado novamente")
  void testNomeDuplicadoEhRejeitado() {
    RepositorioMemoria repositorio = new RepositorioMemoria();

    InterfaceConsole interfaceConsole =
        criarInterface(
            "1\nAsh\n1\nAsh\nMisty\n2\nBrock\n3\nGary\n4\n",
            repositorio,
            new ExecutorVencedorPrimeiro(repositorio));

    interfaceConsole.iniciar();

    assertTrue(saida.toString().contains("Esse nome já foi escolhido."));
    assertEquals(3, repositorio.listar().size());
  }

  @Test
  @DisplayName("Interrupção da batalha encerra o fluxo sem apresentar campeão")
  void testInterrupcaoDoTorneio() {
    ExecutorBatalha executor =
        (a, b) -> {
          throw new InterrompeBatalhaException();
        };

    InterfaceConsole interfaceConsole =
        criarInterface(
            "1\nAsh\n1\nMisty\n2\nBrock\n3\nGary\n4\n", new RepositorioMemoria(), executor);

    interfaceConsole.iniciar();

    String resultado = saida.toString();

    assertAll(
        () -> assertFalse(resultado.contains("CAMPEÃO:")),
        () -> assertTrue(resultado.contains("Histórico") == false));
  }

  @Test
  @DisplayName("Torneio exibe o histórico das batalhas realizadas")
  void testExibirHistorico() {
    RepositorioMemoria repositorio = new RepositorioMemoria();

    InterfaceConsole interfaceConsole =
        criarInterface(
            "1\nAsh\n1\nMisty\n2\nBrock\n3\nGary\n4\n",
            repositorio,
            new ExecutorVencedorPrimeiro(repositorio));

    interfaceConsole.iniciar();

    assertTrue(saida.toString().contains("venceu"));
  }

  private InterfaceConsole criarInterface(
      String dados, RepositorioMemoria repositorio, ExecutorBatalha executor) {

    Scanner scanner = new Scanner(new ByteArrayInputStream(dados.getBytes(StandardCharsets.UTF_8)));

    return new InterfaceConsole(new EntradaConsole(scanner), new Torneio(executor), repositorio);
  }

  private static class ExecutorVencedorPrimeiro implements ExecutorBatalha {

    private final RepositorioMemoria repositorio;

    ExecutorVencedorPrimeiro(RepositorioMemoria repositorio) {
      this.repositorio = repositorio;
    }

    @Override
    public ResultadoBatalha executar(Treinador a, Treinador b) {
      ResultadoBatalha resultado =
          new ResultadoBatalha(
              a.getNome(), b.getNome(), 3, a.getPokesal().getNome(), b.getPokesal().getNome());

      repositorio.salvar(resultado);
      return resultado;
    }
  }

  private static class RepositorioMemoria implements RepositorioResultados {

    private final List<ResultadoBatalha> resultados = new ArrayList<>();

    @Override
    public void salvar(ResultadoBatalha resultado) {
      resultados.add(resultado);
    }

    @Override
    public List<ResultadoBatalha> listar() {
      return List.copyOf(resultados);
    }
  }
}
