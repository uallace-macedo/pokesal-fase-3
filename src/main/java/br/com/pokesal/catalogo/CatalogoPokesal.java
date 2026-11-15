package br.com.pokesal.catalogo;

import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.TipoElemental;
import br.com.pokesal.modelo.Ataque;
import br.com.pokesal.modelo.Pokesal;
import java.util.List;

/** Catálogo dos seis Pokésals iniciais disponíveis para escolha. */
public final class CatalogoPokesal {
  private static final int DANO_NORMAL = 15;
  private static final int DANO_ELEMENTAL = 25;
  private static final int PRECISAO_NORMAL = 100;
  private static final int PRECISAO_ELEMENTAL = 90;
  private static final int PRECISAO_STATUS = 80;

  private CatalogoPokesal() {}

  /**
   * Cria novas instâncias dos seis Pokésals iniciais, com HP cheio.
   *
   * @return lista com BulbaSal, CharSal, SquirtSal, ChikoSal, CyndaSal e TotoSal
   */
  public static List<Pokesal> criarIniciais() {
    return List.of(
        criar(
            "BulbaSal",
            TipoElemental.PLANTA,
            new int[] {110, 45, 50, 40},
            "Folha Navalha",
            "Pó Venenoso",
            10,
            EfeitoStatus.ENVENENADO),
        criar(
            "CharSal",
            TipoElemental.FOGO,
            new int[] {95, 55, 35, 55},
            "Brasas",
            "Chama Ardente",
            15,
            EfeitoStatus.QUEIMADO),
        criar(
            "SquirtSal",
            TipoElemental.AGUA,
            new int[] {105, 40, 55, 35},
            "Jato d'Água",
            "Água Paralisante",
            10,
            EfeitoStatus.PARALISADO),
        criar(
            "ChikoSal",
            TipoElemental.PLANTA,
            new int[] {100, 40, 50, 45},
            "Chicote de Folhas",
            "Esporos Venenosos",
            10,
            EfeitoStatus.ENVENENADO),
        criar(
            "CyndaSal",
            TipoElemental.FOGO,
            new int[] {90, 55, 35, 60},
            "Roda de Fogo",
            "Fumaça Ardente",
            15,
            EfeitoStatus.QUEIMADO),
        criar(
            "TotoSal",
            TipoElemental.AGUA,
            new int[] {100, 45, 45, 50},
            "Jato d'Água",
            "Pulso Paralisante",
            10,
            EfeitoStatus.PARALISADO));
  }

  /** Atributos na ordem: HP, ATK, DEF, SPD. */
  private static Pokesal criar(
      String nome,
      TipoElemental tipo,
      int[] atributos,
      String nomeElemental,
      String nomeStatus,
      int danoStatus,
      EfeitoStatus efeito) {
    List<Ataque> ataques =
        List.of(
            new Ataque(
                "Investida",
                TipoElemental.NORMAL,
                DANO_NORMAL,
                PRECISAO_NORMAL,
                EfeitoStatus.NENHUM),
            new Ataque(
                nomeElemental, tipo, DANO_ELEMENTAL, PRECISAO_ELEMENTAL, EfeitoStatus.NENHUM),
            new Ataque(nomeStatus, tipo, danoStatus, PRECISAO_STATUS, efeito));
    return new Pokesal(nome, tipo, atributos[0], atributos[1], atributos[2], atributos[3], ataques);
  }
}
