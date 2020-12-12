package mequie.server.domain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mequie.client.response.GInfoOwnerResponse;
import mequie.client.response.GInfoResponse;
import mequie.server.catalogs.CatalogoUtilizadores;
import mequie.server.files.ServerArquiver;

public class Grupo {

	private String nome;
	private String dono;
	private ArrayList<String> utilizadores;
	private ArrayList<Mensagem> caixaMensagens;
	private ArrayList<Texto> historicoMensagens;


	public Grupo(String nome, String dono) {
		this.nome = nome;
		this.dono = dono;
		this.utilizadores = new ArrayList<>();
		this.caixaMensagens = new ArrayList<>();
		this.historicoMensagens = new ArrayList<>();
	}

	public String getNome() {
		return nome;
	}

	public String getDono() {
		return dono;
	}

	public boolean ehDono(String utilizador) {
		return dono.equals(utilizador);
	}

	public List<String> getUtilizadores(){		
		return new ArrayList<>(utilizadores);
	}

	public boolean adicionarUtilizador(String utilizador) {
		return utilizadores.add(utilizador);		
	}

	public boolean removerUtilizador(String utilizador) {
		utilizadores.remove(utilizador);
		return !utilizadores.contains(utilizador);
	}

	public boolean contemUtilizador(String utilizador) {
		return utilizadores.contains(utilizador) || utilizador.equals(dono);
	}

	public GInfoResponse informacaoDoGrupo(String u) {
		GInfoResponse rt;
		if(this.dono.equals(u)) {
			rt = new GInfoOwnerResponse();
			((GInfoOwnerResponse) rt).setAllUsers(this.utilizadores);
		} else {
			rt = new GInfoResponse();
		}
		rt.setGroupID(nome);
		rt.setOwner(dono);
		rt.setNumberUsers(1 + this.utilizadores.size());
		return rt;
	}

	public boolean adicionarMensagem(Texto texto) {
		return caixaMensagens.add(texto);
	}

	public boolean adicionarFoto(Fotografia fotografia) {
		return caixaMensagens.add(fotografia);
	}

	public List<Mensagem> getCaixa() {
		return new ArrayList<>(caixaMensagens);
	}

	public List<Texto> getHistorico() {
		return new ArrayList<>(historicoMensagens);
	}

	public void refrescarHistorico() {
		ArrayList<Mensagem> aMudar = new ArrayList<>();
		for (Mensagem mensagem : caixaMensagens) {
			boolean todosViram = true;
			if (!mensagem.jaViu(dono)) {
				todosViram = false;
			}
			for (String u : utilizadores) {
				if (!mensagem.jaViu(u) ||!mensagem.jaViu(dono)) {
					todosViram = false;
				}
			}
			if (todosViram) {
				aMudar.add(mensagem);
			}
		}
		for (Mensagem mensagem : aMudar) {
			if (mensagem instanceof Texto) {
				historicoMensagens.add((Texto) mensagem);
			} else if (mensagem instanceof Fotografia) {
				((Fotografia) mensagem).getConteudo().delete();
			}
			caixaMensagens.remove(mensagem);
		}
		try {
			ServerArquiver.updateMensagens(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setCaixa(List<Mensagem> caixaGrupo) {
		this.caixaMensagens = new ArrayList<>(caixaGrupo);		
	}

	public void setHistorico(List<Texto> historicoGrupo) {
		this.historicoMensagens = new ArrayList<>(historicoGrupo);		
	}


}
