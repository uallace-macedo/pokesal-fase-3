package br.com.pokesal.terreno;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import br.com.pokesal.batalha.GeradorAleatorio;
import br.com.pokesal.enums.TipoTerreno;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServicoTerrenoClimaticoTest {
  private static final int TOTAL_DE_CLIMAS = 3;
  private static final int INDICE_ASFALTO = 0;
  private static final int INDICE_POCA = 1;
  private static final int INDICE_CANTEIRO = 2;

  @Mock private Clock relogio;
  @Mock private GeradorAleatorio gerador;
  @InjectMocks private ServicoTerrenoClimatico servico;

  private static Instant as(String horaUtc) {
    return Instant.parse("2026-11-06T" + horaUtc + ":00Z");
  }

  @Test
  @DisplayName("Asfalto Quente só tem efeito durante o dia")
  void testAsfaltoQuenteApenasDuranteODia() {
    when(gerador.proximoInteiro(TOTAL_DE_CLIMAS)).thenReturn(INDICE_ASFALTO);
    when(relogio.getZone()).thenReturn(ZoneOffset.UTC);
    when(relogio.instant()).thenReturn(as("15:00"), as("23:00"));

    TipoTerreno dia = servico.consultar();
    TipoTerreno noite = servico.consultar();

    assertEquals(TipoTerreno.ASFALTO_QUENTE, dia);
    assertEquals(TipoTerreno.SEM_EFEITO, noite);
  }

  @Test
  @DisplayName("Valores-limite do período diurno: 06:00 começa o dia e 18:00 começa a noite")
  void testLimitesDoPeriodoDiurno() {
    when(gerador.proximoInteiro(TOTAL_DE_CLIMAS)).thenReturn(INDICE_ASFALTO);
    when(relogio.getZone()).thenReturn(ZoneOffset.UTC);
    when(relogio.instant()).thenReturn(as("05:59"), as("06:00"), as("17:59"), as("18:00"));

    assertEquals(TipoTerreno.SEM_EFEITO, servico.consultar());
    assertEquals(TipoTerreno.ASFALTO_QUENTE, servico.consultar());
    assertEquals(TipoTerreno.ASFALTO_QUENTE, servico.consultar());
    assertEquals(TipoTerreno.SEM_EFEITO, servico.consultar());
  }

  @Test
  @DisplayName("Poça d'água independe do horário")
  void testPocaDAguaIndependeDoHorario() {
    when(gerador.proximoInteiro(TOTAL_DE_CLIMAS)).thenReturn(INDICE_POCA);

    assertEquals(TipoTerreno.POCA_DAGUA, servico.consultar());
    verifyNoInteractions(relogio);
  }

  @Test
  @DisplayName("Canteiro Central independe do horário")
  void testCanteiroCentralIndependeDoHorario() {
    when(gerador.proximoInteiro(TOTAL_DE_CLIMAS)).thenReturn(INDICE_CANTEIRO);

    assertEquals(TipoTerreno.CANTEIRO_CENTRAL, servico.consultar());
    verifyNoInteractions(relogio);
  }
}
