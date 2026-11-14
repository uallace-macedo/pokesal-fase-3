package br.com.pokesal.console;

import br.com.pokesal.batalha.RegistroBatalha;
import br.com.pokesal.enums.EfeitoStatus;
import br.com.pokesal.enums.TipoTerreno;
import br.com.pokesal.modelo.Ataque;
import br.com.pokesal.modelo.Item;
import br.com.pokesal.modelo.Pokesal;
import br.com.pokesal.modelo.Treinador;

/** Exibe no terminal os eventos de cada turno. */
public class RegistroBatalhaConsole implements RegistroBatalha {

    @Override
    public void registrarAtaque(Pokesal atacante, Ataque ataque, Pokesal defensor) {
        System.out.println(atacante.getNome() + " usa " + ataque.nome() + " em " + defensor.getNome());
    }

    @Override
    public void registrarTropeco(Pokesal atacante, String mensagem) {
        System.out.println("  " + mensagem);
    }

    @Override
    public void registrarDano(Pokesal defensor, int dano, boolean critico) {
        if (dano == 0) {
            System.out.println("  Nenhum dano causado.");
            return;
        }
        String sufixo = critico ? " (golpe crítico!)" : "";
        System.out.println("  " + defensor.getNome() + " perde " + dano + " HP" + sufixo);
    }

    @Override
    public void registrarEfeitoStatus(Pokesal pokesal, EfeitoStatus efeito, int dano) {
        System.out.println("  " + pokesal.getNome() + " está " + efeito + " e perde " + dano + " HP");
    }

    @Override
    public void registrarEfeitoTerreno(Pokesal pokesal, TipoTerreno terreno, int cura) {
        System.out.println(
                "  " + terreno.getNome() + ": " + pokesal.getNome() + " recupera " + cura + " HP");
    }

    @Override
    public void registrarUsoItem(Treinador treinador, Item item) {
        System.out.println(treinador.getNome() + " usa " + item.getTipo().getNome());
    }
}
