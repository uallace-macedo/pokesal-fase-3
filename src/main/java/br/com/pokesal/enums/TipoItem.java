package br.com.pokesal.enums;

/** Tipos de item que um treinador pode usar durante a batalha. */
public enum TipoItem {
  /** Recupera 20 pontos de HP. */
  POCAO("Poção", 20),
  /** Recupera 40 pontos de HP. */
  SUPER_POCAO("Super Poção", 40),
  /** Remove o envenenamento. */
  ANTIDOTO("Antídoto", 0);

  private final String nome;
  private final int cura;

  TipoItem(String nome, int cura) {
    this.nome = nome;
    this.cura = cura;
  }

  /**
   * Retorna o nome de exibição do item.
   *
   * @return nome do item
   */
  public String getNome() {
    return nome;
  }

  /**
   * Retorna a quantidade de HP recuperada pelo item.
   *
   * @return pontos de cura, ou zero se o item não cura
   */
  public int getCura() {
    return cura;
  }
}
