package br.com.pokesal.batalha;

/** Estado externo que controla a batalha, como o pedido de parada da Equipe Pokemal. */
public class ContextoBatalha {
    private boolean parada;

    /**
     * Indica se a batalha deve ser interrompida.
     *
     * @return {@code true} se a Equipe Pokemal pediu a parada
     */
    public boolean isParada() {
        return parada;
    }

    /**
     * Define se a batalha deve ser interrompida.
     *
     * @param parada {@code true} para interromper a batalha em andamento
     */
    public void setParada(boolean parada) {
        this.parada = parada;
    }
}
