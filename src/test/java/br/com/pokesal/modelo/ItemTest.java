package br.com.pokesal.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.TipoElemental;
import br.com.pokesal.enums.TipoItem;
import java.util.List;
import org.junit.jupiter.api.Test;

class ItemTest {

  private Pokesal criarPokesal() {
    List<Ataque> ataques =
        List.of(
            new Ataque("Investida", TipoElemental.FOGO, 15, EfeitoStatus.NENHUM, false),
            new Ataque("Brasas", TipoElemental.FOGO, 25, EfeitoStatus.NENHUM),
            new Ataque("Chama", TipoElemental.FOGO, 15, EfeitoStatus.QUEIMADO));

    return new Pokesal("CharSal", TipoElemental.FOGO, 100, 55, 35, 55, ataques);
  }

  @Test
  void pocaoDeveRecuperarVinteHp() {
    Pokesal pokesal = criarPokesal();
    Item item = new Item("Poção", TipoItem.POCAO, 1);

    pokesal.receberDano(30);
    item.aplicar(pokesal);

    assertEquals(90, pokesal.getHpAtual());
    assertEquals(0, item.getQuantidade());
  }

  @Test
  void superPocaoDeveRecuperarQuarentaHp() {
    Pokesal pokesal = criarPokesal();
    Item item = new Item("Super Poção", TipoItem.SUPER_POCAO, 1);

    pokesal.receberDano(50);
    item.aplicar(pokesal);

    assertEquals(90, pokesal.getHpAtual());
    assertEquals(0, item.getQuantidade());
  }

  @Test
  void antidotoDeveRemoverEnvenenamento() {
    Pokesal pokesal = criarPokesal();
    Item item = new Item("Antídoto", TipoItem.ANTIDOTO, 1);

    pokesal.aplicarStatus(EfeitoStatus.ENVENENADO, 3);
    item.aplicar(pokesal);

    assertEquals(EfeitoStatus.NENHUM, pokesal.getEfeitoStatus());
    assertEquals(0, pokesal.getTurnosStatus());
    assertEquals(0, item.getQuantidade());
  }

  @Test
  void antidotoNaoDeveSerUsavelSemEnvenenamento() {
    Pokesal pokesal = criarPokesal();
    Item item = new Item("Antídoto", TipoItem.ANTIDOTO, 1);

    assertFalse(item.podeSerUsado(pokesal));
  }

  @Test
  void pocaoNaoDeveSerUsavelComHpCheio() {
    Pokesal pokesal = criarPokesal();
    Item item = new Item("Poção", TipoItem.POCAO, 1);

    assertFalse(item.podeSerUsado(pokesal));
  }

  @Test
  void itemSemQuantidadeNaoDeveSerUsavel() {
    Pokesal pokesal = criarPokesal();
    Item item = new Item("Poção", TipoItem.POCAO, 0);

    pokesal.receberDano(20);

    assertFalse(item.podeSerUsado(pokesal));
  }

  @Test
  void itemNaoDeveSerUsavelEmPokesalNulo() {
    Item item = new Item("Poção", TipoItem.POCAO, 1);

    assertFalse(item.podeSerUsado(null));
  }

  @Test
  void deveRejeitarAplicacaoInvalida() {
    Pokesal pokesal = criarPokesal();
    Item item = new Item("Poção", TipoItem.POCAO, 1);

    assertThrows(IllegalStateException.class, () -> item.aplicar(pokesal));
  }

  @Test
  void deveCriarCopiaIndependente() {
    Item original = new Item("Poção", TipoItem.POCAO, 1);
    Item copia = original.copiar();

    Pokesal pokesal = criarPokesal();
    pokesal.receberDano(20);

    copia.aplicar(pokesal);

    assertEquals(1, original.getQuantidade());
    assertEquals(0, copia.getQuantidade());
  }
}
