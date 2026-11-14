package br.com.pokesal.modelo;

/**
 * Ação escolhida por um treinador em um turno: atacar ou usar item.
 *
 * @param ataque golpe escolhido, ou {@code null} se a ação for usar item
 * @param item item escolhido, ou {@code null} se a ação for atacar
 */
public record Acao(Ataque ataque, Item item) {

    /**
     * Valida que exatamente um dos dois campos foi informado.
     *
     * @throws IllegalArgumentException se ambos ou nenhum forem informados
     */
    public Acao {
        if ((ataque == null) == (item == null)) {
            throw new IllegalArgumentException("Informe apenas um ataque ou apenas um item.");
        }
    }

    /**
     * Cria uma ação de ataque.
     *
     * @param ataque golpe a executar
     * @return ação de ataque
     */
    public static Acao atacar(Ataque ataque) {
        return new Acao(ataque, null);
    }

    /**
     * Cria uma ação de uso de item.
     *
     * @param item item a usar
     * @return ação de uso de item
     */
    public static Acao usarItem(Item item) {
        return new Acao(null, item);
    }

    /**
     * Indica se a ação é um ataque.
     *
     * @return {@code true} se for ataque
     */
    public boolean ehAtaque() {
        return ataque != null;
    }
}
