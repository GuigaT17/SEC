package mequie.server.domain;

import java.util.ArrayList;
import java.util.List;

public class TuploMensagem {

	private final String messageID;
    private final String tipo;
    private final String remetente;
    private final String conteudo;
    private final ArrayList<String> utilizadores;

    public TuploMensagem(String messageID, String tipo, String remetente, String conteudo, List<String> list) {
        this.messageID = messageID;
		this.tipo = tipo;
        this.remetente = remetente;
        this.conteudo = conteudo;
        this.utilizadores = new ArrayList<>(list);
    }

    public String getMessageID() {
    	return messageID;
    }
    
	public String getTipo() {
		return tipo;
	}

	public String getRemetente() {
		return remetente;
	}

	public String getConteudo() {
		return conteudo;
	}

	public List<String> getUtilizadores() {
		return utilizadores;
	}

}