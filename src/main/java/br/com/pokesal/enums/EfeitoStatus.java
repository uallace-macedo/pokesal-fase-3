package br.com.pokesal.enums;

/** Efeitos de status que um Pokésal pode sofrer, com duração e consequências. */
public enum EfeitoStatus {
  /** Ausência de efeito de status. */
  NENHUM(0, 0, 0, 0),
  /** Causa 5 de dano ao final de cada turno e reduz o ataque em 5, por 2 turnos. */
  QUEIMADO(2, 5, 5, 0),
  /** Causa 5 de dano ao final de cada turno, por 3 turnos. */
  ENVENENADO(3, 5, 0, 0),
  /** Reduz a velocidade em 10 enquanto estiver ativo, por 2 turnos. */
  PARALISADO(2, 0, 0, 10);

  private final int duracao;
  private final int danoPorTurno;
  private final int reducaoAtaque;
  private final int reducaoVelocidade;

  EfeitoStatus(int duracao, int danoPorTurno, int reducaoAtaque, int reducaoVelocidade) {
    this.duracao = duracao;
    this.danoPorTurno = danoPorTurno;
    this.reducaoAtaque = reducaoAtaque;
    this.reducaoVelocidade = reducaoVelocidade;
  }

  /**
   * Retorna a duração do efeito.
   *
   * @return quantidade de turnos em que o efeito permanece ativo
   */
  public int getDuracao() {
    return duracao;
  }

  /**
   * Retorna o dano causado ao final de cada turno.
   *
   * @return dano por turno
   */
  public int getDanoPorTurno() {
    return danoPorTurno;
  }

  /**
   * Retorna a redução de ataque causada pelo efeito.
   *
   * @return pontos de ATK subtraídos enquanto o efeito estiver ativo
   */
  public int getReducaoAtaque() {
    return reducaoAtaque;
  }

  /**
   * Retorna a redução de velocidade causada pelo efeito.
   *
   * @return pontos de SPD subtraídos enquanto o efeito estiver ativo
   */
  public int getReducaoVelocidade() {
    return reducaoVelocidade;
  }
}
