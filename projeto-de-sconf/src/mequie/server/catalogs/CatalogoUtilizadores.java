package mequie.server.catalogs;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import mequie.server.domain.Mensagem;
import mequie.server.domain.Grupo;
import mequie.server.domain.Utilizador;
import mequie.server.files.ServerArquiver;
import mequie.server.files.ServerRestorer;

public class CatalogoUtilizadores {

	private static CatalogoUtilizadores singleInstance = null; 
	private String filePath = "utilizadores.txt";
	private Hashtable<String, Utilizador> tabelaUtilizadores = new Hashtable<>();


	private CatalogoUtilizadores() {
		try {
			Hashtable<String, String> tabelaAut = ServerRestorer.paresNoFicheiro(filePath);
			for (String utilizador : tabelaAut.keySet()) {
				tabelaUtilizadores.put(utilizador, new Utilizador(utilizador, tabelaAut.get(utilizador)));
			} 
		}catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static CatalogoUtilizadores getInstance() {
		if (singleInstance == null) 
			singleInstance = new CatalogoUtilizadores();
		return singleInstance;
	}
	
	public boolean existeUser(String u) {
		return tabelaUtilizadores.containsKey(u);
	}

	public boolean autenticacao(String utilizador, String password) {
		if (utilizador.contains(":")) {
			return false;
		}
		if (tabelaUtilizadores.containsKey(utilizador)) {
			return password.equals(tabelaUtilizadores.get(utilizador).getPassword());
		} else {
			try {
				ServerArquiver.escreverPar(filePath, utilizador, password);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Utilizador u = new Utilizador(utilizador, password);
			tabelaUtilizadores.put(utilizador, u);
			CatalogoGrupos.getInstance().adicionarAoGrupo("Geral", "MeQuie", utilizador);
			return true;
		}
	}

	public List<String> getNomes() {
		return new ArrayList<>(tabelaUtilizadores.keySet());
	}

	public List<Utilizador> getUtilizadores() {
		return new ArrayList<>(tabelaUtilizadores.values());
	}

	public Utilizador getUtilizador(String utilizador) {
		return tabelaUtilizadores.get(utilizador);
	}

	public List<String> informacaoDoUtilizador(String utilizador) {
		return this.getUtilizador(utilizador).informacaoDoUtilizador();
	}

    /**
     * Devolve as mensagens enviadas para grupo, que o utilizador ainda não viu.
     * @param grupo
     * @param utilizador
     * @return
     */
	public List<Mensagem> verMensagens(String grupo, String utilizador) {
		Grupo g = CatalogoGrupos.getInstance().getGrupo(grupo);
		ArrayList<Mensagem> mensagensAVer = new ArrayList<>();
		for (Mensagem mensagem : g.getCaixa()) {
			if (mensagem.jaViu(utilizador)) {
				mensagensAVer.add(mensagem);
				mensagem.verMensagem(utilizador);
			}
		}
		g.refrescarHistorico();
		return mensagensAVer;
	}
}
