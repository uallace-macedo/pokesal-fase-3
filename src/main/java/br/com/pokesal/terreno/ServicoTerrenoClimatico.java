package br.com.pokesal.terreno;

import br.com.pokesal.batalha.GeradorAleatorio;
import br.com.pokesal.enums.TipoTerreno;

import java.time.Clock;
import java.time.LocalTime;

/**
 * Serviço de terreno que sorteia o clima do dia e usa o relógio para decidir se o efeito está
 * ativo. O Asfalto Quente só tem efeito durante o dia, entre 6h (inclusive) e 18h (exclusive).
 */
public class ServicoTerrenoClimatico implements ServicoTerreno {
    private static final TipoTerreno[] CLIMAS = {
            TipoTerreno.ASFALTO_QUENTE, TipoTerreno.POCA_DAGUA, TipoTerreno.CANTEIRO_CENTRAL
    };
    private static final LocalTime INICIO_DIA = LocalTime.of(6, 0);
    private static final LocalTime FIM_DIA = LocalTime.of(18, 0);

    private final Clock relogio;
    private final GeradorAleatorio gerador;

    /**
     * Cria o serviço.
     *
     * @param relogio relógio que informa o horário atual
     * @param gerador fonte de aleatoriedade para sortear o clima
     */
    public ServicoTerrenoClimatico(Clock relogio, GeradorAleatorio gerador) {
        this.relogio = relogio;
        this.gerador = gerador;
    }

    @Override
    public TipoTerreno consultar() {
        TipoTerreno sorteado = CLIMAS[gerador.proximoInteiro(CLIMAS.length)];
        if (sorteado == TipoTerreno.ASFALTO_QUENTE && !ehDia()) {
            return TipoTerreno.SEM_EFEITO;
        }
        return sorteado;
    }

    private boolean ehDia() {
        LocalTime agora = LocalTime.now(relogio);
        return !agora.isBefore(INICIO_DIA) && agora.isBefore(FIM_DIA);
    }
}

