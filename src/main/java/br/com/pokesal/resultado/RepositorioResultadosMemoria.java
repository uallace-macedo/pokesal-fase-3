package br.com.pokesal.resultado;

import java.util.ArrayList;
import java.util.List;

/** Repositório que guarda os resultados em memória, enquanto o programa está em execução. */
public class RepositorioResultadosMemoria implements RepositorioResultados {
  private final List<ResultadoBatalha> resultados = new ArrayList<>();

  @Override
  public void salvar(ResultadoBatalha resultado) {
    resultados.add(resultado);
  }

  @Override
  public List<ResultadoBatalha> listar() {
    return List.copyOf(resultados);
  }
}
