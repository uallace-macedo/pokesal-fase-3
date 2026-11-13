package torneio;

import resultado.ResultadoBatalha;
import resultado.Treinador;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Torneio eliminatório com 4 ou 8 treinadores. */
public class Torneio {
    private static final int MINIMO_PARTICIPANTES = 4;
    private static final int MAXIMO_PARTICIPANTES = 8;

    private final ExecutorBatalha executor;

    /**
     * Cria o torneio.
     *
     * @param executor responsável por disputar cada batalha
     */
    public Torneio(ExecutorBatalha executor) {
        this.executor = executor;
    }

    /**
     * Executa o chaveamento: a cada rodada, os participantes se enfrentam em pares, na ordem da
     * lista, e os vencedores avançam até restar um campeão.
     *
     * @param participantes 4 ou 8 treinadores, com nomes diferentes
     * @return campeão do torneio
     * @throws IllegalArgumentException se a quantidade não for 4 ou 8 ou houver nomes repetidos
     */
    public Treinador executar(List<Treinador> participantes) {
        validar(participantes);
        List<Treinador> rodada = new ArrayList<>(participantes);
        while (rodada.size() > 1) {
            List<Treinador> vencedores = new ArrayList<>();
            for (int i = 0; i < rodada.size(); i += 2) {
                Treinador a = rodada.get(i);
                Treinador b = rodada.get(i + 1);
                vencedores.add(escolherVencedor(a, b, executor.executar(a, b)));
            }
            rodada = vencedores;
        }
        return rodada.get(0);
    }

    private void validar(List<Treinador> participantes) {
        int total = participantes.size();
        if (total != MINIMO_PARTICIPANTES && total != MAXIMO_PARTICIPANTES) {
            throw new IllegalArgumentException("O torneio deve ter 4 ou 8 treinadores.");
        }
        Set<String> nomes = new HashSet<>();
        for (Treinador treinador : participantes) {
            if (!nomes.add(treinador.getNome())) {
                throw new IllegalArgumentException("Os treinadores devem ter nomes diferentes.");
            }
        }
    }

    private Treinador escolherVencedor(Treinador a, Treinador b, ResultadoBatalha resultado) {
        if (resultado.vencedor().equals(a.getNome())) {
            return a;
        }
        if (resultado.vencedor().equals(b.getNome())) {
            return b;
        }
        throw new IllegalStateException("O vencedor informado não participou da batalha.");
    }
}

