package br.com.pokesal.batalha;

import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.TipoElemental;
import br.com.pokesal.enums.TipoTerreno;
import br.com.pokesal.modelo.Acao;
import br.com.pokesal.modelo.Ataque;
import br.com.pokesal.modelo.Pokesal;
import br.com.pokesal.modelo.Treinador;
import br.com.pokesal.resultado.RepositorioResultados;
import br.com.pokesal.resultado.ResultadoBatalha;
import br.com.pokesal.terreno.ServicoTerreno;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Batalha entre dois treinadores. Todos os colaboradores são recebidos pelo construtor, o que
 * permite substituí-los por mocks nos testes.
 */
public class Batalha {
  private static final Logger LOGGER = Logger.getLogger(Batalha.class.getName());

  private static final int FAIXA_PRECISAO = 100;
  private static final int FAIXA_CRITICO = 16;
  private static final double MULTIPLICADOR_CRITICO = 1.5;
  private static final int DANO_MINIMO = 1;
  private static final int PERCENTUAL_CURA_CANTEIRO = 5;
  private static final int CEM_PORCENTO = 100;
  private static final int SORTEIO_TROPECO_MINIMO = 1;
  private static final int SORTEIO_TROPECO_MAXIMO = 50;
  private static final int LIMITE_TROPECO = 48;

  /** Mensagem exibida quando o Pokésal tropeça. */
  public static final String MENSAGEM_TROPECO = "Seu pokemon tropeçou e errou o ataque";

  private final Treinador treinador1;
  private final Treinador treinador2;
  private final GeradorAleatorio gerador;
  private final RepositorioResultados repositorio;
  private final RegistroBatalha registro;
  private final ContextoBatalha contexto;
  private final TipoTerreno terreno;
  private int turnos;
  private ResultadoBatalha resultado;

  /**
   * Cria a batalha e consulta o terreno uma única vez, antes do primeiro turno.
   *
   * @param treinador1 primeiro jogador (ataca primeiro em caso de empate de velocidade)
   * @param treinador2 segundo jogador
   * @param servicoTerreno serviço que informa o terreno
   * @param gerador fonte de aleatoriedade (precisão e crítico)
   * @param repositorio onde o resultado é salvo ao final
   * @param registro destino dos eventos de cada turno
   * @param contexto estado externo que pode interromper a batalha
   */
  public Batalha(
      Treinador treinador1,
      Treinador treinador2,
      ServicoTerreno servicoTerreno,
      GeradorAleatorio gerador,
      RepositorioResultados repositorio,
      RegistroBatalha registro,
      ContextoBatalha contexto) {
    this.treinador1 = treinador1;
    this.treinador2 = treinador2;
    this.gerador = gerador;
    this.repositorio = repositorio;
    this.registro = registro;
    this.contexto = contexto;
    this.terreno = servicoTerreno.consultar();
  }

  /**
   * Indica quem age primeiro. Em empate de velocidade, o primeiro jogador age primeiro.
   *
   * @return {@code true} se o treinador 1 age primeiro
   */
  public boolean jogador1AtacaPrimeiro() {
    return treinador1.getPokesal().getVelocidadeEfetiva()
        >= treinador2.getPokesal().getVelocidadeEfetiva();
  }

  /**
   * Calcula o dano de um golpe que acertou.
   *
   * <p>Fórmula: {@code max(1, danoBase + ATK efetivo - DEF)}, multiplicado pela vantagem elemental,
   * pelo bônus do terreno e, se crítico, por 1,5; o resultado é arredondado e tem mínimo de 1.
   *
   * @param ataque golpe usado
   * @param atacante Pokésal que ataca
   * @param defensor Pokésal alvo
   * @param critico se o golpe é crítico
   * @return dano a aplicar
   */
  public int calcularDano(Ataque ataque, Pokesal atacante, Pokesal defensor, boolean critico) {
    int bruto = ataque.danoBase() + atacante.getAtaqueEfetivo() - defensor.getDefesa();
    int base = Math.max(DANO_MINIMO, bruto);
    double multiplicador =
        ataque.tipo().multiplicadorContra(defensor.getTipo())
            * terreno.multiplicadorDano(ataque.tipo());
    if (critico) {
      multiplicador *= MULTIPLICADOR_CRITICO;
    }
    return Math.max(DANO_MINIMO, (int) Math.round(base * multiplicador));
  }

  /**
   * Executa um ataque. Primeiro sorteia o tropeço (REQ-02): de 1 a 50, valores de 48 a 50 anulam o
   * ataque, mesmo com precisão 100. Se não tropeçou, sorteia a precisão e, se acertou, o crítico;
   * então aplica o dano e o efeito de status e envia os eventos ao registro.
   *
   * @param ataque golpe usado
   * @param atacante Pokésal que ataca
   * @param defensor Pokésal alvo
   * @return dano causado, zero se o golpe errou
   */
  public int executarAtaque(Ataque ataque, Pokesal atacante, Pokesal defensor) {
    registro.registrarAtaque(atacante, ataque, defensor);
    if (gerador.sortear(SORTEIO_TROPECO_MINIMO, SORTEIO_TROPECO_MAXIMO) >= LIMITE_TROPECO) {
      registro.registrarTropeco(atacante, MENSAGEM_TROPECO);
      registro.registrarDano(defensor, 0, false);
      return 0;
    }
    boolean acertou = gerador.proximoInteiro(FAIXA_PRECISAO) < ataque.precisao();
    int dano = 0;
    boolean critico = false;
    if (acertou) {
      critico = gerador.proximoInteiro(FAIXA_CRITICO) == 0;
      dano = calcularDano(ataque, atacante, defensor, critico);
      defensor.receberDano(dano);
    }
    registro.registrarDano(defensor, dano, critico);
    if (acertou && defensor.estaVivo()) {
      defensor.aplicarStatus(ataque.efeitoStatus());
    }
    return dano;
  }

  /**
   * Executa um turno completo: as ações em ordem de velocidade e, se ninguém foi derrotado, os
   * efeitos de fim de turno. Se houver vencedor, o resultado é salvo no repositório.
   *
   * @param acao1 ação do treinador 1
   * @param acao2 ação do treinador 2
   * @throws IllegalStateException se a batalha já terminou
   * @throws InterrompeBatalhaException se a Equipe Pokemal pediu a parada (REQ-01)
   */
  public void executarTurno(Acao acao1, Acao acao2) {
    if (resultado != null) {
      throw new IllegalStateException("A batalha já terminou.");
    }
    verificarInterrupcao();
    turnos++;
    boolean treinador1Primeiro = jogador1AtacaPrimeiro();
    Treinador primeiro = treinador1Primeiro ? treinador1 : treinador2;
    Treinador segundo = treinador1Primeiro ? treinador2 : treinador1;
    executarAcao(primeiro, segundo, treinador1Primeiro ? acao1 : acao2);
    if (segundo.getPokesal().estaVivo()) {
      verificarInterrupcao();
      executarAcao(segundo, primeiro, treinador1Primeiro ? acao2 : acao1);
    }
    if (verificarVencedor().isEmpty()) {
      for (Treinador treinador : List.of(primeiro, segundo)) {
        processarFimDoTurno(treinador.getPokesal());
        if (!treinador.getPokesal().estaVivo()) {
          break;
        }
      }
    }
    verificarVencedor().ifPresent(this::finalizar);
  }

  /**
   * Aplica ao Pokésal os efeitos de fim de turno: status e terreno.
   *
   * @param pokesal Pokésal afetado
   */
  public void processarFimDoTurno(Pokesal pokesal) {
    EfeitoStatus efeito = pokesal.getEfeitoStatus();
    if (efeito != EfeitoStatus.NENHUM) {
      pokesal.receberDano(efeito.getDanoPorTurno());
      registro.registrarEfeitoStatus(pokesal, efeito, efeito.getDanoPorTurno());
      pokesal.avancarTurnoStatus();
    }
    if (terreno == TipoTerreno.CANTEIRO_CENTRAL
        && pokesal.getTipo() == TipoElemental.PLANTA
        && pokesal.estaVivo()) {
      int hpAntes = pokesal.getHpAtual();
      int cura = pokesal.getHpMaximo() * PERCENTUAL_CURA_CANTEIRO / CEM_PORCENTO;
      cura = Math.max(DANO_MINIMO, cura);
      pokesal.recuperarVida(cura);
      registro.registrarEfeitoTerreno(pokesal, terreno, pokesal.getHpAtual() - hpAntes);
    }
  }

  /**
   * Verifica se algum Pokésal foi derrotado.
   *
   * @return o treinador vencedor, ou vazio se a batalha continua
   */
  public Optional<Treinador> verificarVencedor() {
    if (!treinador1.getPokesal().estaVivo()) {
      return Optional.of(treinador2);
    }
    if (!treinador2.getPokesal().estaVivo()) {
      return Optional.of(treinador1);
    }
    return Optional.empty();
  }

  /**
   * Indica se a batalha terminou.
   *
   * @return {@code true} se há resultado
   */
  public boolean estaTerminada() {
    return resultado != null;
  }

  /**
   * Retorna o resultado da batalha.
   *
   * @return resultado, ou vazio se a batalha não terminou
   */
  public Optional<ResultadoBatalha> getResultado() {
    return Optional.ofNullable(resultado);
  }

  /**
   * Retorna o terreno da batalha.
   *
   * @return terreno consultado na criação
   */
  public TipoTerreno getTerreno() {
    return terreno;
  }

  /**
   * Retorna a quantidade de turnos executados.
   *
   * @return número de turnos
   */
  public int getTurnos() {
    return turnos;
  }

  private void verificarInterrupcao() {
    if (contexto.isParada()) {
      throw new InterrompeBatalhaException();
    }
  }

  private void executarAcao(Treinador atual, Treinador adversario, Acao acao) {
    if (acao.ehAtaque()) {
      executarAtaque(acao.ataque(), atual.getPokesal(), adversario.getPokesal());
      return;
    }
    if (!atual.usarItem(acao.item())) {
      throw new IllegalArgumentException("O item não pode ser usado.");
    }
    registro.registrarUsoItem(atual, acao.item());
  }

  private void finalizar(Treinador vencedor) {
    Treinador perdedor = vencedor == treinador1 ? treinador2 : treinador1;
    resultado =
        new ResultadoBatalha(
            vencedor.getNome(),
            perdedor.getNome(),
            turnos,
            vencedor.getPokesal().getNome(),
            perdedor.getPokesal().getNome());
    try {
      repositorio.salvar(resultado);
    } catch (RuntimeException e) {
      LOGGER.log(Level.SEVERE, "Falha ao salvar o resultado da batalha.", e);
    }
  }
}
