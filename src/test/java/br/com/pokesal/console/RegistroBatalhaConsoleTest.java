package br.com.pokesal.console;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.TipoElemental;
import br.com.pokesal.enums.TipoTerreno;
import br.com.pokesal.modelo.Ataque;
import br.com.pokesal.modelo.Item;
import br.com.pokesal.modelo.Pokesal;
import br.com.pokesal.modelo.Treinador;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegistroBatalhaConsoleTest {

  private final ByteArrayOutputStream saida = new ByteArrayOutputStream();
  private PrintStream saidaOriginal;
  private RegistroBatalhaConsole registro;

  @BeforeEach
  void setUp() {
    saidaOriginal = System.out;
    System.setOut(new PrintStream(saida, true, StandardCharsets.UTF_8));
    registro = new RegistroBatalhaConsole();
  }

  @AfterEach
  void tearDown() {
    System.setOut(saidaOriginal);
  }

  @Test
  @DisplayName("Ataque registra o atacante, o movimento e o defensor")
  void testRegistrarAtaque() {
    Pokesal atacante = criarPokesal("Pikachu");
    Pokesal defensor = criarPokesal("Charmander");
    Ataque ataque = new Ataque("Choque", TipoElemental.NORMAL, 20, 100, EfeitoStatus.NENHUM);

    registro.registrarAtaque(atacante, ataque, defensor);

    assertTrue(saida.toString().contains("Pikachu usa Choque em Charmander"));
  }

  @Test
  @DisplayName("Tropeço registra a mensagem recebida")
  void testRegistrarTropeco() {
    registro.registrarTropeco(criarPokesal("Pikachu"), "O ataque falhou!");

    assertTrue(saida.toString().contains("O ataque falhou!"));
  }

  @Test
  @DisplayName("Dano positivo registra a quantidade de HP perdida")
  void testRegistrarDano() {
    registro.registrarDano(criarPokesal("Pikachu"), 20, false);

    assertTrue(saida.toString().contains("Pikachu perde 20 HP"));
  }

  @Test
  @DisplayName("Dano crítico identifica o golpe como crítico")
  void testRegistrarDanoCritico() {
    registro.registrarDano(criarPokesal("Pikachu"), 40, true);

    assertTrue(saida.toString().contains("(golpe crítico!)"));
  }

  @Test
  @DisplayName("Dano zero informa que nenhum dano foi causado")
  void testRegistrarDanoZero() {
    registro.registrarDano(criarPokesal("Pikachu"), 0, false);

    assertTrue(saida.toString().contains("Nenhum dano causado."));
  }

  @Test
  @DisplayName("Efeito de status registra o efeito e o dano recebido")
  void testRegistrarEfeitoStatus() {
    registro.registrarEfeitoStatus(criarPokesal("Pikachu"), EfeitoStatus.QUEIMADO, 5);

    String resultado = saida.toString();

    assertAll(
        () -> assertTrue(resultado.contains("Pikachu")),
        () -> assertTrue(resultado.contains("QUEIMADO")),
        () -> assertTrue(resultado.contains("perde 5 HP")));
  }

  @Test
  @DisplayName("Efeito de terreno registra a recuperação de HP")
  void testRegistrarEfeitoTerreno() {
    registro.registrarEfeitoTerreno(criarPokesal("Pikachu"), TipoTerreno.CANTEIRO_CENTRAL, 10);

    String resultado = saida.toString();

    assertTrue(resultado.contains("Pikachu recupera 10 HP"));
  }

  @Test
  @DisplayName("Uso de item registra o treinador e o item")
  void testRegistrarUsoItem() {
    Treinador treinador = new Treinador("Ash", criarPokesal("Pikachu"));
    Item item = treinador.getInventario().get(0);

    registro.registrarUsoItem(treinador, item);

    assertTrue(saida.toString().contains("Ash usa " + item.getTipo().getNome()));
  }

  private Pokesal criarPokesal(String nome) {
    return new Pokesal(nome, TipoElemental.FOGO, 100, 50, 30, 55, List.of());
  }
}
