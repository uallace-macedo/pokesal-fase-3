package br.com.pokesal.catalogo;

import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.TipoElemental;
import br.com.pokesal.modelo.Ataque;
import br.com.pokesal.modelo.Pokesal;
import java.util.List;

/** Catálogo dos pokesals iniciais disponíveis. */
public final class CatalogoPokesal {

  private static final int DANO_INVESTIDA = 15;
  private static final int DANO_ELEMENTAL = 25;
  private static final int DANO_STATUS = 10;
  private static final int DANO_STATUS_QUEIMADURA = 15;

  private static final int HP_BULBASAL = 110;
  private static final int ATAQUE_BULBASAL = 45;
  private static final int DEFESA_BULBASAL = 50;
  private static final int VELOCIDADE_BULBASAL = 40;

  private static final int HP_CHARSAL = 95;
  private static final int ATAQUE_CHARSAL = 55;
  private static final int DEFESA_CHARSAL = 35;
  private static final int VELOCIDADE_CHARSAL = 55;

  private static final int HP_SQUIRTSAL = 105;
  private static final int ATAQUE_SQUIRTSAL = 40;
  private static final int DEFESA_SQUIRTSAL = 55;
  private static final int VELOCIDADE_SQUIRTSAL = 35;

  private static final int HP_CHIKOSAL = 100;
  private static final int ATAQUE_CHIKOSAL = 40;
  private static final int DEFESA_CHIKOSAL = 50;
  private static final int VELOCIDADE_CHIKOSAL = 45;

  private static final int HP_CYNDASAL = 90;
  private static final int ATAQUE_CYNDASAL = 55;
  private static final int DEFESA_CYNDASAL = 35;
  private static final int VELOCIDADE_CYNDASAL = 60;

  private static final int HP_TOTOSAL = 100;
  private static final int ATAQUE_TOTOSAL = 45;
  private static final int DEFESA_TOTOSAL = 45;
  private static final int VELOCIDADE_TOTOSAL = 50;

  private CatalogoPokesal() {}

  /**
   * Cria os seis pokesals iniciais.
   *
   * @return lista de pokesals disponíveis
   */
  public static List<Pokesal> criarIniciais() {
    return List.of(
        criarBulbaSal(),
        criarCharSal(),
        criarSquirtSal(),
        criarChikoSal(),
        criarCyndaSal(),
        criarTotoSal());
  }

  private static Pokesal criarBulbaSal() {
    TipoElemental tipo = TipoElemental.PLANTA;

    return criarPokesal(
        "BulbaSal",
        tipo,
        HP_BULBASAL,
        ATAQUE_BULBASAL,
        DEFESA_BULBASAL,
        VELOCIDADE_BULBASAL,
        ataqueNormal(tipo),
        new Ataque("Folha Navalha", tipo, DANO_ELEMENTAL, EfeitoStatus.NENHUM),
        new Ataque("Pó Venenoso", tipo, DANO_STATUS, EfeitoStatus.ENVENENADO));
  }

  private static Pokesal criarCharSal() {
    TipoElemental tipo = TipoElemental.FOGO;

    return criarPokesal(
        "CharSal",
        tipo,
        HP_CHARSAL,
        ATAQUE_CHARSAL,
        DEFESA_CHARSAL,
        VELOCIDADE_CHARSAL,
        ataqueNormal(tipo),
        new Ataque("Brasas", tipo, DANO_ELEMENTAL, EfeitoStatus.NENHUM),
        new Ataque("Chama Ardente", tipo, DANO_STATUS_QUEIMADURA, EfeitoStatus.QUEIMADO));
  }

  private static Pokesal criarSquirtSal() {
    TipoElemental tipo = TipoElemental.AGUA;

    return criarPokesal(
        "SquirtSal",
        tipo,
        HP_SQUIRTSAL,
        ATAQUE_SQUIRTSAL,
        DEFESA_SQUIRTSAL,
        VELOCIDADE_SQUIRTSAL,
        ataqueNormal(tipo),
        new Ataque("Jato d'Água", tipo, DANO_ELEMENTAL, EfeitoStatus.NENHUM),
        new Ataque("Água Paralisante", tipo, DANO_STATUS, EfeitoStatus.PARALISADO));
  }

  private static Pokesal criarChikoSal() {
    TipoElemental tipo = TipoElemental.PLANTA;

    return criarPokesal(
        "ChikoSal",
        tipo,
        HP_CHIKOSAL,
        ATAQUE_CHIKOSAL,
        DEFESA_CHIKOSAL,
        VELOCIDADE_CHIKOSAL,
        ataqueNormal(tipo),
        new Ataque("Chicote de Folhas", tipo, DANO_ELEMENTAL, EfeitoStatus.NENHUM),
        new Ataque("Esporos Venenosos", tipo, DANO_STATUS, EfeitoStatus.ENVENENADO));
  }

  private static Pokesal criarCyndaSal() {
    TipoElemental tipo = TipoElemental.FOGO;

    return criarPokesal(
        "CyndaSal",
        tipo,
        HP_CYNDASAL,
        ATAQUE_CYNDASAL,
        DEFESA_CYNDASAL,
        VELOCIDADE_CYNDASAL,
        ataqueNormal(tipo),
        new Ataque("Roda de Fogo", tipo, DANO_ELEMENTAL, EfeitoStatus.NENHUM),
        new Ataque("Fumaça Ardente", tipo, DANO_STATUS_QUEIMADURA, EfeitoStatus.QUEIMADO));
  }

  private static Pokesal criarTotoSal() {
    TipoElemental tipo = TipoElemental.AGUA;

    return criarPokesal(
        "TotoSal",
        tipo,
        HP_TOTOSAL,
        ATAQUE_TOTOSAL,
        DEFESA_TOTOSAL,
        VELOCIDADE_TOTOSAL,
        ataqueNormal(tipo),
        new Ataque("Jato d'Água", tipo, DANO_ELEMENTAL, EfeitoStatus.NENHUM),
        new Ataque("Pulso Paralisante", tipo, DANO_STATUS, EfeitoStatus.PARALISADO));
  }

  private static Ataque ataqueNormal(TipoElemental tipo) {
    return new Ataque("Investida", tipo, DANO_INVESTIDA, EfeitoStatus.NENHUM, false);
  }

  private static Pokesal criarPokesal(
      String nome,
      TipoElemental tipo,
      int hp,
      int ataque,
      int defesa,
      int velocidade,
      Ataque ataqueNormal,
      Ataque ataqueElemental,
      Ataque ataqueStatus) {

    return new Pokesal(
        nome,
        tipo,
        hp,
        ataque,
        defesa,
        velocidade,
        List.of(ataqueNormal, ataqueElemental, ataqueStatus));
  }
}
