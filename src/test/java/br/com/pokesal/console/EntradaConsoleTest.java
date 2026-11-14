package br.com.pokesal.console;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Scanner;
import org.junit.jupiter.api.Test;

class EntradaConsoleTest {

  @Test
  void deveRetornarOpcaoValida() {
    Scanner scanner = new Scanner("2\n");
    EntradaConsole entrada = new EntradaConsole(scanner);

    assertEquals(2, entrada.lerOpcao("Escolha: ", 1, 3));
  }

  @Test
  void deveSolicitarNovamenteQuandoOpcaoForMenorQueMinimo() {
    Scanner scanner = new Scanner("0\n2\n");
    EntradaConsole entrada = new EntradaConsole(scanner);

    assertEquals(2, entrada.lerOpcao("Escolha: ", 1, 3));
  }

  @Test
  void deveSolicitarNovamenteQuandoOpcaoForMaiorQueMaximo() {
    Scanner scanner = new Scanner("5\n3\n");
    EntradaConsole entrada = new EntradaConsole(scanner);

    assertEquals(3, entrada.lerOpcao("Escolha: ", 1, 3));
  }

  @Test
  void deveSolicitarNovamenteQuandoEntradaNaoForNumerica() {
    Scanner scanner = new Scanner("abc\n2\n");
    EntradaConsole entrada = new EntradaConsole(scanner);

    assertEquals(2, entrada.lerOpcao("Escolha: ", 1, 3));
  }

  @Test
  void deveAceitarLimitesDoIntervalo() {
    Scanner scanner = new Scanner("1\n3\n");
    EntradaConsole entrada = new EntradaConsole(scanner);

    assertEquals(1, entrada.lerOpcao("Escolha: ", 1, 3));
    assertEquals(3, entrada.lerOpcao("Escolha: ", 1, 3));
  }

  @Test
  void deveAceitarIntervaloComUmUnicoValor() {
    Scanner scanner = new Scanner("2\n");
    EntradaConsole entrada = new EntradaConsole(scanner);

    assertEquals(2, entrada.lerOpcao("Escolha: ", 2, 2));
  }

  @Test
  void deveRejeitarScannerNulo() {
    IllegalArgumentException excecao =
        assertThrows(IllegalArgumentException.class, () -> new EntradaConsole(null));

    assertEquals("O scanner é obrigatório", excecao.getMessage());
  }

  @Test
  void deveRejeitarIntervaloInvalido() {
    Scanner scanner = new Scanner("1\n");
    EntradaConsole entrada = new EntradaConsole(scanner);

    IllegalArgumentException excecao =
        assertThrows(IllegalArgumentException.class, () -> entrada.lerOpcao("Escolha: ", 5, 1));

    assertEquals("O intervalo de opções é inválido", excecao.getMessage());
  }
}
