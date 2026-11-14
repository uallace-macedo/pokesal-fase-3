package br.com.pokesal.console;

import java.util.Scanner;

/** Leitura de dados digitados pelo usuário no terminal. */
public class EntradaConsole {
  private final Scanner scanner;

  /**
   * Cria a entrada.
   *
   * @param scanner origem dos dados digitados
   */
  public EntradaConsole(Scanner scanner) {
    this.scanner = scanner;
  }

  /**
   * Lê um texto que não pode ser vazio, repetindo a pergunta até receber uma resposta válida.
   *
   * @param mensagem pergunta exibida
   * @return texto digitado, sem espaços nas pontas
   */
  public String lerTextoObrigatorio(String mensagem) {
    while (true) {
      System.out.print(mensagem);
      String texto = scanner.nextLine().trim();
      if (!texto.isEmpty()) {
        return texto;
      }
      System.out.println("Digite um valor.");
    }
  }

  /**
   * Lê um inteiro dentro de um intervalo, repetindo a pergunta até receber um valor válido.
   *
   * @param mensagem pergunta exibida
   * @param minimo menor valor aceito
   * @param maximo maior valor aceito
   * @return número digitado
   */
  public int lerOpcao(String mensagem, int minimo, int maximo) {
    while (true) {
      System.out.print(mensagem);
      try {
        int valor = Integer.parseInt(scanner.nextLine().trim());
        if (valor >= minimo && valor <= maximo) {
          return valor;
        }
      } catch (NumberFormatException e) {
        // Entrada inválida: a pergunta é repetida abaixo.
      }
      System.out.println("Escolha um número entre " + minimo + " e " + maximo + ".");
    }
  }
}
