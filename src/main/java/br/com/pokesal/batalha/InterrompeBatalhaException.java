package br.com.pokesal.batalha;

/** Lançada quando a Equipe Pokemal interrompe uma batalha em andamento (REQ-01). */
public class InterrompeBatalhaException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /** Mensagem exata da exceção. */
  public static final String MENSAGEM = "Equipe Pokemal interrompeu a batalha";

  /** Cria a exceção com a mensagem padrão. */
  public InterrompeBatalhaException() {
    super(MENSAGEM);
  }
}
