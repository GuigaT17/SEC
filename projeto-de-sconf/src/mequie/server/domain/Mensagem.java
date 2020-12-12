package mequie.server.domain;

import java.util.ArrayList;
import java.util.List;

import mequie.server.catalogs.CatalogoUtilizadores;

public abstract class Mensagem {
	
	protected String messageID;
	protected String remetente;
	protected ArrayList<String> jaViram = new ArrayList<>();
	
	public String getRemetente() {
		return remetente;
	}
	
	public boolean verMensagem(String utilizador) {
		if (!this.jaViu(utilizador)) {
			return jaViram.add(utilizador);
		}
		return true;
	}
	
	public List<String> getJaViram(){
		return jaViram;
	}

	public boolean jaViu(String utilizador) {
		return jaViram.contains(utilizador);
	}
	
	public String getMessageID() {
		return messageID;
	}

}
