package torneio;

import resultado.ResultadoBatalha;
import resultado.Treinador;

/** Disputa uma batalha entre dois treinadores e devolve o resultado. */
public interface ExecutorBatalha {

    /**
     * Executa uma batalha completa.
     *
     * @param treinador1 primeiro treinador
     * @param treinador2 segundo treinador
     * @return resultado da batalha
     */
    ResultadoBatalha executar(Treinador treinador1, Treinador treinador2);
}