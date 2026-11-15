package br.com.pokesal.infra;

import br.com.pokesal.batalha.GeradorAleatorio;
import java.util.Random;

/** Implementação real do gerador, baseada em {@link Random}, usada só na execução do jogo. */
public class GeradorAleatorioPadrao implements GeradorAleatorio {
  private final Random random = new Random();

  @Override
  public int proximoInteiro(int limite) {
    return random.nextInt(limite);
  }
}
