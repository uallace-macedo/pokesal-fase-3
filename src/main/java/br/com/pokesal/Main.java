package br.com.pokesal;

import br.com.pokesal.console.InterfaceConsole;

/** Classe principal do simulador de batalhas */
public class Main {

  /**
   * Inicia o programa.
   *
   * @param args argumentos recebidos pela linha de comando
   */
  public static void main(String[] args) {
    InterfaceConsole console = new InterfaceConsole();
    console.iniciar();
  }
}
