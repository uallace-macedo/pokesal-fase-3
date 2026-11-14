package br.com.pokesal.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.TipoElemental;
import br.com.pokesal.enums.TipoItem;
import java.util.List;
import org.junit.jupiter.api.Test;

class TreinadorTest {

  private Pokesal criarPokesal() {
    List<Ataque> ataques =
        List.of(
            new Ataque("Investida", TipoElemental.PLANTA, 15, EfeitoStatus.NENHUM, false),
            new Ataque("Folha", TipoElemental.PLANTA, 25, EfeitoStatus.NENHUM),
            new Ataque("Veneno", TipoElemental.PLANTA, 10, EfeitoStatus.ENVENENADO));

    return new Pokesal("BulbaSal", TipoElemental.PLANTA, 110, 45, 50, 40, ataques);
  }

  @Test
  void deveIniciarComTresItens() {
    Treinador treinador = new Treinador("Ash", criarPokesal());

    assertEquals(3, treinador.getInventario().size());
    assertEquals(0, treinador.getItensUsados());
    assertTrue(treinador.podeUsarItem());
  }

  @Test
  void deveContabilizarUsoDeItem() {
    Treinador treinador = new Treinador("Ash", criarPokesal());

    treinador.getPokesal().receberDano(30);

    assertTrue(treinador.usarItem(TipoItem.POCAO));
    assertEquals(1, treinador.getItensUsados());
    assertEquals(100, treinador.getPokesal().getHpAtual());
  }

  @Test
  void deveCompartilharLimiteEntreTiposDeItens() {
    Treinador treinador = new Treinador("Ash", criarPokesal());

    treinador.getPokesal().receberDano(80);

    assertTrue(treinador.usarItem(TipoItem.POCAO));
    assertTrue(treinador.usarItem(TipoItem.SUPER_POCAO));
    assertFalse(treinador.podeUsarItem());
  }

  @Test
  void deveLancarExcecaoAoExcederLimiteDeItens() {
    Treinador treinador = new Treinador("Ash", criarPokesal());

    treinador.getPokesal().receberDano(80);

    treinador.usarItem(TipoItem.POCAO);
    treinador.usarItem(TipoItem.SUPER_POCAO);

    assertThrows(IllegalStateException.class, () -> treinador.usarItem(TipoItem.ANTIDOTO));
  }

  @Test
  void naoDeveContabilizarItemInutilizavel() {
    Treinador treinador = new Treinador("Ash", criarPokesal());

    assertFalse(treinador.usarItem(TipoItem.POCAO));
    assertEquals(0, treinador.getItensUsados());
  }

  @Test
  void naoDeveConsumirItemSeAntidotoNaoForNecessario() {
    Treinador treinador = new Treinador("Ash", criarPokesal());

    assertFalse(treinador.usarItem(TipoItem.ANTIDOTO));
    assertEquals(1, treinador.buscarItem(TipoItem.ANTIDOTO).getQuantidade());
    assertEquals(0, treinador.getItensUsados());
  }

  @Test
  void inventarioDeveSerUmaCopiaIndependente() {
    Treinador treinador = new Treinador("Ash", criarPokesal());

    List<Item> inventarioExterno = treinador.getInventario();
    Item pocaoExterna = inventarioExterno.get(0);

    treinador.getPokesal().receberDano(30);
    pocaoExterna.aplicar(treinador.getPokesal());

    assertEquals(1, treinador.buscarItem(TipoItem.POCAO).getQuantidade());
    assertEquals(0, treinador.getItensUsados());
  }

  @Test
  void buscaItemDeveRetornarCopiaIndependente() {
    Treinador treinador = new Treinador("Ash", criarPokesal());

    Item pocaoExterna = treinador.buscarItem(TipoItem.POCAO);

    treinador.getPokesal().receberDano(30);
    pocaoExterna.aplicar(treinador.getPokesal());

    assertEquals(1, treinador.buscarItem(TipoItem.POCAO).getQuantidade());
  }
}
