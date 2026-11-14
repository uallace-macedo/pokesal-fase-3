package br.com.pokesal.modelo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.TipoElemental;
import br.com.pokesal.enums.TipoItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AcaoTest {
    private final Ataque ataque =
            new Ataque("Golpe", TipoElemental.NORMAL, 10, 100, EfeitoStatus.NENHUM);
    private final Item item = new Item(TipoItem.POCAO);

    @Test
    @DisplayName("Ação de ataque e ação de item são distinguíveis")
    void testTiposDeAcao() {
        assertAll(
                () -> assertTrue(Acao.atacar(ataque).ehAtaque()),
                () -> assertFalse(Acao.usarItem(item).ehAtaque()));
    }

    @Test
    @DisplayName("Uma ação precisa de exatamente um ataque ou um item")
    void testAcaoInvalida() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Acao(null, null)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Acao(ataque, item)));
    }
}
