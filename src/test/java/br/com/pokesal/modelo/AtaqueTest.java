package br.com.pokesal.modelo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.TipoElemental;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AtaqueTest {

  @Test
  @DisplayName("Precisão nos limites 0 e 100 é aceita")
  void testPrecisaoNosLimitesEhValida() {
    assertDoesNotThrow(() -> new Ataque("A", TipoElemental.NORMAL, 10, 0, EfeitoStatus.NENHUM));
    assertDoesNotThrow(() -> new Ataque("A", TipoElemental.NORMAL, 10, 100, EfeitoStatus.NENHUM));
  }

  @Test
  @DisplayName("Precisão fora de 0 a 100 é rejeitada")
  void testPrecisaoForaDoIntervaloEhInvalida() {
    assertThrows(
            IllegalArgumentException.class,
            () -> new Ataque("A", TipoElemental.NORMAL, 10, -1, EfeitoStatus.NENHUM));
    assertThrows(
            IllegalArgumentException.class,
            () -> new Ataque("A", TipoElemental.NORMAL, 10, 101, EfeitoStatus.NENHUM));
  }

  @Test
  @DisplayName("Dano base negativo é rejeitado")
  void testDanoBaseNegativoEhInvalido() {
    assertThrows(
            IllegalArgumentException.class,
            () -> new Ataque("A", TipoElemental.NORMAL, -1, 100, EfeitoStatus.NENHUM));
  }
}
