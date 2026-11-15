package br.com.pokesal.console;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EntradaConsoleTest {

  @Test
  @DisplayName("Texto obrigatório é lido removendo espaços nas pontas")
  void testLerTextoObrigatorio() {
    EntradaConsole entrada = criarEntrada("  Uallace  ");

    String resultado = entrada.lerTextoObrigatorio("Nome: ");

    assertEquals("Uallace", resultado);
  }

  @Test
  @DisplayName("Texto vazio é rejeitado até receber uma entrada válida")
  void testTextoVazioEhRejeitado() {
    EntradaConsole entrada = criarEntrada("   \nUallace\n");

    String resultado = entrada.lerTextoObrigatorio("Nome: ");

    assertEquals("Uallace", resultado);
  }

  @Test
  @DisplayName("Opção dentro do intervalo é aceita")
  void testLerOpcaoValida() {
    EntradaConsole entrada = criarEntrada("2\n");

    int resultado = entrada.lerOpcao("Escolha: ", 1, 4);

    assertEquals(2, resultado);
  }

  @Test
  @DisplayName("Entrada não numérica é rejeitada")
  void testEntradaNaoNumericaEhRejeitada() {
    EntradaConsole entrada = criarEntrada("abc\n3\n");

    int resultado = entrada.lerOpcao("Escolha: ", 1, 4);

    assertEquals(3, resultado);
  }

  @Test
  @DisplayName("Número fora do intervalo é rejeitado")
  void testOpcaoForaDoIntervaloEhRejeitada() {
    EntradaConsole entrada = criarEntrada("0\n5\n2\n");

    int resultado = entrada.lerOpcao("Escolha: ", 1, 4);

    assertEquals(2, resultado);
  }

  @Test
  @DisplayName("Limites mínimo e máximo do intervalo são aceitos")
  void testLimitesDoIntervalo() {
    EntradaConsole minimo = criarEntrada("1\n");
    EntradaConsole maximo = criarEntrada("4\n");

    assertEquals(1, minimo.lerOpcao("Escolha: ", 1, 4));
    assertEquals(4, maximo.lerOpcao("Escolha: ", 1, 4));
  }

  private EntradaConsole criarEntrada(String dados) {
    Scanner scanner = new Scanner(new ByteArrayInputStream(dados.getBytes(StandardCharsets.UTF_8)));
    return new EntradaConsole(scanner);
  }
}
