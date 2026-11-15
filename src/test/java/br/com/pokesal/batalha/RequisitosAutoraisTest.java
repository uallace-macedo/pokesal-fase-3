package br.com.pokesal.batalha;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Testes dos requisitos autorais da Fase 03.
 *
 * <p>REQ-01: interrupção da batalha pela Equipe Pokemal (colaborador mockado: ContextoBatalha).
 * REQ-02: tropeço do Pokémon (colaboradores mockados: GeradorAleatorio e RegistroBatalha).
 *
 * <p>Com o golpe padrão (dano base 20), ATK 50 e DEF 30, o dano sem multiplicadores é 40.
 */
@ExtendWith(MockitoExtension.class)
class RequisitosAutoraisTest {
  private static final String MENSAGEM_TROPECO = "Seu pokemon tropeçou e errou o ataque";
  private static final int FAIXA_PRECISAO = 100;
  private static final int FAIXA_CRITICO = 16;

  @Mock private GeradorAleatorio gerador;
  @Mock private ServicoTerreno servicoTerreno;
  @Mock private RepositorioResultados repositorio;
  @Mock private RegistroBatalha registro;
  @Mock private ContextoBatalha contexto;

  private Ataque golpe;
  private Pokesal alfa;
  private Pokesal beta;
  private Batalha batalha;

  @BeforeEach
  void setUp() {
    golpe = new Ataque("Golpe", TipoElemental.NORMAL, 20, 100, EfeitoStatus.NENHUM);
    alfa = new Pokesal("Alfa", TipoElemental.NORMAL, 100, 50, 30, 60, List.of(golpe));
    beta = new Pokesal("Beta", TipoElemental.NORMAL, 100, 50, 30, 50, List.of(golpe));
    when(servicoTerreno.consultar()).thenReturn(TipoTerreno.SEM_EFEITO);
    batalha =
        new Batalha(
            new Treinador("Ana", alfa),
            new Treinador("Bruno", beta),
            servicoTerreno,
            gerador,
            repositorio,
            registro,
            contexto);
  }

  private void sortearAcertoSemCritico() {
    when(gerador.proximoInteiro(FAIXA_PRECISAO)).thenReturn(0);
    when(gerador.proximoInteiro(FAIXA_CRITICO)).thenReturn(1);
  }

  // ---------- REQ-01: interrupção da batalha pela Equipe Pokemal ----------

  @Test
  @DisplayName("CT-01.1: com parada ativa, o turno lança InterrompeBatalhaException")
  void testCt011InterrupcaoLancaExcecao() {
    when(contexto.isParada()).thenReturn(true);

    assertThrows(
        InterrompeBatalhaException.class,
        () -> batalha.executarTurno(Acao.atacar(golpe), Acao.atacar(golpe)));

    assertAll(
        () -> assertEquals(0, batalha.getTurnos()),
        () -> assertFalse(batalha.estaTerminada()),
        () -> assertEquals(100, beta.getHpAtual()));
    verify(registro, never())
        .registrarAtaque(any(Pokesal.class), any(Ataque.class), any(Pokesal.class));
    verify(repositorio, never()).salvar(any(ResultadoBatalha.class));
  }

  @Test
  @DisplayName("CT-01.2: a mensagem da exceção é exatamente a definida no requisito")
  void testCt012MensagemDaExcecao() {
    when(contexto.isParada()).thenReturn(true);

    InterrompeBatalhaException excecao =
        assertThrows(
            InterrompeBatalhaException.class,
            () -> batalha.executarTurno(Acao.atacar(golpe), Acao.atacar(golpe)));

    assertEquals("Equipe Pokemal interrompeu a batalha", excecao.getMessage());
  }

  @Test
  @DisplayName("CT-01.3: com parada desativada, nenhuma exceção é lançada e a batalha continua")
  void testCt013SemParadaABatalhaContinua() {
    when(contexto.isParada()).thenReturn(false);
    sortearAcertoSemCritico();

    assertDoesNotThrow(() -> batalha.executarTurno(Acao.atacar(golpe), Acao.atacar(golpe)));

    assertAll(
        () -> assertEquals(1, batalha.getTurnos()),
        () -> assertEquals(60, beta.getHpAtual()),
        () -> assertEquals(60, alfa.getHpAtual()));
    verify(registro, times(2))
        .registrarAtaque(any(Pokesal.class), any(Ataque.class), any(Pokesal.class));
  }

  @Test
  @DisplayName("A parada pedida no meio do turno interrompe antes da segunda ação")
  void testInterrupcaoNoMeioDoTurno() {
    when(contexto.isParada()).thenReturn(false, true);
    sortearAcertoSemCritico();

    assertThrows(
        InterrompeBatalhaException.class,
        () -> batalha.executarTurno(Acao.atacar(golpe), Acao.atacar(golpe)));

    verify(registro, times(1))
        .registrarAtaque(any(Pokesal.class), any(Ataque.class), any(Pokesal.class));
    verify(registro, never()).registrarAtaque(eq(beta), any(Ataque.class), any(Pokesal.class));
    verify(repositorio, never()).salvar(any(ResultadoBatalha.class));
  }

  // ---------- REQ-02: falha crítica por tropeço ----------

  @Test
  @DisplayName("CT-02.1: sorteios 48, 49 e 50 disparam o tropeço, mesmo com precisão 100")
  void testCt021GatilhoDoTropeco() {
    when(gerador.sortear(1, 50)).thenReturn(48, 49, 50);

    batalha.executarAtaque(golpe, alfa, beta);
    batalha.executarAtaque(golpe, alfa, beta);
    batalha.executarAtaque(golpe, alfa, beta);

    verify(registro, times(3)).registrarTropeco(alfa, MENSAGEM_TROPECO);
    verify(gerador, never()).proximoInteiro(FAIXA_PRECISAO);
  }

  @Test
  @DisplayName("CT-02.2: sorteios 1, 25 e 47 não disparam o tropeço e o ataque segue normal")
  void testCt022SemTropeco() {
    when(gerador.sortear(1, 50)).thenReturn(1, 25, 47);
    sortearAcertoSemCritico();
    Pokesal resistente = new Pokesal("Tanque", TipoElemental.NORMAL, 500, 50, 30, 50, List.of());

    int primeiro = batalha.executarAtaque(golpe, alfa, resistente);
    int segundo = batalha.executarAtaque(golpe, alfa, resistente);
    int terceiro = batalha.executarAtaque(golpe, alfa, resistente);

    assertAll(
        () -> assertEquals(40, primeiro),
        () -> assertEquals(40, segundo),
        () -> assertEquals(40, terceiro),
        () -> assertEquals(380, resistente.getHpAtual()));
    verify(registro, never()).registrarTropeco(any(Pokesal.class), anyString());
  }

  @Test
  @DisplayName("Valor-limite: 47 não tropeça e 48 tropeça")
  void testLimiteDoTropeco() {
    when(gerador.sortear(1, 50)).thenReturn(47, 48);
    sortearAcertoSemCritico();

    int sem = batalha.executarAtaque(golpe, alfa, beta);
    int com = batalha.executarAtaque(golpe, alfa, beta);

    assertAll(() -> assertEquals(40, sem), () -> assertEquals(0, com));
    verify(registro, times(1)).registrarTropeco(alfa, MENSAGEM_TROPECO);
  }

  @Test
  @DisplayName("CT-02.3: no tropeço o dano é 0 e o HP do adversário não muda")
  void testCt023TropecoNaoCausaDano() {
    when(gerador.sortear(1, 50)).thenReturn(50);

    int dano = batalha.executarAtaque(golpe, alfa, beta);

    assertAll(
        () -> assertEquals(0, dano),
        () -> assertEquals(100, beta.getHpAtual()),
        () -> assertEquals(EfeitoStatus.NENHUM, beta.getEfeitoStatus()));
    verify(registro).registrarDano(beta, 0, false);
  }

  @Test
  @DisplayName("No tropeço o golpe com efeito de status não aplica o status")
  void testTropecoNaoAplicaStatus() {
    Ataque veneno = new Ataque("Veneno", TipoElemental.NORMAL, 10, 100, EfeitoStatus.ENVENENADO);
    when(gerador.sortear(1, 50)).thenReturn(49);

    batalha.executarAtaque(veneno, alfa, beta);

    assertEquals(EfeitoStatus.NENHUM, beta.getEfeitoStatus());
  }

  @Test
  @DisplayName("CT-02.4: a mensagem do tropeço é exatamente a definida no requisito")
  void testCt024MensagemDoTropeco() {
    when(gerador.sortear(1, 50)).thenReturn(48);
    ArgumentCaptor<String> mensagem = ArgumentCaptor.forClass(String.class);

    batalha.executarAtaque(golpe, alfa, beta);

    verify(registro).registrarTropeco(eq(alfa), mensagem.capture());
    assertEquals("Seu pokemon tropeçou e errou o ataque", mensagem.getValue());
  }

  @Test
  @DisplayName("O tropeço chega ao registro entre o ataque e o dano")
  void testOrdemDosEventosNoTropeco() {
    when(gerador.sortear(1, 50)).thenReturn(48);

    batalha.executarAtaque(golpe, alfa, beta);

    InOrder ordem = inOrder(registro);
    ordem.verify(registro).registrarAtaque(alfa, golpe, beta);
    ordem.verify(registro).registrarTropeco(alfa, MENSAGEM_TROPECO);
    ordem.verify(registro).registrarDano(beta, 0, false);
  }
}
