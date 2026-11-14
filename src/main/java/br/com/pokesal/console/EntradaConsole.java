package br.com.pokesal.console;

import java.util.Scanner;

/** Responsável pela leitura e validação das entradas do terminal. */
public class EntradaConsole {

  private final Scanner scanner;

  /**
   * Cria a entrada do console.
   *
   * @param scanner leitor de entrada
   */
  public EntradaConsole(Scanner scanner) {
    if (scanner == null) {
      throw new IllegalArgumentException("O scanner é obrigatório");
    }

    this.scanner = scanner;
  }

  /**
   * Lê um texto do terminal.
   *
   * @param mensagem mensagem exibida
   * @return texto informado
   */
  public String lerTexto(String mensagem) {
    System.out.print(mensagem);

    if (!scanner.hasNextLine()) {
      throw new IllegalStateException("Não existem mais entradas disponíveis");
    }

    return scanner.nextLine();
  }

  /**
   * Lê um texto não vazio.
   *
   * @param mensagem mensagem exibida
   * @return texto válido
   */
  public String lerTextoObrigatorio(String mensagem) {
    while (true) {
      String texto = lerTexto(mensagem).trim();

      if (!texto.isBlank()) {
        return texto;
      }

      System.out.println("A entrada não pode ser vazia.");
    }
  }

  /**
   * Lê uma opção numérica dentro do intervalo informado.
   *
   * @param mensagem mensagem exibida
   * @param minimo valor mínimo permitido
   * @param maximo valor máximo permitido
   * @return opção validada
   */
  public int lerOpcao(String mensagem, int minimo, int maximo) {
    if (minimo > maximo) {
      throw new IllegalArgumentException("O intervalo de opções é inválido");
    }

    while (true) {
      String entrada = lerTexto(mensagem).trim();

      try {
        int opcao = Integer.parseInt(entrada);

        if (opcao >= minimo && opcao <= maximo) {
          return opcao;
        }

        System.out.println("Opção fora do intervalo permitido.");
      } catch (NumberFormatException excecao) {
        System.out.println("Digite uma opção numérica válida.");
      }
    }
  }
}
