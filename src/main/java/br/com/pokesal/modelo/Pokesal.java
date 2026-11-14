package br.com.pokesal.modelo;

import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.TipoElemental;
import java.util.List;
import java.util.Objects;

/** Representa um pokesal e seus atributos durante uma batalha. */
public class Pokesal {

  private static final int VALOR_MINIMO = 0;
  private static final int REDUCAO_ATAQUE_QUEIMADO = 5;
  private static final int REDUCAO_VELOCIDADE_PARALISADO = 10;

  private final String nome;
  private final TipoElemental tipo;
  private final int hpMaximo;
  private final int ataque;
  private final int defesa;
  private final int velocidade;
  private final List<Ataque> ataques;

  private int hpAtual;
  private EfeitoStatus efeitoStatus;
  private int turnosStatus;

  /**
   * Cria um pokesal com seus atributos e ataques.
   *
   * @param nome nome do pokesal
   * @param tipo tipo elemental
   * @param hpMaximo pontos de vida máximos
   * @param ataque atributo de ataque
   * @param defesa atributo de defesa
   * @param velocidade atributo de velocidade
   * @param ataques lista de ataques disponíveis
   */
  public Pokesal(
      String nome,
      TipoElemental tipo,
      int hpMaximo,
      int ataque,
      int defesa,
      int velocidade,
      List<Ataque> ataques) {

    if (nome == null || nome.isBlank()) {
      throw new IllegalArgumentException("O nome não pode ser vazio");
    }

    if (tipo == null || ataques == null) {
      throw new IllegalArgumentException("Tipo e ataques são obrigatórios");
    }

    if (hpMaximo <= VALOR_MINIMO
        || ataque < VALOR_MINIMO
        || defesa < VALOR_MINIMO
        || velocidade < VALOR_MINIMO) {
      throw new IllegalArgumentException("Os atributos do pokesal são inválidos");
    }

    if (ataques.size() != 3) {
      throw new IllegalArgumentException("O pokesal deve possuir exatamente três ataques válidos");
    }

    if (ataques.stream().anyMatch(Objects::isNull)) {
      throw new IllegalArgumentException("A lista de ataques não pode conter valores nulos");
    }

    this.nome = nome;
    this.tipo = tipo;
    this.hpMaximo = hpMaximo;
    this.ataque = ataque;
    this.defesa = defesa;
    this.velocidade = velocidade;
    this.ataques = List.copyOf(ataques);

    this.hpAtual = hpMaximo;
    this.efeitoStatus = EfeitoStatus.NENHUM;
    this.turnosStatus = VALOR_MINIMO;
  }

  public String getNome() {
    return nome;
  }

  public TipoElemental getTipo() {
    return tipo;
  }

  public int getHpMaximo() {
    return hpMaximo;
  }

  public int getHpAtual() {
    return hpAtual;
  }

  public int getAtaque() {
    return ataque;
  }

  /**
   * Retorna o ataque considerando o efeito de status atual.
   *
   * @return ataque efetivo
   */
  public int getAtaqueEfetivo() {
    if (efeitoStatus == EfeitoStatus.QUEIMADO) {
      return Math.max(VALOR_MINIMO, ataque - REDUCAO_ATAQUE_QUEIMADO);
    }

    return ataque;
  }

  public int getDefesa() {
    return defesa;
  }

  public int getVelocidade() {
    return velocidade;
  }

  /**
   * Retorna a velocidade considerando o efeito de status atual.
   *
   * @return velocidade efetiva
   */
  public int getVelocidadeEfetiva() {
    if (efeitoStatus == EfeitoStatus.PARALISADO) {
      return Math.max(VALOR_MINIMO, velocidade - REDUCAO_VELOCIDADE_PARALISADO);
    }

    return velocidade;
  }

  public List<Ataque> getAtaques() {
    return ataques;
  }

  public EfeitoStatus getEfeitoStatus() {
    return efeitoStatus;
  }

  public int getTurnosStatus() {
    return turnosStatus;
  }

  public boolean estaVivo() {
    return hpAtual > VALOR_MINIMO;
  }

  /**
   * Aplica dano sem permitir que o HP fique negativo.
   *
   * @param dano quantidade de dano
   */
  public void receberDano(int dano) {
    if (dano < VALOR_MINIMO) {
      throw new IllegalArgumentException("O dano não pode ser negativo");
    }

    if (dano >= hpAtual) {
      hpAtual = VALOR_MINIMO;
      return;
    }

    hpAtual -= dano;
  }

  /**
   * Recupera HP sem ultrapassar o máximo e sem risco de overflow.
   *
   * @param quantidade quantidade de HP recuperado
   */
  public void recuperarVida(int quantidade) {
    if (quantidade < VALOR_MINIMO) {
      throw new IllegalArgumentException("A recuperação não pode ser negativa");
    }

    if (quantidade >= hpMaximo - hpAtual) {
      hpAtual = hpMaximo;
      return;
    }

    hpAtual += quantidade;
  }

  /**
   * Aplica um efeito de status se o pokesal não possuir outro ativo.
   *
   * @param efeito efeito a aplicar
   * @param duracao duração em turnos
   * @return true se o efeito foi aplicado
   */
  public boolean aplicarStatus(EfeitoStatus efeito, int duracao) {
    if (efeito == null || efeito == EfeitoStatus.NENHUM || duracao <= VALOR_MINIMO) {
      throw new IllegalArgumentException("O efeito e a duração são inválidos");
    }

    if (efeitoStatus != EfeitoStatus.NENHUM) {
      return false;
    }

    efeitoStatus = efeito;
    turnosStatus = duracao;
    return true;
  }

  /** Reduz a duração do status e o remove quando termina. */
  public void avancarTurnoStatus() {
    if (efeitoStatus == EfeitoStatus.NENHUM) {
      return;
    }

    turnosStatus--;

    if (turnosStatus <= VALOR_MINIMO) {
      removerStatus();
    }
  }

  /** Remove o efeito de status atual. */
  public void removerStatus() {
    efeitoStatus = EfeitoStatus.NENHUM;
    turnosStatus = VALOR_MINIMO;
  }
}
