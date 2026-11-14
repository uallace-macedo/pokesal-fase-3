package br.com.pokesal.modelo;

import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.TipoItem;

/** Item de uso único que um treinador pode aplicar ao seu Pokésal. */
public class Item {
  private final TipoItem tipo;
  private boolean usado;

  /**
   * Cria um item ainda não usado.
   *
   * @param tipo tipo do item
   */
  public Item(TipoItem tipo) {
    this.tipo = tipo;
  }

  /**
   * Indica se o item ainda pode ser usado.
   *
   * @return {@code true} se o item não foi usado
   */
  public boolean podeSerUsado() {
    return !usado;
  }

  /**
   * Aplica o item ao Pokésal alvo e o marca como usado.
   *
   * @param alvo Pokésal que recebe o efeito
   * @throws IllegalStateException se o item já foi usado
   */
  public void aplicar(Pokesal alvo) {
    if (usado) {
      throw new IllegalStateException("O item já foi usado.");
    }
    if (tipo == TipoItem.ANTIDOTO) {
      if (alvo.getEfeitoStatus() == EfeitoStatus.ENVENENADO) {
        alvo.removerStatus();
      }
    } else {
      alvo.recuperarVida(tipo.getCura());
    }
    usado = true;
  }

  /** Torna o item utilizável novamente, para uma nova batalha. */
  public void reiniciar() {
    usado = false;
  }

  /**
   * Retorna o tipo do item.
   *
   * @return tipo do item
   */
  public TipoItem getTipo() {
    return tipo;
  }
}
