package br.com.pokesal.enums;

/**
 * Tipos elementais de Pokésals e de ataques.
 *
 * <p>Fogo vence Planta, Planta vence Água e Água vence Fogo. O tipo {@link #NORMAL} é usado apenas
 * em ataques e não tem vantagem nem desvantagem contra nenhum tipo.
 */
public enum TipoElemental {
  /** Tipo fogo. */
  FOGO,
  /** Tipo água. */
  AGUA,
  /** Tipo planta. */
  PLANTA,
  /** Tipo sem vantagem elemental, usado no ataque normal. */
  NORMAL;

  private static final double SUPER_EFETIVO = 2.0;
  private static final double POUCO_EFETIVO = 0.5;
  private static final double NEUTRO = 1.0;

  /**
   * Calcula o multiplicador de dano de um ataque deste tipo contra um defensor.
   *
   * @param defensor tipo elemental do Pokésal que recebe o ataque
   * @return 2,0 se houver vantagem, 0,5 se houver desvantagem e 1,0 nos demais casos
   */
  public double multiplicadorContra(TipoElemental defensor) {
    if (venceu(this, defensor)) {
      return SUPER_EFETIVO;
    }
    if (venceu(defensor, this)) {
      return POUCO_EFETIVO;
    }
    return NEUTRO;
  }

  private static boolean venceu(TipoElemental atacante, TipoElemental defensor) {
    return (atacante == FOGO && defensor == PLANTA)
        || (atacante == PLANTA && defensor == AGUA)
        || (atacante == AGUA && defensor == FOGO);
  }
}
