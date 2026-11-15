package br.com.pokesal;

import br.com.pokesal.batalha.ContextoBatalha;
import br.com.pokesal.batalha.GeradorAleatorio;
import br.com.pokesal.batalha.RegistroBatalha;
import br.com.pokesal.console.EntradaConsole;
import br.com.pokesal.console.ExecutorBatalhaConsole;
import br.com.pokesal.console.InterfaceConsole;
import br.com.pokesal.console.RegistroBatalhaConsole;
import br.com.pokesal.infra.GeradorAleatorioPadrao;
import br.com.pokesal.resultado.RepositorioResultadosMemoria;
import br.com.pokesal.terreno.ServicoTerrenoClimatico;
import br.com.pokesal.torneio.Torneio;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Scanner;

/** Classe principal do simulador de batalhas. Monta as dependências e inicia o jogo. */
public class Main {

  private Main() {}

  /**
   * Inicia o programa.
   *
   * @param args argumentos recebidos pela linha de comando
   */
  public static void main(String[] args) {
    EntradaConsole entrada = new EntradaConsole(new Scanner(System.in));
    GeradorAleatorio gerador = new GeradorAleatorioPadrao();
    RepositorioResultadosMemoria repositorio = new RepositorioResultadosMemoria();
    RegistroBatalha registro = new RegistroBatalhaConsole();
    ContextoBatalha contexto = new ContextoBatalha();
    ServicoTerrenoClimatico servicoTerreno =
        new ServicoTerrenoClimatico(Clock.system(ZoneId.of("America/Bahia")), gerador);
    ExecutorBatalhaConsole executor =
        new ExecutorBatalhaConsole(
            entrada, servicoTerreno, gerador, repositorio, registro, contexto);
    new InterfaceConsole(entrada, new Torneio(executor), repositorio).iniciar();
  }
}
