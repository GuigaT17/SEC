package mequie.server.domain;

public class Texto extends Mensagem {

	private String conteudo;

	public Texto(String mensagem, String utilizador, String messageID) {
		this.conteudo = mensagem;
		this.remetente = utilizador;
		this.messageID = messageID;
	}

	public Texto(String remetente, String conteudo) {
		this.conteudo = conteudo;
		this.remetente = remetente;
	}

	public String getConteudo() {
		return conteudo;
	}

	public String toString() {
		return "De: " + remetente + "\n" + conteudo;
	}
}
