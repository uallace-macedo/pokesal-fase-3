package br.com.pokesal.terreno;

import br.com.pokesal.enums.TipoTerreno;

/** Serviço que informa o terreno da batalha considerando clima e horário. */
public interface ServicoTerreno {

  /**
   * Consulta o terreno atual.
   *
   * @return terreno com efeito ativo, ou {@code SEM_EFEITO}
   */
  TipoTerreno consultar();
}
