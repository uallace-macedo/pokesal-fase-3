package br.com.pokesal.console;

import br.com.pokesal.batalha.Batalha;
import br.com.pokesal.catalogo.CatalogoPokesal;
import br.com.pokesal.enums.TipoTerreno;
import br.com.pokesal.enums.TipoItem;
import br.com.pokesal.modelo.Ataque;
import br.com.pokesal.modelo.Item;
import br.com.pokesal.modelo.Pokesal;
import br.com.pokesal.modelo.Treinador;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/** Responsável pela interação dos jogadores durante a batalha. */
public class InterfaceConsole {

  private static final int PRIMEIRA_OPCAO = 1;
  private static final int OPCAO_CANCELAR = 0;
  private static final int OPCAO_ATACAR = 1;
  private static final int OPCAO_ITEM = 2;
  private static final int QUANTIDADE_ITENS = 3;

  private static final TipoItem[] TIPOS_ITENS = {
    TipoItem.POCAO, TipoItem.SUPER_POCAO, TipoItem.ANTIDOTO
  };

  private final EntradaConsole entrada;
  private final Random random;

  private Treinador treinador1;
  private Treinador treinador2;

  /** Cria a interface utilizando a entrada e saída padrão. */
  public InterfaceConsole() {
    this(new Scanner(System.in), new Random());
  }

  /**
   * Cria a interface com dependências definidas.
   *
   * @param scanner leitor de entrada
   * @param random gerador aleatório
   */
  public InterfaceConsole(Scanner scanner, Random random) {
    if (scanner == null) {
      throw new IllegalArgumentException("O scanner é obrigatório");
    }

    if (random == null) {
      throw new IllegalArgumentException("O gerador aleatório é obrigatório");
    }

    this.entrada = new EntradaConsole(scanner);
    this.random = random;
  }

  /** Inicia a seleção dos jogadores e a batalha. */
  public void iniciar() {
    System.out.println("==================================");
    System.out.println("          POKESAL");
    System.out.println("==================================");

    treinador1 = selecionarTreinador(PRIMEIRA_OPCAO);
    treinador2 = selecionarTreinador(2);

    TipoTerreno[] tipoTerrenos = TipoTerreno.values();
    TipoTerreno tipoTerreno = tipoTerrenos[random.nextInt(tipoTerrenos.length)];

    System.out.println();
    System.out.println("Terreno escolhido: " + tipoTerreno);

    Batalha batalha = new Batalha(treinador1, treinador2, tipoTerreno, random);

    executarBatalha(batalha);
  }

  /**
   * Realiza a seleção de um treinador e seu pokesal.
   *
   * @param numero identificação do jogador
   * @return treinador criado
   */
  private Treinador selecionarTreinador(int numero) {
    System.out.println();
    System.out.println("Jogador " + numero);

    String nome = entrada.lerTextoObrigatorio("Nome do treinador: ");

    List<Pokesal> pokesals = CatalogoPokesal.criarIniciais();

    System.out.println();
    System.out.println("Escolha seu Pokésal:");

    for (int indice = 0; indice < pokesals.size(); indice++) {
      Pokesal pokesal = pokesals.get(indice);

      System.out.printf(
          "%d - %s (%s)%n", indice + PRIMEIRA_OPCAO, pokesal.getNome(), pokesal.getTipo());
    }

    int opcao = entrada.lerOpcao("Escolha: ", PRIMEIRA_OPCAO, pokesals.size());

    return new Treinador(nome, pokesals.get(opcao - PRIMEIRA_OPCAO));
  }

  /**
   * Executa a batalha até que exista um vencedor.
   *
   * @param batalha batalha em execução
   */
  private void executarBatalha(Batalha batalha) {
    System.out.println();
    System.out.println("========== INICIO DA BATALHA ==========");

    while (batalha.verificarVencedor().isEmpty()) {
      exibirEstado();

      Treinador primeiro;
      Treinador segundo;

      if (batalha.jogador1AtacaPrimeiro()) {
        primeiro = treinador1;
        segundo = treinador2;
      } else {
        primeiro = treinador2;
        segundo = treinador1;
      }

      executarTurno(batalha, primeiro, segundo);

      if (batalha.verificarVencedor().isPresent()) {
        break;
      }

      executarTurno(batalha, segundo, primeiro);

      if (batalha.verificarVencedor().isPresent()) {
        break;
      }

      processarFimDoTurno(batalha, treinador1.getPokesal());
      processarFimDoTurno(batalha, treinador2.getPokesal());
    }

    System.out.println();
    System.out.println("========== FIM DA BATALHA ==========");

    batalha
        .verificarVencedor()
        .ifPresentOrElse(
            vencedor -> System.out.println("Vencedor: " + vencedor.getNome()),
            () -> System.out.println("A batalha terminou sem vencedor."));
  }

  /**
   * Executa a ação de um treinador durante seu turno.
   *
   * @param batalha batalha atual
   * @param atual treinador que está jogando
   * @param adversario treinador adversário
   */
  private void executarTurno(Batalha batalha, Treinador atual, Treinador adversario) {

    while (atual.getPokesal().estaVivo() && adversario.getPokesal().estaVivo()) {
      System.out.println();
      System.out.println("Turno de " + atual.getNome());

      System.out.println(OPCAO_ATACAR + " - Atacar");
      System.out.println(OPCAO_ITEM + " - Usar item");

      int opcao = entrada.lerOpcao("Escolha sua ação: ", OPCAO_ATACAR, OPCAO_ITEM);

      if (opcao == OPCAO_ATACAR) {
        if (executarAtaque(batalha, atual, adversario)) {
          return;
        }
      } else {
        if (usarItem(atual)) {
          return;
        }
      }
    }
  }

  /**
   * Permite escolher e executar um dos ataques disponíveis.
   *
   * @param batalha batalha atual
   * @param atual treinador atacante
   * @param adversario treinador defensor
   */
  private boolean executarAtaque(Batalha batalha, Treinador atual, Treinador adversario) {

    if (!atual.getPokesal().estaVivo() || !adversario.getPokesal().estaVivo()) {
      return false;
    }

    List<Ataque> ataques = atual.getPokesal().getAtaques();

    System.out.println();
    System.out.println("Escolha um ataque:");

    for (int indice = 0; indice < ataques.size(); indice++) {
      Ataque ataque = ataques.get(indice);

      System.out.printf(
          "%d - %s (Dano base: %d)%n",
          indice + PRIMEIRA_OPCAO, ataque.getNome(), ataque.getDanoBase());
    }

    System.out.println(OPCAO_CANCELAR + " - Voltar");

    int opcao = entrada.lerOpcao("Escolha: ", OPCAO_CANCELAR, ataques.size());

    if (opcao == OPCAO_CANCELAR) {
      return false;
    }

    Ataque ataque = ataques.get(opcao - PRIMEIRA_OPCAO);

    int dano = batalha.executarAtaque(ataque, atual.getPokesal(), adversario.getPokesal());

    System.out.println();
    System.out.println(atual.getPokesal().getNome() + " utilizou " + ataque.getNome() + "!");

    System.out.println("Dano causado: " + dano);

    if (!adversario.getPokesal().estaVivo()) {
      System.out.println(adversario.getPokesal().getNome() + " foi derrotado!");
    } else {
      System.out.println("Status atual: " + adversario.getPokesal().getEfeitoStatus());
    }

    return true;
  }

  /**
   * Permite selecionar e utilizar um item do inventário.
   *
   * @param treinador treinador que utilizará o item
   */
  private boolean usarItem(Treinador treinador) {
    if (!treinador.podeUsarItem()) {
      System.out.println("Limite de utilização de itens atingido.");
      return false;
    }

    System.out.println();
    System.out.println("Escolha um item:");
    System.out.println("1 - Poção");
    System.out.println("2 - Super Poção");
    System.out.println("3 - Antídoto");
    System.out.println("0 - Cancelar");

    int opcao = entrada.lerOpcao("Escolha: ", OPCAO_CANCELAR, QUANTIDADE_ITENS);

    if (opcao == OPCAO_CANCELAR) {
      return false;
    }

    TipoItem tipo = TIPOS_ITENS[opcao - PRIMEIRA_OPCAO];
    Item item = treinador.buscarItem(tipo);

    if (treinador.usarItem(item)) {
      System.out.println("Item utilizado: " + item.getNome());
      return true;
    }

    System.out.println("Não foi possível utilizar esse item.");
    return false;
  }

  /**
   * Processa os efeitos de final de turno.
   *
   * @param batalha batalha atual
   * @param pokesal pokesal afetado
   */
  private void processarFimDoTurno(Batalha batalha, Pokesal pokesal) {
    batalha.processarFimDoTurno(pokesal);
  }

  /** Exibe o estado dos participantes armazenados na interface. */
  private void exibirEstado() {
    if (treinador1 == null || treinador2 == null) {
      return;
    }

    exibirEstado(treinador1, treinador2);
  }

  /**
   * Exibe o estado dos dois participantes.
   *
   * @param jogador1 primeiro treinador
   * @param jogador2 segundo treinador
   */
  private void exibirEstado(Treinador jogador1, Treinador jogador2) {
    System.out.println();
    System.out.println("========== ESTADO DOS POKESALS ==========");

    exibirPokesal(jogador1);
    exibirPokesal(jogador2);
  }

  private void exibirPokesal(Treinador treinador) {
    Pokesal pokesal = treinador.getPokesal();

    System.out.println();
    System.out.println("Treinador: " + treinador.getNome());
    System.out.println("POKESAL: " + pokesal.getNome());
    System.out.println("HP: " + pokesal.getHpAtual() + "/" + pokesal.getHpMaximo());
    System.out.println("ATK: " + pokesal.getAtaqueEfetivo());
    System.out.println("DEF: " + pokesal.getDefesa());
    System.out.println("SPD: " + pokesal.getVelocidadeEfetiva());
    System.out.println("Status: " + pokesal.getEfeitoStatus());
  }
}
