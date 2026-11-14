package br.com.pokesal.modelo;

import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.TipoElemental;

/**
 * Golpe que um Pokésal pode executar.
 *
 * @param nome nome do golpe
 * @param tipo tipo elemental do golpe
 * @param danoBase dano base do golpe, maior ou igual a zero
 * @param precisao chance de acerto, de 0 a 100
 * @param efeitoStatus efeito aplicado ao alvo quando o golpe acerta, ou {@code NENHUM}
 */
public record Ataque(
        String nome, TipoElemental tipo, int danoBase, int precisao, EfeitoStatus efeitoStatus) {

  /** Precisão mínima aceita. */
  public static final int PRECISAO_MINIMA = 0;

  /** Precisão máxima aceita. */
  public static final int PRECISAO_MAXIMA = 100;

  /**
   * Valida os dados do golpe.
   *
   * @throws IllegalArgumentException se o dano base for negativo ou a precisão estiver fora de 0 a
   *     100
   */
  public Ataque {
    if (danoBase < 0) {
      throw new IllegalArgumentException("O dano base não pode ser negativo.");
    }
    if (precisao < PRECISAO_MINIMA || precisao > PRECISAO_MAXIMA) {
      throw new IllegalArgumentException("A precisão deve estar entre 0 e 100.");
    }
  }
}
