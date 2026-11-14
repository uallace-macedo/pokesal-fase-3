package br.com.pokesal.batalha;

import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.Terreno;
import br.com.pokesal.enums.TipoElemental;
import br.com.pokesal.modelo.Ataque;
import br.com.pokesal.modelo.Pokesal;
import br.com.pokesal.modelo.Treinador;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

/** Coordena as regras e os cálculos de combate entre pokesal. */
public class Batalha {

  private static final int DANO_MINIMO = 1;
  private static final int DANO_STATUS_POR_TURNO = 5;
  private static final int PERCENTUAL = 100;

  private static final double MULTIPLICADOR_VANTAGEM = 2.0;
  private static final double MULTIPLICADOR_DESVANTAGEM = 0.5;
  private static final double MULTIPLICADOR_NEUTRO = 1.0;
  private static final double MULTIPLICADOR_ASFALTO_QUENTE = 1.15;
  private static final double MULTIPLICADOR_POCA_DAGUA = 1.10;
  private static final double MULTIPLICADOR_CRITICO = 1.5;
  private static final double CHANCE_CRITICO = 0.10;
  private static final double RECUPERACAO_CANTEIRO = 0.05;

  private final Treinador treinador1;
  private final Treinador treinador2;
  private final Terreno terreno;
  private final Random random;

  /**
   * Cria uma batalha sem participantes definidos.
   *
   * @param terreno terreno da batalha
   */
  public Batalha(Terreno terreno) {
    this(terreno, new Random());
  }

  /**
   * Cria uma batalha sem participantes definidos.
   *
   * @param terreno terreno da batalha
   * @param random gerador aleatório
   */
  public Batalha(Terreno terreno, Random random) {
    this(terreno, random, null, null);
  }

  /**
   * Cria uma batalha com dois treinadores.
   *
   * @param treinador1 primeiro treinador
   * @param treinador2 segundo treinador
   * @param terreno terreno da batalha
   */
  public Batalha(Treinador treinador1, Treinador treinador2, Terreno terreno) {
    this(treinador1, treinador2, terreno, new Random());
  }

  /**
   * Cria uma batalha com dois treinadores e gerador aleatório.
   *
   * @param treinador1 primeiro treinador
   * @param treinador2 segundo treinador
   * @param terreno terreno da batalha
   * @param random gerador aleatório
   */
  public Batalha(Treinador treinador1, Treinador treinador2, Terreno terreno, Random random) {
    this(
        terreno,
        random,
        Objects.requireNonNull(treinador1, "O primeiro treinador é obrigatório"),
        Objects.requireNonNull(treinador2, "O segundo treinador é obrigatório"));
  }

  private Batalha(Terreno terreno, Random random, Treinador treinador1, Treinador treinador2) {
    this.terreno = Objects.requireNonNull(terreno, "O terreno é obrigatório");
    this.random = Objects.requireNonNull(random, "O gerador aleatório é obrigatório");
    this.treinador1 = treinador1;
    this.treinador2 = treinador2;
  }

  public Terreno getTerreno() {
    return terreno;
  }

  /**
   * Calcula o multiplicador de vantagem elemental.
   *
   * @param ataque tipo elemental do ataque
   * @param defesa tipo elemental do defensor
   * @return multiplicador de dano
   */
  public double calcularMultiplicadorElemental(TipoElemental ataque, TipoElemental defesa) {

    Objects.requireNonNull(ataque, "O tipo de ataque é obrigatório");
    Objects.requireNonNull(defesa, "O tipo de defesa é obrigatório");

    if (ataque == defesa) {
      return MULTIPLICADOR_NEUTRO;
    }

    boolean vantagem =
        (ataque == TipoElemental.FOGO && defesa == TipoElemental.PLANTA)
            || (ataque == TipoElemental.PLANTA && defesa == TipoElemental.AGUA)
            || (ataque == TipoElemental.AGUA && defesa == TipoElemental.FOGO);

    return vantagem ? MULTIPLICADOR_VANTAGEM : MULTIPLICADOR_DESVANTAGEM;
  }

  /**
   * Calcula o multiplicador de dano fornecido pelo terreno.
   *
   * @param tipoAtaque tipo elemental do ataque
   * @return multiplicador de terreno
   */
  public double calcularMultiplicadorTerreno(TipoElemental tipoAtaque) {
    Objects.requireNonNull(tipoAtaque, "O tipo de ataque é obrigatório");

    if (terreno == Terreno.ASFALTO_QUENTE && tipoAtaque == TipoElemental.FOGO) {
      return MULTIPLICADOR_ASFALTO_QUENTE;
    }

    if (terreno == Terreno.POCA_DAGUA && tipoAtaque == TipoElemental.AGUA) {
      return MULTIPLICADOR_POCA_DAGUA;
    }

    return MULTIPLICADOR_NEUTRO;
  }

  /**
   * Calcula o dano considerando ataque, defesa, elemento, terreno e crítico.
   *
   * @param ataque ataque utilizado
   * @param atacante pokesal atacante
   * @param defensor pokesal defensor
   * @return dano calculado
   */
  public int calcularDano(Ataque ataque, Pokesal atacante, Pokesal defensor) {

    Objects.requireNonNull(ataque, "O ataque é obrigatório");
    Objects.requireNonNull(atacante, "O atacante é obrigatório");
    Objects.requireNonNull(defensor, "O defensor é obrigatório");

    int danoBase =
        Math.max(
            DANO_MINIMO, ataque.getDanoBase() + atacante.getAtaqueEfetivo() - defensor.getDefesa());

    double multiplicadorElemental = MULTIPLICADOR_NEUTRO;
    double multiplicadorTerreno = MULTIPLICADOR_NEUTRO;

    if (ataque.isElemental()) {
      multiplicadorElemental = calcularMultiplicadorElemental(ataque.getTipo(), defensor.getTipo());

      multiplicadorTerreno = calcularMultiplicadorTerreno(ataque.getTipo());
    }

    double multiplicadorCritico =
        random.nextInt(PERCENTUAL) < CHANCE_CRITICO * PERCENTUAL
            ? MULTIPLICADOR_CRITICO
            : MULTIPLICADOR_NEUTRO;

    return Math.max(
        DANO_MINIMO,
        (int)
            Math.round(
                danoBase * multiplicadorElemental * multiplicadorTerreno * multiplicadorCritico));
  }

  /**
   * Verifica a iniciativa entre dois treinadores.
   *
   * @param jogador1 primeiro treinador
   * @param jogador2 segundo treinador
   * @return true se o primeiro treinador atacar primeiro
   */
  public boolean jogador1AtacaPrimeiro(Treinador jogador1, Treinador jogador2) {

    Objects.requireNonNull(jogador1, "O primeiro treinador é obrigatório");
    Objects.requireNonNull(jogador2, "O segundo treinador é obrigatório");

    return jogador1.getPokesal().getVelocidadeEfetiva()
        >= jogador2.getPokesal().getVelocidadeEfetiva();
  }

  /**
   * Verifica a iniciativa utilizando os participantes da batalha.
   *
   * @return true se o primeiro treinador atacar primeiro
   */
  public boolean jogador1AtacaPrimeiro() {
    validarParticipantes();
    return jogador1AtacaPrimeiro(treinador1, treinador2);
  }

  /**
   * Executa um ataque e aplica seu efeito de status.
   *
   * @param ataque ataque utilizado
   * @param atacante pokesal atacante
   * @param defensor pokesal defensor
   * @return dano causado
   */
  public int executarAtaque(Ataque ataque, Pokesal atacante, Pokesal defensor) {

    int dano = calcularDano(ataque, atacante, defensor);
    defensor.receberDano(dano);

    if (defensor.estaVivo()) {
      aplicarEfeitoStatus(ataque, defensor);
    }

    return dano;
  }

  private void aplicarEfeitoStatus(Ataque ataque, Pokesal defensor) {
    EfeitoStatus efeito = ataque.getEfeitoStatus();

    switch (efeito) {
      case QUEIMADO -> defensor.aplicarStatus(efeito, 2);
      case ENVENENADO -> defensor.aplicarStatus(efeito, 3);
      case PARALISADO -> defensor.aplicarStatus(efeito, 2);
      case NENHUM -> {
        // Nenhum efeito adicional.
      }
    }
  }

  /**
   * Aplica a recuperação fornecida pelo terreno.
   *
   * @param pokesal pokesal afetado
   */
  public void aplicarEfeitoTerrenoNoFimDoTurno(Pokesal pokesal) {
    Objects.requireNonNull(pokesal, "O pokesal é obrigatório");

    if (!pokesal.estaVivo()) {
      return;
    }

    if (terreno == Terreno.CANTEIRO_CENTRAL && pokesal.getTipo() == TipoElemental.PLANTA) {

      int recuperacao = (int) Math.round(pokesal.getHpMaximo() * RECUPERACAO_CANTEIRO);

      pokesal.recuperarVida(recuperacao);
    }
  }

  /**
   * Processa o dano periódico de status e avança sua duração.
   *
   * @param pokesal pokesal afetado
   */
  public void processarStatusNoFimDoTurno(Pokesal pokesal) {
    Objects.requireNonNull(pokesal, "O pokesal é obrigatório");

    if (!pokesal.estaVivo()) {
      return;
    }

    switch (pokesal.getEfeitoStatus()) {
      case QUEIMADO, ENVENENADO -> pokesal.receberDano(DANO_STATUS_POR_TURNO);
      case PARALISADO, NENHUM -> {
        // Esses status não causam dano periódico.
      }
    }

    pokesal.avancarTurnoStatus();
  }

  /**
   * Processa todos os efeitos do final do turno para um pokesal.
   *
   * @param pokesal pokesal afetado
   */
  public void processarFimDoTurno(Pokesal pokesal) {
    Objects.requireNonNull(pokesal, "O pokesal é obrigatório");

    processarStatusNoFimDoTurno(pokesal);
    aplicarEfeitoTerrenoNoFimDoTurno(pokesal);
  }

  /**
   * Verifica se existe um treinador vencedor.
   *
   * @return treinador vencedor ou Optional vazio se a batalha continuar
   */
  public Optional<Treinador> verificarVencedor() {
    validarParticipantes();

    boolean treinador1Vivo = treinador1.getPokesal().estaVivo();
    boolean treinador2Vivo = treinador2.getPokesal().estaVivo();

    if (treinador1Vivo && !treinador2Vivo) {
      return Optional.of(treinador1);
    }

    if (!treinador1Vivo && treinador2Vivo) {
      return Optional.of(treinador2);
    }

    return Optional.empty();
  }

  private void validarParticipantes() {
    if (treinador1 == null || treinador2 == null) {
      throw new IllegalStateException("Os participantes da batalha não foram definidos");
    }
  }
}
