package br.com.pokesal.catalogo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.TipoElemental;
import br.com.pokesal.modelo.Ataque;
import br.com.pokesal.modelo.Pokesal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CatalogoPokesalTest {
  private List<Pokesal> iniciais;

  @BeforeEach
  void setUp() {
    iniciais = CatalogoPokesal.criarIniciais();
  }

  @Test
  @DisplayName("O catálogo tem os seis Pokésals iniciais, na ordem")
  void testNomesDosIniciais() {
    assertEquals(
        List.of("BulbaSal", "CharSal", "SquirtSal", "ChikoSal", "CyndaSal", "TotoSal"),
        iniciais.stream().map(Pokesal::getNome).toList());
  }

  @Test
  @DisplayName("BulbaSal tem os atributos da tabela do projeto")
  void testAtributosDoBulbaSal() {
    Pokesal bulba = iniciais.get(0);

    assertAll(
        () -> assertEquals(TipoElemental.PLANTA, bulba.getTipo()),
        () -> assertEquals(110, bulba.getHpMaximo()),
        () -> assertEquals(45, bulba.getAtaqueEfetivo()),
        () -> assertEquals(50, bulba.getDefesa()),
        () -> assertEquals(40, bulba.getVelocidadeEfetiva()));
  }

  @Test
  @DisplayName("CyndaSal é o mais rápido, com velocidade 60")
  void testVelocidadeDoCyndaSal() {
    assertEquals(60, iniciais.get(4).getVelocidadeEfetiva());
  }

  @Test
  @DisplayName("Cada Pokésal tem ataque normal, elemental e com efeito de status")
  void testTresAtaquesPorPokesal() {
    for (Pokesal pokesal : iniciais) {
      Ataque normal = pokesal.getAtaques().get(0);
      Ataque elemental = pokesal.getAtaques().get(1);
      Ataque comStatus = pokesal.getAtaques().get(2);

      assertAll(
          () -> assertEquals(3, pokesal.getAtaques().size()),
          () -> assertEquals("Investida", normal.nome()),
          () -> assertEquals(TipoElemental.NORMAL, normal.tipo()),
          () -> assertEquals(15, normal.danoBase()),
          () -> assertEquals(pokesal.getTipo(), elemental.tipo()),
          () -> assertEquals(25, elemental.danoBase()),
          () -> assertNotEquals(EfeitoStatus.NENHUM, comStatus.efeitoStatus()));
    }
  }

  @Test
  @DisplayName("Cada chamada cria novas instâncias, com HP cheio")
  void testNovasInstanciasACadaChamada() {
    iniciais.get(0).receberDano(50);

    List<Pokesal> novos = CatalogoPokesal.criarIniciais();

    assertAll(
        () -> assertNotSame(iniciais.get(0), novos.get(0)),
        () -> assertEquals(110, novos.get(0).getHpAtual()));
  }
}
