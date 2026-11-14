package br.com.pokesal.modelo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.pokesal.enums.TipoElemental;
import br.com.pokesal.enums.TipoItem;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TreinadorTest {
  private Pokesal pokesal;
  private Treinador treinador;
  private Item pocao;
  private Item superPocao;
  private Item antidoto;

  @BeforeEach
  void setUp() {
    pokesal = new Pokesal("Alfa", TipoElemental.FOGO, 100, 50, 30, 50, List.of());
    treinador = new Treinador("Ana", pokesal);
    pocao = treinador.getInventario().get(0);
    superPocao = treinador.getInventario().get(1);
    antidoto = treinador.getInventario().get(2);
  }

  @Test
  @DisplayName("O treinador começa com um item de cada tipo")
  void testInventarioInicial() {
    assertAll(
            () -> assertEquals(3, treinador.getInventario().size()),
            () -> assertEquals(TipoItem.POCAO, pocao.getTipo()),
            () -> assertEquals(TipoItem.SUPER_POCAO, superPocao.getTipo()),
            () -> assertEquals(TipoItem.ANTIDOTO, antidoto.getTipo()));
  }

  @Test
  @DisplayName("Limite de itens: o terceiro uso é recusado, mesmo sendo de outro tipo")
  void testLimiteDeUtilizacaoDeItens() {
    boolean primeiro = treinador.usarItem(pocao);
    boolean segundo = treinador.usarItem(superPocao);
    boolean terceiro = treinador.usarItem(antidoto);

    assertAll(
            () -> assertTrue(primeiro),
            () -> assertTrue(segundo),
            () -> assertFalse(terceiro),
            () -> assertEquals(2, treinador.getItensUsados()),
            () -> assertFalse(treinador.podeUsarItem()),
            () -> assertTrue(antidoto.podeSerUsado()));
  }

  @Test
  @DisplayName("O treinador pode terminar a batalha sem usar itens")
  void testPodeNaoUsarItens() {
    assertAll(
            () -> assertTrue(treinador.podeUsarItem()),
            () -> assertEquals(0, treinador.getItensUsados()));
  }

  @Test
  @DisplayName("Usar o mesmo item duas vezes é recusado e não consome o limite")
  void testMesmoItemNaoPodeSerReutilizado() {
    treinador.usarItem(pocao);

    boolean repetido = treinador.usarItem(pocao);

    assertAll(() -> assertFalse(repetido), () -> assertEquals(1, treinador.getItensUsados()));
  }

  @Test
  @DisplayName("Item que não pertence ao inventário é recusado")
  void testItemDeForaDoInventario() {
    boolean usou = treinador.usarItem(new Item(TipoItem.POCAO));

    assertAll(() -> assertFalse(usou), () -> assertEquals(0, treinador.getItensUsados()));
  }

  @Test
  @DisplayName("Reiniciar restaura o Pokésal, os itens e o contador")
  void testReiniciar() {
    pokesal.receberDano(60);
    treinador.usarItem(pocao);

    treinador.reiniciar();

    assertAll(
            () -> assertEquals(100, pokesal.getHpAtual()),
            () -> assertEquals(0, treinador.getItensUsados()),
            () -> assertTrue(pocao.podeSerUsado()));
  }

  @Test
  @DisplayName("O inventário exposto não pode ser modificado")
  void testInventarioImutavel() {
    List<Item> inventario = treinador.getInventario();

    assertThrows(UnsupportedOperationException.class, () -> inventario.add(pocao));
  }

  @Test
  @DisplayName("Os getters devolvem nome e Pokésal")
  void testGetters() {
    assertAll(
            () -> assertEquals("Ana", treinador.getNome()),
            () -> assertEquals(pokesal, treinador.getPokesal()));
  }
}
