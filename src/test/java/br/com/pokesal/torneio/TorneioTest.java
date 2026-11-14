package br.com.pokesal.torneio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import br.com.pokesal.enums.TipoElemental;
import br.com.pokesal.modelo.Pokesal;
import br.com.pokesal.modelo.Treinador;
import br.com.pokesal.resultado.ResultadoBatalha;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TorneioTest {
    @Mock private ExecutorBatalha executor;
    @InjectMocks private Torneio torneio;

    private static Treinador criar(String nome) {
        Pokesal pokesal = new Pokesal(nome + "Sal", TipoElemental.FOGO, 100, 50, 30, 50, List.of());
        return new Treinador(nome, pokesal);
    }

    private static ResultadoBatalha venceu(String vencedor, String perdedor) {
        return new ResultadoBatalha(vencedor, perdedor, 3, vencedor + "Sal", perdedor + "Sal");
    }

    @Test
    @DisplayName("Os vencedores de cada rodada avançam até o campeão")
    void testChaveamentoTorneioAvancaVencedores() {
        Treinador ana = criar("Ana");
        Treinador bia = criar("Bia");
        Treinador caio = criar("Caio");
        Treinador davi = criar("Davi");
        when(executor.executar(ana, bia)).thenReturn(venceu("Ana", "Bia"));
        when(executor.executar(caio, davi)).thenReturn(venceu("Caio", "Davi"));
        when(executor.executar(ana, caio)).thenReturn(venceu("Caio", "Ana"));

        Treinador campeao = torneio.executar(List.of(ana, bia, caio, davi));

        assertSame(caio, campeao);
        InOrder ordem = inOrder(executor);
        ordem.verify(executor).executar(ana, bia);
        ordem.verify(executor).executar(caio, davi);
        ordem.verify(executor).executar(ana, caio);
        verify(executor, times(3)).executar(any(Treinador.class), any(Treinador.class));
    }

    @Test
    @DisplayName("Com 8 treinadores acontecem 7 batalhas")
    void testTorneioComOitoTreinadores() {
        List<Treinador> participantes =
                List.of("A", "B", "C", "D", "E", "F", "G", "H").stream().map(TorneioTest::criar).toList();
        when(executor.executar(any(Treinador.class), any(Treinador.class)))
                .thenAnswer(
                        invocacao -> {
                            Treinador primeiro = invocacao.getArgument(0);
                            Treinador segundo = invocacao.getArgument(1);
                            return venceu(primeiro.getNome(), segundo.getNome());
                        });

        Treinador campeao = torneio.executar(participantes);

        assertSame(participantes.get(0), campeao);
        verify(executor, times(7)).executar(any(Treinador.class), any(Treinador.class));
    }

    @Test
    @DisplayName("Quantidade diferente de 4 ou 8 treinadores é rejeitada")
    void testQuantidadeInvalidaDeParticipantes() {
        List<Treinador> tres = List.of(criar("Ana"), criar("Bia"), criar("Caio"));

        assertThrows(IllegalArgumentException.class, () -> torneio.executar(tres));
        verifyNoInteractions(executor);
    }

    @Test
    @DisplayName("Nomes repetidos são rejeitados")
    void testNomesRepetidos() {
        List<Treinador> repetidos = List.of(criar("Ana"), criar("Ana"), criar("Caio"), criar("Davi"));

        assertThrows(IllegalArgumentException.class, () -> torneio.executar(repetidos));
        verifyNoInteractions(executor);
    }

    @Test
    @DisplayName("Vencedor que não participou da batalha é um erro")
    void testVencedorInesperado() {
        Treinador ana = criar("Ana");
        Treinador bia = criar("Bia");
        List<Treinador> participantes = List.of(ana, bia, criar("Caio"), criar("Davi"));
        when(executor.executar(ana, bia)).thenReturn(venceu("Fulano", "Bia"));

        assertThrows(IllegalStateException.class, () -> torneio.executar(participantes));
    }

    @Test
    @DisplayName("Falha ao disputar uma batalha interrompe o torneio")
    void testFalhaDoExecutorInterrompeTorneio() {
        Treinador ana = criar("Ana");
        Treinador bia = criar("Bia");
        List<Treinador> participantes = List.of(ana, bia, criar("Caio"), criar("Davi"));
        when(executor.executar(ana, bia)).thenThrow(new IllegalStateException("falha"));

        assertThrows(IllegalStateException.class, () -> torneio.executar(participantes));
        verify(executor, times(1)).executar(any(Treinador.class), any(Treinador.class));
        assertEquals(4, participantes.size());
    }
}
