package br.com.pokesal.batalha;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
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
import br.com.pokesal.modelo.Item;
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
 * Colaboradores mockados: GeradorAleatorio, ServicoTerreno, RepositorioResultados, RegistroBatalha
 * e ContextoBatalha (sem parada). Pokesal e Treinador são objetos reais (ver documento de testes).
 *
 * <p>Com o golpe padrão (dano base 20), ATK 50 e DEF 30, o dano sem multiplicadores é 40.
 */
@ExtendWith(MockitoExtension.class)
class BatalhaTest {
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
  private Treinador ana;
  private Treinador bruno;

  @BeforeEach
  void setUp() {
    golpe = new Ataque("Golpe", TipoElemental.NORMAL, 20, 100, EfeitoStatus.NENHUM);
    alfa = criarPokesal("Alfa", TipoElemental.NORMAL, 100, 50);
    beta = criarPokesal("Beta", TipoElemental.NORMAL, 100, 50);
    ana = new Treinador("Ana", alfa);
    bruno = new Treinador("Bruno", beta);
  }

  private Pokesal criarPokesal(String nome, TipoElemental tipo, int hp, int velocidade) {
    return new Pokesal(nome, tipo, hp, 50, 30, velocidade, List.of(golpe));
  }

  private Batalha criarBatalha(TipoTerreno terreno) {
    when(servicoTerreno.consultar()).thenReturn(terreno);
    return new Batalha(ana, bruno, servicoTerreno, gerador, repositorio, registro, contexto);
  }

  private void sortearAcertoSemCritico() {
    when(gerador.proximoInteiro(FAIXA_PRECISAO)).thenReturn(0);
    when(gerador.proximoInteiro(FAIXA_CRITICO)).thenReturn(1);
  }

  // ---------- RN01: golpe crítico e precisão ----------

  @Test
  @DisplayName("RN01: crítico multiplica o dano por 1,5 quando o gerador cai na faixa de crítico")
  void testAcertoCriticoComGeradorMockado() {
    Batalha batalha = criarBatalha(TipoTerreno.SEM_EFEITO);
    when(gerador.proximoInteiro(FAIXA_PRECISAO)).thenReturn(0);
    when(gerador.proximoInteiro(FAIXA_CRITICO)).thenReturn(0);

    int dano = batalha.executarAtaque(golpe, alfa, beta);

    assertAll(() -> assertEquals(60, dano), () -> assertEquals(40, beta.getHpAtual()));
    verify(registro).registrarDano(beta, 60, true);
  }

  @Test
  @DisplayName("Sem crítico o dano não recebe o multiplicador 1,5")
  void testAcertoSemCritico() {
    Batalha batalha = criarBatalha(TipoTerreno.SEM_EFEITO);
    sortearAcertoSemCritico();

    int dano = batalha.executarAtaque(golpe, alfa, beta);

    assertEquals(40, dano);
    verify(registro).registrarDano(beta, 40, false);
  }

  @Test
  @DisplayName("Golpe erra quando o sorteio de precisão é maior ou igual à precisão")
  void testGolpeErraQuandoPrecisaoFalha() {
    Ataque impreciso = new Ataque("Impreciso", TipoElemental.NORMAL, 20, 90, EfeitoStatus.NENHUM);
    Batalha batalha = criarBatalha(TipoTerreno.SEM_EFEITO);
    when(gerador.proximoInteiro(FAIXA_PRECISAO)).thenReturn(95);

    int dano = batalha.executarAtaque(impreciso, alfa, beta);

    assertAll(() -> assertEquals(0, dano), () -> assertEquals(100, beta.getHpAtual()));
    verify(registro).registrarDano(beta, 0, false);
    verify(gerador, never()).proximoInteiro(FAIXA_CRITICO);
  }

  @Test
  @DisplayName("Valor-limite de precisão: sorteio 89 acerta e 90 erra para precisão 90")
  void testRetornosEmSequenciaDoGerador() {
    Ataque impreciso = new Ataque("Impreciso", TipoElemental.NORMAL, 20, 90, EfeitoStatus.NENHUM);
    Batalha batalha = criarBatalha(TipoTerreno.SEM_EFEITO);
    when(gerador.proximoInteiro(FAIXA_PRECISAO)).thenReturn(89, 90);
    when(gerador.proximoInteiro(FAIXA_CRITICO)).thenReturn(1);

    int primeiro = batalha.executarAtaque(impreciso, alfa, beta);
    int segundo = batalha.executarAtaque(impreciso, alfa, beta);

    assertAll(() -> assertEquals(40, primeiro), () -> assertEquals(0, segundo));
    verify(gerador, times(2)).proximoInteiro(FAIXA_PRECISAO);
    verify(gerador, times(1)).proximoInteiro(FAIXA_CRITICO);
  }

  @Test
  @DisplayName("Golpe com efeito aplica o status ao acertar")
  void testGolpeComEfeitoAplicaStatus() {
    Ataque veneno = new Ataque("Veneno", TipoElemental.NORMAL, 10, 100, EfeitoStatus.ENVENENADO);
    Batalha batalha = criarBatalha(TipoTerreno.SEM_EFEITO);
    sortearAcertoSemCritico();

    batalha.executarAtaque(veneno, alfa, beta);

    assertEquals(EfeitoStatus.ENVENENADO, beta.getEfeitoStatus());
  }

  @Test
  @DisplayName("Golpe com efeito não aplica o status quando erra")
  void testGolpeComEfeitoNaoAplicaStatusAoErrar() {
    Ataque veneno = new Ataque("Veneno", TipoElemental.NORMAL, 10, 50, EfeitoStatus.ENVENENADO);
    Batalha batalha = criarBatalha(TipoTerreno.SEM_EFEITO);
    when(gerador.proximoInteiro(FAIXA_PRECISAO)).thenReturn(60);

    batalha.executarAtaque(veneno, alfa, beta);

    assertEquals(EfeitoStatus.NENHUM, beta.getEfeitoStatus());
  }

  // ---------- Dano: tipos e terrenos ----------

  @Test
  @DisplayName("Vantagem elemental dobra o dano e desvantagem o reduz à metade")
  void testVantagemEDesvantagemNoDano() {
    Ataque fogo = new Ataque("Brasas", TipoElemental.FOGO, 20, 100, EfeitoStatus.NENHUM);
    Pokesal planta = criarPokesal("Planta", TipoElemental.PLANTA, 100, 50);
    Pokesal agua = criarPokesal("Agua", TipoElemental.AGUA, 100, 50);
    Batalha batalha = criarBatalha(TipoTerreno.SEM_EFEITO);

    assertAll(
        () -> assertEquals(80, batalha.calcularDano(fogo, alfa, planta, false)),
        () -> assertEquals(20, batalha.calcularDano(fogo, alfa, agua, false)));
  }

  @Test
  @DisplayName("Asfalto Quente aumenta em 15% o dano de Fogo")
  void testAsfaltoQuenteAumentaDanoDeFogo() {
    Ataque fogo = new Ataque("Brasas", TipoElemental.FOGO, 20, 100, EfeitoStatus.NENHUM);
    Pokesal alvoFogo = criarPokesal("AlvoFogo", TipoElemental.FOGO, 100, 50);

    Batalha asfalto = criarBatalha(TipoTerreno.ASFALTO_QUENTE);
    Batalha neutra = criarBatalha(TipoTerreno.SEM_EFEITO);

    int comTerreno = asfalto.calcularDano(fogo, alfa, alvoFogo, false);
    int semTerreno = neutra.calcularDano(fogo, alfa, alvoFogo, false);

    assertAll(() -> assertEquals(46, comTerreno), () -> assertEquals(40, semTerreno));
  }

  @Test
  @DisplayName("Poça d'água aumenta em 10% o dano de Água e não altera o de Fogo")
  void testPocaDAguaAumentaDanoDeAgua() {
    Ataque agua = new Ataque("Jato", TipoElemental.AGUA, 20, 100, EfeitoStatus.NENHUM);
    Ataque fogo = new Ataque("Brasas", TipoElemental.FOGO, 20, 100, EfeitoStatus.NENHUM);
    Pokesal alvoAgua = criarPokesal("AlvoAgua", TipoElemental.AGUA, 100, 50);
    Batalha batalha = criarBatalha(TipoTerreno.POCA_DAGUA);

    assertAll(
        () -> assertEquals(44, batalha.calcularDano(agua, alfa, alvoAgua, false)),
        () -> assertEquals(20, batalha.calcularDano(fogo, alfa, alvoAgua, false)));
  }

  @Test
  @DisplayName("O dano mínimo é 1, mesmo com defesa muito alta")
  void testDanoMinimoEhUm() {
    Pokesal blindado = new Pokesal("Blindado", TipoElemental.NORMAL, 100, 50, 500, 50, List.of());
    Batalha batalha = criarBatalha(TipoTerreno.SEM_EFEITO);

    assertEquals(1, batalha.calcularDano(golpe, alfa, blindado, false));
  }

  @Test
  @DisplayName("Queimadura reduz o ataque e, por consequência, o dano")
  void testAtaqueQueimadoCausaMenosDano() {
    Batalha batalha = criarBatalha(TipoTerreno.SEM_EFEITO);
    alfa.aplicarStatus(EfeitoStatus.QUEIMADO);

    assertEquals(35, batalha.calcularDano(golpe, alfa, beta, false));
  }

  // ---------- Ordem de ataque ----------

  @Test
  @DisplayName("Quem tem maior velocidade ataca primeiro")
  void testOrdemDeAtaquePorVelocidade() {
    Pokesal rapido = criarPokesal("Rapido", TipoElemental.NORMAL, 100, 60);
    Pokesal lento = criarPokesal("Lento", TipoElemental.NORMAL, 100, 40);
    when(servicoTerreno.consultar()).thenReturn(TipoTerreno.SEM_EFEITO);

    Batalha rapidoPrimeiro =
        new Batalha(
            new Treinador("A", rapido),
            new Treinador("B", lento),
            servicoTerreno,
            gerador,
            repositorio,
            registro,
            contexto);
    Batalha lentoPrimeiro =
        new Batalha(
            new Treinador("A", lento),
            new Treinador("B", rapido),
            servicoTerreno,
            gerador,
            repositorio,
            registro,
            contexto);

    assertAll(
        () -> assertTrue(rapidoPrimeiro.jogador1AtacaPrimeiro()),
        () -> assertFalse(lentoPrimeiro.jogador1AtacaPrimeiro()));
  }

  @Test
  @DisplayName("Com a mesma velocidade, o primeiro jogador ataca primeiro")
  void testEmpateDeVelocidadeFavoreceJogador1() {
    Batalha batalha = criarBatalha(TipoTerreno.SEM_EFEITO);

    assertTrue(batalha.jogador1AtacaPrimeiro());
  }

  @Test
  @DisplayName("A paralisia reduz a velocidade e pode inverter a ordem de ataque")
  void testParalisiaInverteOrdem() {
    Pokesal quaseRapido = criarPokesal("Quase", TipoElemental.NORMAL, 100, 55);
    Treinador paralisado = new Treinador("A", quaseRapido);
    when(servicoTerreno.consultar()).thenReturn(TipoTerreno.SEM_EFEITO);
    Batalha batalha =
        new Batalha(paralisado, bruno, servicoTerreno, gerador, repositorio, registro, contexto);
    quaseRapido.aplicarStatus(EfeitoStatus.PARALISADO);

    assertFalse(batalha.jogador1AtacaPrimeiro());
  }

  // ---------- Turno, registro e itens ----------

  @Test
  @DisplayName("Os eventos do turno chegam ao registro na ordem correta")
  void testSequenciaDeEventosNoTurno() {
    Pokesal planta = criarPokesal("Planta", TipoElemental.PLANTA, 100, 60);
    Treinador donoDaPlanta = new Treinador("Ana", planta);
    when(servicoTerreno.consultar()).thenReturn(TipoTerreno.CANTEIRO_CENTRAL);
    Batalha batalha =
        new Batalha(donoDaPlanta, bruno, servicoTerreno, gerador, repositorio, registro, contexto);
    sortearAcertoSemCritico();
    planta.aplicarStatus(EfeitoStatus.ENVENENADO);

    batalha.executarTurno(Acao.atacar(golpe), Acao.atacar(golpe));

    InOrder ordem = inOrder(registro);
    ordem.verify(registro).registrarAtaque(planta, golpe, beta);
    ordem.verify(registro).registrarDano(beta, 40, false);
    ordem.verify(registro).registrarAtaque(beta, golpe, planta);
    ordem.verify(registro).registrarDano(planta, 40, false);
    ordem.verify(registro).registrarEfeitoStatus(planta, EfeitoStatus.ENVENENADO, 5);
    ordem.verify(registro).registrarEfeitoTerreno(planta, TipoTerreno.CANTEIRO_CENTRAL, 5);
    verify(registro, atLeastOnce()).registrarDano(any(Pokesal.class), anyInt(), anyBoolean());
    assertEquals(60, planta.getHpAtual());
  }

  @Test
  @DisplayName("Usar item consome o turno do treinador e é enviado ao registro")
  void testUsoDeItemConsomeOTurno() {
    Batalha batalha = criarBatalha(TipoTerreno.SEM_EFEITO);
    sortearAcertoSemCritico();
    alfa.receberDano(50);
    Item pocao = ana.getInventario().get(0);

    batalha.executarTurno(Acao.usarItem(pocao), Acao.atacar(golpe));

    verify(registro).registrarUsoItem(ana, pocao);
    verify(registro, never()).registrarAtaque(eq(alfa), any(Ataque.class), any(Pokesal.class));
    verify(registro, times(1)).registrarAtaque(beta, golpe, alfa);
    assertEquals(30, alfa.getHpAtual());
  }

  @Test
  @DisplayName("Item que não pode ser usado interrompe o turno")
  void testItemInvalidoLancaExcecao() {
    Batalha batalha = criarBatalha(TipoTerreno.SEM_EFEITO);
    Item estranho = new Item(br.com.pokesal.enums.TipoItem.POCAO);

    assertThrows(
        IllegalArgumentException.class,
        () -> batalha.executarTurno(Acao.usarItem(estranho), Acao.atacar(golpe)));
    verify(registro, never()).registrarUsoItem(any(Treinador.class), any(Item.class));
  }

  @Test
  @DisplayName("Fim de turno: o status causa dano e termina após a duração")
  void testStatusNoFimDoTurno() {
    Batalha batalha = criarBatalha(TipoTerreno.SEM_EFEITO);
    beta.aplicarStatus(EfeitoStatus.QUEIMADO);

    batalha.processarFimDoTurno(beta);
    int hpAposPrimeiro = beta.getHpAtual();
    batalha.processarFimDoTurno(beta);

    assertAll(
        () -> assertEquals(95, hpAposPrimeiro),
        () -> assertEquals(90, beta.getHpAtual()),
        () -> assertEquals(EfeitoStatus.NENHUM, beta.getEfeitoStatus()));
    verify(registro, times(2)).registrarEfeitoStatus(beta, EfeitoStatus.QUEIMADO, 5);
  }

  @Test
  @DisplayName("Canteiro Central cura 5% do HP máximo apenas de Pokésals de Planta")
  void testCanteiroCentralCuraApenasPlanta() {
    Pokesal planta = criarPokesal("Planta", TipoElemental.PLANTA, 100, 50);
    Batalha batalha = criarBatalha(TipoTerreno.CANTEIRO_CENTRAL);
    planta.receberDano(50);
    beta.receberDano(50);

    batalha.processarFimDoTurno(planta);
    batalha.processarFimDoTurno(beta);

    assertAll(
        () -> assertEquals(55, planta.getHpAtual()), () -> assertEquals(50, beta.getHpAtual()));
    verify(registro).registrarEfeitoTerreno(planta, TipoTerreno.CANTEIRO_CENTRAL, 5);
    verify(registro, never()).registrarEfeitoTerreno(eq(beta), any(TipoTerreno.class), anyInt());
  }

  @Test
  @DisplayName("RN02: a cura do terreno não ultrapassa o HP máximo")
  void testCuraDoTerrenoRespeitaHpMaximo() {
    Pokesal planta = criarPokesal("Planta", TipoElemental.PLANTA, 100, 50);
    Batalha batalha = criarBatalha(TipoTerreno.CANTEIRO_CENTRAL);
    planta.receberDano(2);

    batalha.processarFimDoTurno(planta);

    assertEquals(100, planta.getHpAtual());
    verify(registro).registrarEfeitoTerreno(planta, TipoTerreno.CANTEIRO_CENTRAL, 2);
  }

  // ---------- RN03: fim da batalha e repositório ----------

  @Test
  @DisplayName("RN03: a batalha só tem vencedor quando um Pokésal chega a zero de HP")
  void testFimDaBatalhaQuandoPokesalNaoPodeLutar() {
    Batalha batalha = criarBatalha(TipoTerreno.SEM_EFEITO);
    assertTrue(batalha.verificarVencedor().isEmpty());

    beta.receberDano(100);
    assertSame(ana, batalha.verificarVencedor().orElseThrow());

    Batalha outra =
        new Batalha(ana, bruno, servicoTerreno, gerador, repositorio, registro, contexto);
    alfa.receberDano(100);
    beta.restaurar();
    assertSame(bruno, outra.verificarVencedor().orElseThrow());
  }

  @Test
  @DisplayName("O resultado é salvo ao fim da batalha com vencedor, perdedor e turnos")
  void testResultadoSalvoAoFimDaBatalha() {
    Pokesal rapido = criarPokesal("Rapido", TipoElemental.NORMAL, 100, 60);
    Pokesal fraco = criarPokesal("Fraco", TipoElemental.NORMAL, 30, 40);
    when(servicoTerreno.consultar()).thenReturn(TipoTerreno.SEM_EFEITO);
    Batalha batalha =
        new Batalha(
            new Treinador("Ana", rapido),
            new Treinador("Bruno", fraco),
            servicoTerreno,
            gerador,
            repositorio,
            registro,
            contexto);
    sortearAcertoSemCritico();
    ArgumentCaptor<ResultadoBatalha> captor = ArgumentCaptor.forClass(ResultadoBatalha.class);

    batalha.executarTurno(Acao.atacar(golpe), Acao.atacar(golpe));

    verify(repositorio, times(1)).salvar(captor.capture());
    ResultadoBatalha salvo = captor.getValue();
    assertAll(
        () -> assertEquals("Ana", salvo.vencedor()),
        () -> assertEquals("Bruno", salvo.perdedor()),
        () -> assertEquals(1, salvo.turnos()),
        () -> assertEquals("Rapido", salvo.pokesalVencedor()),
        () -> assertEquals("Fraco", salvo.pokesalPerdedor()),
        () -> assertTrue(batalha.estaTerminada()),
        () -> assertEquals(salvo, batalha.getResultado().orElseThrow()));
    verify(registro, times(1))
        .registrarAtaque(any(Pokesal.class), any(Ataque.class), any(Pokesal.class));
  }

  @Test
  @DisplayName("Falha no repositório não invalida a batalha")
  void testFalhaNoRepositorioNaoInvalidaBatalha() {
    Pokesal fraco = criarPokesal("Fraco", TipoElemental.NORMAL, 30, 40);
    when(servicoTerreno.consultar()).thenReturn(TipoTerreno.SEM_EFEITO);
    Batalha batalha =
        new Batalha(
            ana,
            new Treinador("Bruno", fraco),
            servicoTerreno,
            gerador,
            repositorio,
            registro,
            contexto);
    sortearAcertoSemCritico();
    doThrow(new IllegalStateException("banco indisponível"))
        .when(repositorio)
        .salvar(any(ResultadoBatalha.class));

    assertDoesNotThrow(() -> batalha.executarTurno(Acao.atacar(golpe), Acao.atacar(golpe)));

    assertAll(
        () -> assertTrue(batalha.estaTerminada()),
        () -> assertEquals("Ana", batalha.verificarVencedor().orElseThrow().getNome()),
        () -> assertEquals("Ana", batalha.getResultado().orElseThrow().vencedor()));
    verify(repositorio, times(1)).salvar(any(ResultadoBatalha.class));
  }

  @Test
  @DisplayName("O repositório não é chamado enquanto a batalha não terminou")
  void testRepositorioNaoChamadoSeBatalhaNaoTerminou() {
    Batalha batalha = criarBatalha(TipoTerreno.SEM_EFEITO);
    sortearAcertoSemCritico();

    batalha.executarTurno(Acao.atacar(golpe), Acao.atacar(golpe));

    assertAll(
        () -> assertFalse(batalha.estaTerminada()),
        () -> assertTrue(batalha.getResultado().isEmpty()),
        () -> assertEquals(1, batalha.getTurnos()));
    verify(repositorio, never()).salvar(any(ResultadoBatalha.class));
  }

  @Test
  @DisplayName("Depois do fim da batalha não é possível executar outro turno")
  void testTurnoAposOFimLancaExcecao() {
    Pokesal fraco = criarPokesal("Fraco", TipoElemental.NORMAL, 30, 40);
    when(servicoTerreno.consultar()).thenReturn(TipoTerreno.SEM_EFEITO);
    Batalha batalha =
        new Batalha(
            ana,
            new Treinador("Bruno", fraco),
            servicoTerreno,
            gerador,
            repositorio,
            registro,
            contexto);
    sortearAcertoSemCritico();
    batalha.executarTurno(Acao.atacar(golpe), Acao.atacar(golpe));

    assertThrows(
        IllegalStateException.class,
        () -> batalha.executarTurno(Acao.atacar(golpe), Acao.atacar(golpe)));
    verify(repositorio, times(1)).salvar(any(ResultadoBatalha.class));
  }

  @Test
  @DisplayName("Um efeito de status ao fim do turno pode derrotar o Pokésal e encerrar a batalha")
  void testStatusPodeDerrotarPokesalNoFimDoTurno() {
    Pokesal rapido = criarPokesal("Rapido", TipoElemental.NORMAL, 100, 60);
    Pokesal envenenado = criarPokesal("Envenenado", TipoElemental.NORMAL, 100, 40);
    when(servicoTerreno.consultar()).thenReturn(TipoTerreno.SEM_EFEITO);
    Batalha batalha =
        new Batalha(
            new Treinador("Ana", rapido),
            new Treinador("Bruno", envenenado),
            servicoTerreno,
            gerador,
            repositorio,
            registro,
            contexto);
    sortearAcertoSemCritico();
    rapido.receberDano(55);
    rapido.aplicarStatus(EfeitoStatus.ENVENENADO);

    batalha.executarTurno(Acao.atacar(golpe), Acao.atacar(golpe));

    assertAll(
        () -> assertEquals(0, rapido.getHpAtual()),
        () -> assertEquals("Bruno", batalha.getResultado().orElseThrow().vencedor()));
    verify(repositorio, times(1)).salvar(any(ResultadoBatalha.class));
  }

  @Test
  @DisplayName("O terreno é consultado uma única vez, na criação da batalha")
  void testTerrenoConsultadoUmaVez() {
    Batalha batalha = criarBatalha(TipoTerreno.POCA_DAGUA);

    assertEquals(TipoTerreno.POCA_DAGUA, batalha.getTerreno());
    verify(servicoTerreno, times(1)).consultar();
  }
}
