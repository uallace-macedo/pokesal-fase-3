package br.com.pokesal.batalha;

import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.TipoTerreno;
import br.com.pokesal.modelo.Ataque;
import br.com.pokesal.modelo.Item;
import br.com.pokesal.modelo.Pokesal;
import br.com.pokesal.modelo.Treinador;

/**
 * Recebe os eventos de cada turno. Em um turno os eventos chegam na ordem: ataque, dano, efeito de
 * status, efeito de terreno; o uso de item substitui o ataque do treinador.
 */
public interface RegistroBatalha {

  /**
   * Registra a execução de um ataque.
   *
   * @param atacante Pokésal que ataca
   * @param ataque golpe usado
   * @param defensor Pokésal alvo
   */
  void registrarAtaque(Pokesal atacante, Ataque ataque, Pokesal defensor);

  /**
   * Registra que o Pokésal tropeçou e errou o ataque (REQ-02). Chega entre o ataque e o dano.
   *
   * @param atacante Pokésal que tropeçou
   * @param mensagem texto a exibir
   */
  void registrarTropeco(Pokesal atacante, String mensagem);

  /**
   * Registra o dano causado por um ataque.
   *
   * @param defensor Pokésal que recebeu o dano
   * @param dano dano causado, zero se o golpe errou
   * @param critico {@code true} se o golpe foi crítico
   */
  void registrarDano(Pokesal defensor, int dano, boolean critico);

  /**
   * Registra o efeito de status aplicado ao final do turno.
   *
   * @param pokesal Pokésal afetado
   * @param efeito efeito ativo
   * @param dano dano causado pelo efeito neste turno
   */
  void registrarEfeitoStatus(Pokesal pokesal, EfeitoStatus efeito, int dano);

  /**
   * Registra o efeito do terreno aplicado ao final do turno.
   *
   * @param pokesal Pokésal afetado
   * @param terreno terreno da batalha
   * @param cura HP efetivamente recuperado
   */
  void registrarEfeitoTerreno(Pokesal pokesal, TipoTerreno terreno, int cura);

  /**
   * Registra o uso de um item.
   *
   * @param treinador treinador que usou o item
   * @param item item usado
   */
  void registrarUsoItem(Treinador treinador, Item item);
}
