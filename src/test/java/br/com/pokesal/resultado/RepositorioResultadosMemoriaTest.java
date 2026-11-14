package br.com.pokesal.resultado;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RepositorioResultadosMemoriaTest {
    private RepositorioResultadosMemoria repositorio;
    private final ResultadoBatalha primeiro =
            new ResultadoBatalha("Ana", "Bia", 4, "CharSal", "TotoSal");
    private final ResultadoBatalha segundo =
            new ResultadoBatalha("Caio", "Davi", 6, "BulbaSal", "CyndaSal");

    @BeforeEach
    void setUp() {
        repositorio = new RepositorioResultadosMemoria();
    }

    @Test
    @DisplayName("Começa vazio")
    void testComecaVazio() {
        assertTrue(repositorio.listar().isEmpty());
    }

    @Test
    @DisplayName("Guarda os resultados na ordem em que foram salvos")
    void testSalvarMantemOrdem() {
        repositorio.salvar(primeiro);
        repositorio.salvar(segundo);

        assertEquals(List.of(primeiro, segundo), repositorio.listar());
    }

    @Test
    @DisplayName("A lista devolvida não altera o repositório")
    void testListaDevolvidaEhCopia() {
        repositorio.salvar(primeiro);
        List<ResultadoBatalha> lista = repositorio.listar();

        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> lista.add(segundo)),
                () -> assertEquals(1, repositorio.listar().size()));
    }
}
