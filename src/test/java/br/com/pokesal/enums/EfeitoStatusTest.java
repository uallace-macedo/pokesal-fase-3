package br.com.pokesal.enums;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EfeitoStatusTest {

  @Test
  @DisplayName("Queimadura dura 2 turnos, causa 5 de dano e reduz o ataque em 5")
  void testQueimadura() {
    assertAll(
        () -> assertEquals(2, EfeitoStatus.QUEIMADO.getDuracao()),
        () -> assertEquals(5, EfeitoStatus.QUEIMADO.getDanoPorTurno()),
        () -> assertEquals(5, EfeitoStatus.QUEIMADO.getReducaoAtaque()),
        () -> assertEquals(0, EfeitoStatus.QUEIMADO.getReducaoVelocidade()));
  }

  @Test
  @DisplayName("Envenenamento dura 3 turnos e causa 5 de dano")
  void testEnvenenamento() {
    assertAll(
        () -> assertEquals(3, EfeitoStatus.ENVENENADO.getDuracao()),
        () -> assertEquals(5, EfeitoStatus.ENVENENADO.getDanoPorTurno()),
        () -> assertEquals(0, EfeitoStatus.ENVENENADO.getReducaoAtaque()));
  }

  @Test
  @DisplayName("Paralisia dura 2 turnos e reduz a velocidade em 10")
  void testParalisia() {
    assertAll(
        () -> assertEquals(2, EfeitoStatus.PARALISADO.getDuracao()),
        () -> assertEquals(10, EfeitoStatus.PARALISADO.getReducaoVelocidade()),
        () -> assertEquals(0, EfeitoStatus.PARALISADO.getDanoPorTurno()));
  }
}
