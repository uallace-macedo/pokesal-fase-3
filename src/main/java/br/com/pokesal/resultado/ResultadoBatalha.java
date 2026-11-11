package resultado;
/**
 * Resultado de uma batalha.
 *
 * @param vencedor nome do treinador vencedor
 * @param perdedor nome do treinador perdedor
 * @param turnos quantidade de turnos disputados
 * @param pokesalVencedor nome do Pokésal do vencedor
 * @param pokesalPerdedor nome do Pokésal do perdedor
 */
public record ResultadoBatalha(
        String vencedor, String perdedor, int turnos, String pokesalVencedor, String pokesalPerdedor) {}

