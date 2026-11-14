package br.com.pokesal.modelo;

import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.TipoItem;

/** Representa um item que pode ser utilizado durante uma batalha. */
public class Item {

  private static final int CURA_POCAO = 20;
  private static final int CURA_SUPER_POCAO = 40;
  private static final int QUANTIDADE_INICIAL_MINIMA = 0;

  private final String nome;
  private final TipoItem tipo;
  private int quantidade;

  /**
   * Cria um item com nome, tipo e quantidade inicial.
   *
   * @param nome nome do item
   * @param tipo tipo do item
   * @param quantidade quantidade disponível
   */
  public Item(String nome, TipoItem tipo, int quantidade) {
    if (nome == null || nome.isBlank()) {
      throw new IllegalArgumentException("O nome do item não pode ser vazio");
    }

    if (tipo == null) {
      throw new IllegalArgumentException("O tipo do item é obrigatório");
    }

    if (quantidade < QUANTIDADE_INICIAL_MINIMA) {
      throw new IllegalArgumentException("A quantidade não pode ser negativa");
    }

    this.nome = nome;
    this.tipo = tipo;
    this.quantidade = quantidade;
  }

  public String getNome() {
    return nome;
  }

  public TipoItem getTipo() {
    return tipo;
  }

  public int getQuantidade() {
    return quantidade;
  }

  public boolean estaDisponivel() {
    return quantidade > QUANTIDADE_INICIAL_MINIMA;
  }

  /**
   * Verifica se o item pode ser utilizado no pokesal informado.
   *
   * @param alvo pokesal que receberá o efeito
   * @return true se o item puder ser utilizado
   */
  public boolean podeSerUsado(Pokesal alvo) {
    if (alvo == null || !estaDisponivel() || !alvo.estaVivo()) {
      return false;
    }

    return switch (tipo) {
      case POCAO, SUPER_POCAO -> alvo.getHpAtual() < alvo.getHpMaximo();
      case ANTIDOTO -> alvo.getEfeitoStatus() == EfeitoStatus.ENVENENADO;
    };
  }

  /**
   * Aplica o efeito do item e consome uma unidade.
   *
   * @param alvo pokesal que receberá o efeito
   */
  public void aplicar(Pokesal alvo) {
    if (!podeSerUsado(alvo)) {
      throw new IllegalStateException("O item não pode ser utilizado");
    }

    switch (tipo) {
      case POCAO -> alvo.recuperarVida(CURA_POCAO);
      case SUPER_POCAO -> alvo.recuperarVida(CURA_SUPER_POCAO);
      case ANTIDOTO -> alvo.removerStatus();
    }

    consumir();
  }

  /**
   * Cria uma cópia independente do item.
   *
   * @return cópia do item
   */
  public Item copiar() {
    return new Item(nome, tipo, quantidade);
  }

  /** Consome uma unidade do item. */
  boolean consumir() {
    if (!estaDisponivel()) {
      return false;
    }

    quantidade--;
    return true;
  }
}
