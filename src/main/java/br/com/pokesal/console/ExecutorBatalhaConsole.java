package br.com.pokesal.console;

import br.com.pokesal.batalha.Batalha;
import br.com.pokesal.batalha.ContextoBatalha;
import br.com.pokesal.batalha.GeradorAleatorio;
import br.com.pokesal.batalha.RegistroBatalha;
import br.com.pokesal.modelo.Acao;
import br.com.pokesal.modelo.Ataque;
import br.com.pokesal.modelo.Item;
import br.com.pokesal.modelo.Pokesal;
import br.com.pokesal.modelo.Treinador;
import br.com.pokesal.resultado.RepositorioResultados;
import br.com.pokesal.resultado.ResultadoBatalha;
import br.com.pokesal.terreno.ServicoTerreno;
import br.com.pokesal.torneio.ExecutorBatalha;
import java.util.ArrayList;
import java.util.List;

/** Disputa uma batalha no terminal, lendo a ação de cada treinador a cada turno. */
public class ExecutorBatalhaConsole implements ExecutorBatalha {
    private final EntradaConsole entrada;
    private final ServicoTerreno servicoTerreno;
    private final GeradorAleatorio gerador;
    private final RepositorioResultados repositorio;
    private final RegistroBatalha registro;
    private final ContextoBatalha contexto;

    /**
     * Cria o executor com os colaboradores que serão repassados a cada batalha.
     *
     * @param entrada leitura do terminal
     * @param servicoTerreno serviço de terreno
     * @param gerador gerador de aleatoriedade
     * @param repositorio repositório de resultados
     * @param registro registro de eventos
     * @param contexto estado externo que pode interromper a batalha
     */
    public ExecutorBatalhaConsole(
            EntradaConsole entrada,
            ServicoTerreno servicoTerreno,
            GeradorAleatorio gerador,
            RepositorioResultados repositorio,
            RegistroBatalha registro,
            ContextoBatalha contexto) {
        this.entrada = entrada;
        this.servicoTerreno = servicoTerreno;
        this.gerador = gerador;
        this.repositorio = repositorio;
        this.registro = registro;
        this.contexto = contexto;
    }

    @Override
    public ResultadoBatalha executar(Treinador treinador1, Treinador treinador2) {
        treinador1.reiniciar();
        treinador2.reiniciar();
        Batalha batalha =
                new Batalha(
                        treinador1, treinador2, servicoTerreno, gerador, repositorio, registro, contexto);
        System.out.println();
        System.out.println(
                "=== " + treinador1.getNome() + " x " + treinador2.getNome()
                        + " | Terreno: " + batalha.getTerreno().getNome() + " ===");
        while (!batalha.estaTerminada()) {
            exibirEstado(treinador1, treinador2);
            Acao acao1 = lerAcao(treinador1);
            Acao acao2 = lerAcao(treinador2);
            System.out.println();
            batalha.executarTurno(acao1, acao2);
        }
        ResultadoBatalha resultado = batalha.getResultado().orElseThrow();
        System.out.println(
                "Vencedor: " + resultado.vencedor() + " em " + resultado.turnos() + " turnos");
        return resultado;
    }

    private void exibirEstado(Treinador a, Treinador b) {
        System.out.println();
        for (Treinador t : List.of(a, b)) {
            Pokesal p = t.getPokesal();
            System.out.println(
                    t.getNome() + " - " + p.getNome() + " HP " + p.getHpAtual() + "/" + p.getHpMaximo()
                            + " [" + p.getEfeitoStatus() + "]");
        }
    }

    private Acao lerAcao(Treinador treinador) {
        Pokesal pokesal = treinador.getPokesal();
        System.out.println("Vez de " + treinador.getNome() + ":");
        List<Ataque> ataques = pokesal.getAtaques();
        for (int i = 0; i < ataques.size(); i++) {
            Ataque ataque = ataques.get(i);
            System.out.println(
                    "  " + (i + 1) + " - " + ataque.nome() + " (dano " + ataque.danoBase()
                            + ", precisão " + ataque.precisao() + ")");
        }
        List<Item> itens = itensDisponiveis(treinador);
        int opcaoItem = ataques.size() + 1;
        if (!itens.isEmpty()) {
            System.out.println("  " + opcaoItem + " - Usar item");
        }
        int maximo = itens.isEmpty() ? ataques.size() : opcaoItem;
        int escolha = entrada.lerOpcao("Escolha: ", 1, maximo);
        if (escolha < opcaoItem) {
            return Acao.atacar(ataques.get(escolha - 1));
        }
        for (int i = 0; i < itens.size(); i++) {
            System.out.println("  " + (i + 1) + " - " + itens.get(i).getTipo().getNome());
        }
        return Acao.usarItem(itens.get(entrada.lerOpcao("Item: ", 1, itens.size()) - 1));
    }

    private List<Item> itensDisponiveis(Treinador treinador) {
        List<Item> itens = new ArrayList<>();
        if (treinador.podeUsarItem()) {
            for (Item item : treinador.getInventario()) {
                if (item.podeSerUsado()) {
                    itens.add(item);
                }
            }
        }
        return itens;
    }
}
