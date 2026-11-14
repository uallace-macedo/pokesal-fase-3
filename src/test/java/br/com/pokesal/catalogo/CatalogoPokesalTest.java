package br.com.pokesal.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.TipoElemental;
import br.com.pokesal.modelo.Ataque;
import br.com.pokesal.modelo.Pokesal;
import java.util.List;
import org.junit.jupiter.api.Test;

class CatalogoPokesalTest {

  @Test
  void deveCriarSeisPokesals() {
    List<Pokesal> pokesals = CatalogoPokesal.criarIniciais();

    assertEquals(6, pokesals.size());
  }

  @Test
  void deveCriarIniciaisComTresAtaques() {
    List<Pokesal> pokesals = CatalogoPokesal.criarIniciais();

    for (Pokesal pokesal : pokesals) {
      assertEquals(3, pokesal.getAtaques().size());
    }
  }

  @Test
  void deveCriarBulbaSalComAtributosCorretos() {
    Pokesal bulbaSal = CatalogoPokesal.criarIniciais().get(0);

    assertEquals("BulbaSal", bulbaSal.getNome());
    assertEquals(TipoElemental.PLANTA, bulbaSal.getTipo());
    assertEquals(110, bulbaSal.getHpMaximo());
    assertEquals(45, bulbaSal.getAtaque());
    assertEquals(50, bulbaSal.getDefesa());
    assertEquals(40, bulbaSal.getVelocidade());
  }

  @Test
  void deveCriarAtaquesComDanosCorretos() {
    List<Pokesal> pokesals = CatalogoPokesal.criarIniciais();

    for (Pokesal pokesal : pokesals) {
      List<Ataque> ataques = pokesal.getAtaques();

      assertEquals(15, ataques.get(0).getDanoBase());
      assertEquals(25, ataques.get(1).getDanoBase());
    }
  }

  @Test
  void deveCriarAtaquesDeStatusComDanosCorretos() {
    List<Pokesal> pokesals = CatalogoPokesal.criarIniciais();

    for (Pokesal pokesal : pokesals) {
      Ataque ataqueStatus = pokesal.getAtaques().get(2);

      if (ataqueStatus.getEfeitoStatus() == EfeitoStatus.QUEIMADO) {
        assertEquals(15, ataqueStatus.getDanoBase());
      } else {
        assertEquals(10, ataqueStatus.getDanoBase());
      }
    }
  }

  @Test
  void deveCriarAtaquesComOsEfeitosDeStatusEsperados() {
    List<Pokesal> pokesals = CatalogoPokesal.criarIniciais();

    assertEquals(EfeitoStatus.ENVENENADO, pokesals.get(0).getAtaques().get(2).getEfeitoStatus());

    assertEquals(EfeitoStatus.QUEIMADO, pokesals.get(1).getAtaques().get(2).getEfeitoStatus());

    assertEquals(EfeitoStatus.PARALISADO, pokesals.get(2).getAtaques().get(2).getEfeitoStatus());
  }

  @Test
  void investidaNaoDeveSerElemental() {
    List<Pokesal> pokesals = CatalogoPokesal.criarIniciais();

    for (Pokesal pokesal : pokesals) {
      assertEquals(false, pokesal.getAtaques().get(0).isElemental());
    }
  }
}
