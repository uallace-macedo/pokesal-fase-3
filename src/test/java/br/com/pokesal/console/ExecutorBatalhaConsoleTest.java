package br.com.pokesal.console;

import static org.junit.jupiter.api.Assertions.*;

import br.com.pokesal.batalha.ContextoBatalha;
import br.com.pokesal.batalha.GeradorAleatorio;
import br.com.pokesal.batalha.InterrompeBatalhaException;
import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.TipoElemental;
import br.com.pokesal.enums.TipoTerreno;
import br.com.pokesal.modelo.Ataque;
import br.com.pokesal.modelo.Pokesal;
import br.com.pokesal.modelo.Treinador;
import br.com.pokesal.resultado.RepositorioResultados;
import br.com.pokesal.resultado.ResultadoBatalha;
import br.com.pokesal.terreno.ServicoTerreno;
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

class ExecutorBatalhaConsoleTest {

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
  @DisplayName("Batalha termina e devolve o resultado do vencedor")
  void testExecutarBatalha() {
    Treinador treinador1 = criarTreinador("Ash", "Pikachu", 100);
    Treinador treinador2 = criarTreinador("Misty", "Squirtle", 10);
    RepositorioMemoria repositorio = new RepositorioMemoria();

    ExecutorBatalhaConsole executor = criarExecutor("1\n1\n", repositorio);

    ResultadoBatalha resultado = executor.executar(treinador1, treinador2);

    assertEquals("Ash", resultado.vencedor());
    assertEquals("Misty", resultado.perdedor());
    assertEquals(1, resultado.turnos());
    assertEquals(1, repositorio.listar().size());
  }

  @Test
  @DisplayName("Batalha exibe os participantes e seus Pokésals")
  void testExibirParticipantes() {
    Treinador treinador1 = criarTreinador("Ash", "Pikachu", 100);
    Treinador treinador2 = criarTreinador("Misty", "Squirtle", 10);

    ExecutorBatalhaConsole executor = criarExecutor("1\n1\n", new RepositorioMemoria());

    executor.executar(treinador1, treinador2);

    String resultado = saida.toString();

    assertTrue(resultado.contains("Ash"));
    assertTrue(resultado.contains("Misty"));
    assertTrue(resultado.contains("Pikachu"));
    assertTrue(resultado.contains("Squirtle"));
  }

  @Test
  @DisplayName("Pokésal derrotado não executa sua ação")
  void testAcaoDoDerrotadoNaoEhExecutada() {
    Treinador treinador1 = criarTreinador("Ash", "Pikachu", 100);
    Treinador treinador2 = criarTreinador("Misty", "Squirtle", 10);

    ExecutorBatalhaConsole executor = criarExecutor("1\n1\n", new RepositorioMemoria());

    ResultadoBatalha resultado = executor.executar(treinador1, treinador2);

    assertAll(
        () -> assertEquals("Ash", resultado.vencedor()),
        () -> assertEquals(0, treinador2.getPokesal().getHpAtual()));
  }

  @Test
  @DisplayName("Batalha interrompida propaga a exceção")
  void testBatalhaInterrompida() {
    ContextoBatalha contexto = new ContextoBatalha();
    contexto.setParada(true);

    ExecutorBatalhaConsole executor = criarExecutor("1\n1\n", new RepositorioMemoria(), contexto);

    Treinador treinador1 = criarTreinador("Ash", "Pikachu", 100);
    Treinador treinador2 = criarTreinador("Misty", "Squirtle", 10);

    assertThrows(InterrompeBatalhaException.class, () -> executor.executar(treinador1, treinador2));
  }

  @Test
  @DisplayName("Batalha exibe o vencedor ao terminar")
  void testExibirVencedor() {
    Treinador treinador1 = criarTreinador("Ash", "Pikachu", 100);
    Treinador treinador2 = criarTreinador("Misty", "Squirtle", 10);

    ExecutorBatalhaConsole executor = criarExecutor("1\n1\n", new RepositorioMemoria());

    executor.executar(treinador1, treinador2);

    assertTrue(saida.toString().contains("Vencedor: Ash"));
  }

  private ExecutorBatalhaConsole criarExecutor(String dados, RepositorioMemoria repositorio) {
    return criarExecutor(dados, repositorio, new ContextoBatalha());
  }

  private ExecutorBatalhaConsole criarExecutor(
      String dados, RepositorioMemoria repositorio, ContextoBatalha contexto) {

    Scanner scanner = new Scanner(new ByteArrayInputStream(dados.getBytes(StandardCharsets.UTF_8)));

    ServicoTerreno terreno = () -> TipoTerreno.SEM_EFEITO;

    GeradorAleatorio gerador = limite -> 0;

    return new ExecutorBatalhaConsole(
        new EntradaConsole(scanner),
        terreno,
        gerador,
        repositorio,
        new RegistroBatalhaConsole(),
        contexto);
  }

  private Treinador criarTreinador(String nome, String nomePokesal, int velocidade) {
    Ataque ataque = new Ataque("Golpe Final", TipoElemental.NORMAL, 1000, 100, EfeitoStatus.NENHUM);

    Pokesal pokesal =
        new Pokesal(nomePokesal, TipoElemental.NORMAL, 50, 10, 0, velocidade, List.of(ataque));

    return new Treinador(nome, pokesal);
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
