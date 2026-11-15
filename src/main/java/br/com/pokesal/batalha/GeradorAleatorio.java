package br.com.pokesal.batalha;

/** Fonte de aleatoriedade das regras de negócio, substituível por um mock nos testes. */
public interface GeradorAleatorio {

  /**
   * Sorteia um número inteiro.
   *
   * @param limite limite superior exclusivo, maior que zero
   * @return valor de 0 até {@code limite - 1}
   */
  int proximoInteiro(int limite);

  /**
   * Sorteia um número inteiro em um intervalo fechado.
   *
   * @param minimo menor valor possível
   * @param maximo maior valor possível, maior ou igual a {@code minimo}
   * @return valor de {@code minimo} até {@code maximo}, inclusive
   */
  default int sortear(int minimo, int maximo) {
    return minimo + proximoInteiro(maximo - minimo + 1);
  }
}
