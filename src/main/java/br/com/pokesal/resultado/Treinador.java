package resultado;

import modelo.Pokesal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Treinador que conduz um Pokésal e possui um item de cada tipo. */
public class Treinador {
    /** Quantidade máxima de itens que o treinador pode usar por batalha. */
    public static final int LIMITE_ITENS = 2;

    private final String nome;
    private final Pokesal pokesal;
    private final List<Item> inventario = new ArrayList<>();
    private int itensUsados;

    /**
     * Cria um treinador com um item de cada tipo.
     *
     * @param nome nome do treinador
     * @param pokesal Pokésal escolhido
     */
    public Treinador(String nome, Pokesal pokesal) {
        this.nome = nome;
        this.pokesal = pokesal;
        for (TipoItem tipo : TipoItem.values()) {
            inventario.add(new Item(tipo));
        }
    }

    /**
     * Indica se ainda há uso de item disponível. O limite é compartilhado entre todos os tipos.
     *
     * @return {@code true} se menos de dois itens foram usados
     */
    public boolean podeUsarItem() {
        return itensUsados < LIMITE_ITENS;
    }

    /**
     * Usa um item do inventário no próprio Pokésal.
     *
     * @param item item a usar
     * @return {@code true} se o item foi usado; {@code false} se o limite foi atingido, o item já
     *     foi usado ou não pertence ao inventário
     */
    public boolean usarItem(Item item) {
        if (!podeUsarItem() || !inventario.contains(item) || !item.podeSerUsado()) {
            return false;
        }
        item.aplicar(pokesal);
        itensUsados++;
        return true;
    }

    /** Prepara o treinador para uma nova batalha: restaura o Pokésal e os itens. */
    public void reiniciar() {
        pokesal.restaurar();
        itensUsados = 0;
        inventario.forEach(Item::reiniciar);
    }

    /**
     * Retorna o nome.
     *
     * @return nome do treinador
     */
    public String getNome() {
        return nome;
    }

    /**
     * Retorna o Pokésal.
     *
     * @return Pokésal do treinador
     */
    public Pokesal getPokesal() {
        return pokesal;
    }

    /**
     * Retorna o inventário.
     *
     * @return lista não modificável de itens
     */
    public List<Item> getInventario() {
        return Collections.unmodifiableList(inventario);
    }

    /**
     * Retorna quantos itens já foram usados.
     *
     * @return itens usados na batalha atual
     */
    public int getItensUsados() {
        return itensUsados;
    }
}

