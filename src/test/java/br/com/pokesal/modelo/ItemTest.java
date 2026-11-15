package br.com.pokesal.modelo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.TipoElemental;
import br.com.pokesal.enums.TipoItem;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ItemTest {
  private Pokesal alvo;

  @BeforeEach
  void setUp() {
    alvo = new Pokesal("Alvo", TipoElemental.AGUA, 100, 50, 30, 50, List.of());
    alvo.receberDano(70);
  }

  @Test
  @DisplayName("Poção recupera 20 HP")
  void testPocaoRecupera20() {
    new Item(TipoItem.POCAO).aplicar(alvo);

    assertEquals(50, alvo.getHpAtual());
  }

  @Test
  @DisplayName("Super Poção recupera 40 HP")
  void testSuperPocaoRecupera40() {
    new Item(TipoItem.SUPER_POCAO).aplicar(alvo);

    assertEquals(70, alvo.getHpAtual());
  }

  @Test
  @DisplayName("A cura nunca ultrapassa o HP máximo")
  void testCuraRespeitaHpMaximo() {
    alvo.recuperarVida(45);

    new Item(TipoItem.SUPER_POCAO).aplicar(alvo);

    assertEquals(100, alvo.getHpAtual());
  }

  @Test
  @DisplayName("Antídoto remove o envenenamento")
  void testAntidotoRemoveVeneno() {
    alvo.aplicarStatus(EfeitoStatus.ENVENENADO);

    new Item(TipoItem.ANTIDOTO).aplicar(alvo);

    assertEquals(EfeitoStatus.NENHUM, alvo.getEfeitoStatus());
  }

  @Test
  @DisplayName("Antídoto não remove outros efeitos de status")
  void testAntidotoNaoRemoveQueimadura() {
    alvo.aplicarStatus(EfeitoStatus.QUEIMADO);

    new Item(TipoItem.ANTIDOTO).aplicar(alvo);

    assertEquals(EfeitoStatus.QUEIMADO, alvo.getEfeitoStatus());
  }

  @Test
  @DisplayName("O item só pode ser usado uma vez e pode ser reiniciado")
  void testItemDeUsoUnico() {
    Item pocao = new Item(TipoItem.POCAO);
    pocao.aplicar(alvo);

    assertAll(
        () -> assertFalse(pocao.podeSerUsado()),
        () -> assertThrows(IllegalStateException.class, () -> pocao.aplicar(alvo)));

    pocao.reiniciar();

    assertAll(
        () -> assertTrue(pocao.podeSerUsado()),
        () -> assertEquals(TipoItem.POCAO, pocao.getTipo()));
  }
}
