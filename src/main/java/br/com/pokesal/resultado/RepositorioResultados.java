package br.com.pokesal.resultado;

import java.util.List;

/** Armazena os resultados das batalhas do torneio. */
public interface RepositorioResultados {

  /**
   * Salva o resultado de uma batalha.
   *
   * @param resultado resultado a salvar
   */
  void salvar(ResultadoBatalha resultado);

  /**
   * Lista os resultados salvos, na ordem em que foram salvos.
   *
   * @return resultados salvos
   */
  List<ResultadoBatalha> listar();
}
