package br.com.pokesal.enums;

/** Terrenos possíveis de uma batalha e seus efeitos sobre o dano. */
public enum TipoTerreno {
  /** Terreno sem efeito (por exemplo, Asfalto Quente durante a noite). */
  SEM_EFEITO("Sem efeito", null, 1.0),
  /** Aumenta em 15% o dano dos golpes de Fogo. */
  ASFALTO_QUENTE("Asfalto Quente", TipoElemental.FOGO, 1.15),
  /** Aumenta em 10% o dano dos golpes de Água. */
  POCA_DAGUA("Poça d'água", TipoElemental.AGUA, 1.10),
  /** Pokésals de Planta recuperam 5% do HP máximo ao final de cada turno. */
  CANTEIRO_CENTRAL("Canteiro Central", null, 1.0);

  private final String nome;
  private final TipoElemental tipoBeneficiado;
  private final double bonusDano;

  TipoTerreno(String nome, TipoElemental tipoBeneficiado, double bonusDano) {
    this.nome = nome;
    this.tipoBeneficiado = tipoBeneficiado;
    this.bonusDano = bonusDano;
  }

  /**
   * Retorna o nome de exibição do terreno.
   *
   * @return nome do terreno
   */
  public String getNome() {
    return nome;
  }

  /**
   * Calcula o multiplicador de dano que o terreno aplica a um ataque.
   *
   * @param tipoAtaque tipo elemental do ataque
   * @return multiplicador do terreno, ou 1,0 se o terreno não beneficia esse tipo
   */
  public double multiplicadorDano(TipoElemental tipoAtaque) {
    if (tipoBeneficiado != null && tipoBeneficiado == tipoAtaque) {
      return bonusDano;
    }
    return 1.0;
  }
}
