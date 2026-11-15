package br.com.pokesal.console;

import br.com.pokesal.batalha.InterrompeBatalhaException;
import br.com.pokesal.catalogo.CatalogoPokesal;
import br.com.pokesal.modelo.Pokesal;
import br.com.pokesal.modelo.Treinador;
import br.com.pokesal.resultado.RepositorioResultados;
import br.com.pokesal.resultado.ResultadoBatalha;
import br.com.pokesal.torneio.Torneio;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Fluxo principal do jogo no terminal: monta o torneio, executa e exibe o histórico. */
public class InterfaceConsole {
  private static final int PARTICIPANTES_PEQUENO = 4;
  private static final int PARTICIPANTES_GRANDE = 8;

  private final EntradaConsole entrada;
  private final Torneio torneio;
  private final RepositorioResultados repositorio;

  /**
   * Cria a interface.
   *
   * @param entrada leitura do terminal
   * @param torneio torneio a executar
   * @param repositorio repositório consultado para exibir o histórico
   */
  public InterfaceConsole(
      EntradaConsole entrada, Torneio torneio, RepositorioResultados repositorio) {
    this.entrada = entrada;
    this.torneio = torneio;
    this.repositorio = repositorio;
  }

  /** Executa o jogo do início ao fim. */
  public void iniciar() {
    System.out.println("=== PokeSAL - Torneio do Estacionamento da UCSal Pituaçu ===");
    int opcao = entrada.lerOpcao("Torneio com 1 - 4 treinadores ou 2 - 8 treinadores? ", 1, 2);
    int total = opcao == 1 ? PARTICIPANTES_PEQUENO : PARTICIPANTES_GRANDE;
    List<Treinador> participantes = new ArrayList<>();
    Set<String> nomes = new HashSet<>();
    for (int i = 1; i <= total; i++) {
      String nome = lerNomeUnico(i, nomes);
      participantes.add(new Treinador(nome, escolherPokesal(nome)));
    }
    Treinador campeao;
    try {
      campeao = torneio.executar(participantes);
    } catch (InterrompeBatalhaException e) {
      System.out.println();
      System.out.println(e.getMessage());
      return;
    }
    System.out.println();
    System.out.println("CAMPEÃO: " + campeao.getNome() + " com " + campeao.getPokesal().getNome());
    System.out.println("Histórico de batalhas:");
    for (ResultadoBatalha r : repositorio.listar()) {
      System.out.println(
          "  "
              + r.vencedor()
              + " ("
              + r.pokesalVencedor()
              + ") venceu "
              + r.perdedor()
              + " ("
              + r.pokesalPerdedor()
              + ") em "
              + r.turnos()
              + " turnos");
    }
  }

  private String lerNomeUnico(int numero, Set<String> nomes) {
    while (true) {
      String nome = entrada.lerTextoObrigatorio("Nome do treinador " + numero + ": ");
      if (nomes.add(nome)) {
        return nome;
      }
      System.out.println("Esse nome já foi escolhido.");
    }
  }

  private Pokesal escolherPokesal(String nome) {
    List<Pokesal> opcoes = CatalogoPokesal.criarIniciais();
    for (int i = 0; i < opcoes.size(); i++) {
      Pokesal p = opcoes.get(i);
      System.out.println("  " + (i + 1) + " - " + p.getNome() + " (" + p.getTipo() + ")");
    }
    return opcoes.get(entrada.lerOpcao(nome + ", escolha seu Pokésal: ", 1, opcoes.size()) - 1);
  }
}
