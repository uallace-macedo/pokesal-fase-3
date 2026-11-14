package br.com.pokesal.modelo;

import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.TipoElemental;
import java.util.List;

/** Pokésal em batalha, com atributos, golpes e efeito de status atual. */
public class Pokesal {
  private final String nome;
  private final TipoElemental tipo;
  private final int hpMaximo;
  private final int ataque;
  private final int defesa;
  private final int velocidade;
  private final List<Ataque> ataques;
  private int hpAtual;
  private EfeitoStatus efeitoStatus = EfeitoStatus.NENHUM;
  private int turnosStatus;

  /**
   * Cria um Pokésal com HP cheio e sem efeito de status.
   *
   * @param nome nome do Pokésal
   * @param tipo tipo elemental
   * @param hpMaximo pontos de vida máximos, maiores que zero
   * @param ataque atributo ATK, maior ou igual a zero
   * @param defesa atributo DEF, maior ou igual a zero
   * @param velocidade atributo SPD, maior ou igual a zero
   * @param ataques golpes disponíveis
   * @throws IllegalArgumentException se algum atributo for inválido
   */
  public Pokesal(
          String nome,
          TipoElemental tipo,
          int hpMaximo,
          int ataque,
          int defesa,
          int velocidade,
          List<Ataque> ataques) {
    if (hpMaximo <= 0) {
      throw new IllegalArgumentException("O HP máximo deve ser maior que zero.");
    }
    if (ataque < 0 || defesa < 0 || velocidade < 0) {
      throw new IllegalArgumentException("ATK, DEF e SPD não podem ser negativos.");
    }
    this.nome = nome;
    this.tipo = tipo;
    this.hpMaximo = hpMaximo;
    this.hpAtual = hpMaximo;
    this.ataque = ataque;
    this.defesa = defesa;
    this.velocidade = velocidade;
    this.ataques = List.copyOf(ataques);
  }

  /**
   * Reduz o HP atual. O HP nunca fica abaixo de zero.
   *
   * @param dano dano recebido, maior ou igual a zero
   * @throws IllegalArgumentException se o dano for negativo
   */
  public void receberDano(int dano) {
    if (dano < 0) {
      throw new IllegalArgumentException("O dano não pode ser negativo.");
    }
    hpAtual = Math.max(0, hpAtual - dano);
  }

  /**
   * Recupera HP. O HP nunca ultrapassa o máximo e um Pokésal derrotado não recupera vida.
   *
   * @param valor quantidade recuperada, maior ou igual a zero
   * @throws IllegalArgumentException se o valor for negativo
   */
  public void recuperarVida(int valor) {
    if (valor < 0) {
      throw new IllegalArgumentException("A cura não pode ser negativa.");
    }
    if (estaVivo()) {
      hpAtual = Math.min(hpMaximo, hpAtual + valor);
    }
  }

  /**
   * Indica se o Pokésal ainda pode lutar.
   *
   * @return {@code true} se o HP atual for maior que zero
   */
  public boolean estaVivo() {
    return hpAtual > 0;
  }

  /**
   * Aplica um efeito de status. Efeitos não se acumulam.
   *
   * @param novoEfeito efeito a aplicar
   * @return {@code true} se o efeito foi aplicado; {@code false} se já havia outro efeito ou se o
   *     efeito era {@code NENHUM}
   */
  public boolean aplicarStatus(EfeitoStatus novoEfeito) {
    if (novoEfeito == EfeitoStatus.NENHUM || efeitoStatus != EfeitoStatus.NENHUM) {
      return false;
    }
    efeitoStatus = novoEfeito;
    turnosStatus = novoEfeito.getDuracao();
    return true;
  }

  /** Consome um turno do efeito de status e o remove quando a duração termina. */
  public void avancarTurnoStatus() {
    if (efeitoStatus == EfeitoStatus.NENHUM) {
      return;
    }
    turnosStatus--;
    if (turnosStatus <= 0) {
      removerStatus();
    }
  }

  /** Remove o efeito de status atual. */
  public void removerStatus() {
    efeitoStatus = EfeitoStatus.NENHUM;
    turnosStatus = 0;
  }

  /** Devolve HP máximo e remove o efeito de status, para uma nova batalha. */
  public void restaurar() {
    hpAtual = hpMaximo;
    removerStatus();
  }

  /**
   * Retorna o ataque considerando o efeito de status.
   *
   * @return ATK menos a redução do efeito, nunca abaixo de zero
   */
  public int getAtaqueEfetivo() {
    return Math.max(0, ataque - efeitoStatus.getReducaoAtaque());
  }

  /**
   * Retorna a velocidade considerando o efeito de status.
   *
   * @return SPD menos a redução do efeito, nunca abaixo de zero
   */
  public int getVelocidadeEfetiva() {
    return Math.max(0, velocidade - efeitoStatus.getReducaoVelocidade());
  }

  /**
   * Retorna o nome.
   *
   * @return nome do Pokésal
   */
  public String getNome() {
    return nome;
  }

  /**
   * Retorna o tipo elemental.
   *
   * @return tipo do Pokésal
   */
  public TipoElemental getTipo() {
    return tipo;
  }

  /**
   * Retorna o HP máximo.
   *
   * @return HP máximo
   */
  public int getHpMaximo() {
    return hpMaximo;
  }

  /**
   * Retorna o HP atual.
   *
   * @return HP atual
   */
  public int getHpAtual() {
    return hpAtual;
  }

  /**
   * Retorna a defesa.
   *
   * @return atributo DEF
   */
  public int getDefesa() {
    return defesa;
  }

  /**
   * Retorna os golpes.
   *
   * @return lista imutável de golpes
   */
  public List<Ataque> getAtaques() {
    return ataques;
  }

  /**
   * Retorna o efeito de status atual.
   *
   * @return efeito atual, ou {@code NENHUM}
   */
  public EfeitoStatus getEfeitoStatus() {
    return efeitoStatus;
  }

  /**
   * Retorna os turnos restantes do efeito de status.
   *
   * @return turnos restantes, ou zero se não há efeito
   */
  public int getTurnosStatus() {
    return turnosStatus;
  }
}
