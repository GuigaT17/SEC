package mequie.server.domain;

import java.io.File;

public class Fotografia extends Mensagem{

	private File imagem;
	
	public Fotografia(File imagem, String utilizador, String messageID) {
		this.imagem = imagem;
		this.remetente = utilizador;
		this.messageID = messageID;
	}
	
	public Fotografia(String remetente, File imagem) {
		this.imagem = imagem;
		this.remetente = remetente;	}

	public File getConteudo() {
		return imagem;
	}
}
