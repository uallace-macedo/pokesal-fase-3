package br.com.pokesal.modelo;

import br.com.pokesal.enums.TipoItem;
import java.util.ArrayList;
import java.util.List;

/** Representa um treinador participante da batalha. */
public class Treinador {

  private static final int LIMITE_USO_ITENS = 2;

  private final String nome;
  private final Pokesal pokesal;
  private final List<Item> inventario;
  private int itensUsados;

  /**
   * Cria um treinador com um pokesal e seu inventário inicial.
   *
   * @param nome nome do treinador
   * @param pokesal pokesal escolhido
   */
  public Treinador(String nome, Pokesal pokesal) {
    if (nome == null || nome.isBlank()) {
      throw new IllegalArgumentException("O nome do treinador não pode ser vazio");
    }

    if (pokesal == null) {
      throw new IllegalArgumentException("O Pokésal do treinador é obrigatório");
    }

    this.nome = nome;
    this.pokesal = pokesal;
    this.inventario = new ArrayList<>();

    inventario.add(new Item("Poção", TipoItem.POCAO, 1));
    inventario.add(new Item("Super Poção", TipoItem.SUPER_POCAO, 1));
    inventario.add(new Item("Antídoto", TipoItem.ANTIDOTO, 1));

    this.itensUsados = 0;
  }

  public String getNome() {
    return nome;
  }

  public Pokesal getPokesal() {
    return pokesal;
  }

  /**
   * Retorna uma cópia independente do inventário.
   *
   * @return cópia do inventário
   */
  public List<Item> getInventario() {
    return inventario.stream().map(Item::copiar).toList();
  }

  public int getItensUsados() {
    return itensUsados;
  }

  public boolean podeUsarItem() {
    return itensUsados < LIMITE_USO_ITENS;
  }

  /**
   * Procura um item pelo tipo e retorna uma cópia independente.
   *
   * @param tipo tipo procurado
   * @return cópia do item ou null se não existir
   */
  public Item buscarItem(TipoItem tipo) {
    Item item = localizarItem(tipo);
    return item == null ? null : item.copiar();
  }

  /**
   * Utiliza um item do inventário no pokesal do treinador.
   *
   * @param item item solicitado
   * @return true se o item foi utilizado
   */
  public boolean usarItem(Item item) {
    if (!podeUsarItem()) {
      throw new IllegalStateException("O limite de utilização de itens foi atingido");
    }

    if (item == null) {
      return false;
    }

    Item itemDisponivel = localizarItem(item.getTipo());

    if (itemDisponivel == null || !itemDisponivel.podeSerUsado(pokesal)) {
      return false;
    }

    itemDisponivel.aplicar(pokesal);
    itensUsados++;

    return true;
  }

  /**
   * Utiliza um item pelo tipo.
   *
   * @param tipo tipo do item
   * @return true se o item foi utilizado
   */
  public boolean usarItem(TipoItem tipo) {
    return usarItem(buscarItem(tipo));
  }

  private Item localizarItem(TipoItem tipo) {
    if (tipo == null) {
      return null;
    }

    return inventario.stream().filter(item -> item.getTipo() == tipo).findFirst().orElse(null);
  }
}
