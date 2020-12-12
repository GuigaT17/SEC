package mequie.server.catalogs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javafx.scene.shape.Path;
import mequie.client.response.GInfoResponse;
import mequie.server.domain.Fotografia;
import mequie.server.domain.Grupo;
import mequie.server.domain.Mensagem;
import mequie.server.domain.TuploMensagem;
import mequie.server.domain.Texto;
import mequie.server.files.ServerRestorer;
import mequie.server.files.ServerArquiver;

public class CatalogoGrupos {

	private static CatalogoGrupos singleInstance = null; 

	private Hashtable<String, Grupo> tabelaGrupos = new Hashtable<>();;
	private String filePath = "grupos.txt";


	private CatalogoGrupos() {	
		try {
			Hashtable<String, List<String>> tabelaFicheiro = ServerRestorer.listasNoFicheiro(filePath);

			for (String grupo : tabelaFicheiro.keySet()) {
				//PASSO 1 criar um grupo, com o dono (dono é String, nao Utilizador)
				tabelaGrupos.put(grupo, new Grupo(grupo, tabelaFicheiro.get(grupo).get(0)));
				//PASSO 2 meter os utilziadores (lá dentro grava como Utilizador, mas a classe Grupo tratar de ir buscar
				//ao catalogo o utilizador correspondente à String)
				for (int i = 1; i < tabelaFicheiro.get(grupo).size(); i++) {
					tabelaGrupos.get(grupo).adicionarUtilizador(tabelaFicheiro.get(grupo).get(i));
				}

				//PASSO 3 Ir a pasta onde estao os ficheiros com as mensagens
				String pathMensagens = grupo + "\\";

				File pasta = new File(grupo);
				if (pasta.exists() && pasta.isDirectory()) {
					//PASSO 4 repopular a caixa
					File fileCaixa = new File(pathMensagens + "caixa.txt");
					if(fileCaixa.exists()) {
						List<TuploMensagem> caixaMensagens = ServerRestorer.mensagensNoFicheiro(pathMensagens + "caixa.txt");
						ArrayList<Mensagem> caixaGrupo = new ArrayList<>();
						for (TuploMensagem q : caixaMensagens) {
							if (q.getTipo().equals("texto")) {
								Texto t = CatalogoMensagens.getInstance().novoTexto(q.getRemetente(), q.getConteudo());
								for (String user : q.getUtilizadores()) {
									t.verMensagem(user);
								}
								caixaGrupo.add(t);
							} else if(q.getTipo().equals("foto")) {
								Fotografia f = CatalogoMensagens.getInstance().novaFoto(q.getRemetente(), new File(q.getConteudo()));
								for (String user : q.getUtilizadores()) {
									f.verMensagem(user);
								}
								caixaGrupo.add(f);
							}
						}
						tabelaGrupos.get(grupo).setCaixa(caixaGrupo);
					}
					//PASSO 5 repopular o historico
					File fileHistorico = new File(pathMensagens + "historico.txt");
					if(fileHistorico.exists()) {
						List<TuploMensagem> historicoMensagens = ServerRestorer.mensagensNoFicheiro(pathMensagens + "historico.txt");
						ArrayList<Texto> historicoGrupo = new ArrayList<>();
						for (TuploMensagem q : historicoMensagens) {
							Texto t = CatalogoMensagens.getInstance().novoTexto(q.getRemetente(), q.getConteudo());
							for (String user : q.getUtilizadores()) {
								t.verMensagem(user);
							}
							historicoGrupo.add(t);
						}
						tabelaGrupos.get(grupo).setHistorico(historicoGrupo); 
					}
				}
			}
			if (!this.tabelaGrupos.containsKey("Geral")) {
				this.tabelaGrupos.put("Geral", new Grupo ("Geral", "MeQuie"));
				ServerArquiver.updateGrupos();
			}
			
		}catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static CatalogoGrupos getInstance() {
		if (singleInstance == null) 
			singleInstance = new CatalogoGrupos(); 

		return singleInstance;
	}

	public Grupo getGrupo(String grupo) {
		return tabelaGrupos.get(grupo);
	}

	public List<Grupo> getGrupos(){
		return new ArrayList<>(this.tabelaGrupos.values());
	}

	public boolean adicionarGrupo(String grupo, String dono) {
		if (tabelaGrupos.get(grupo) != null && tabelaGrupos.get(grupo).getDono().equals(dono)) {
			return false;
		}
		tabelaGrupos.put(grupo, new Grupo(grupo, dono));
		if (tabelaGrupos.containsKey(grupo)) {
			try {
				ArrayList<String> users = new ArrayList<>();
				for (String u : this.tabelaGrupos.get(grupo).getUtilizadores()) {
					users.add(u);
				}
				ServerArquiver.escreverGrupo(this.tabelaGrupos.get(grupo));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	public boolean adicionarAoGrupo(String grupo, String dono, String username) {
		if(tabelaGrupos.get(grupo) == null) {
			return false;
		}
		Grupo g = tabelaGrupos.get(grupo);
		if (!g.ehDono(dono) || !CatalogoUtilizadores.getInstance().existeUser(username) || g.contemUtilizador(username) || (g.ehDono(dono) && dono.equals(username))) {
			return false;
		}
		g.adicionarUtilizador(username);
		try {
			ServerArquiver.updateGrupos();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean removerDoGrupo(String grupo, String dono, String username) {
		Grupo g = tabelaGrupos.get(grupo);
		if (g == null || !g.ehDono(dono)) {
			return false;
		}
		boolean feito = false;
		if(g.ehDono(dono) && dono.equals(username)) {
			if(g.getUtilizadores().size() == 0) {
				tabelaGrupos.remove(grupo);
				feito = true;
			} else {
				return false;
			}
		} else {
			feito = g.removerUtilizador(username);
		}
		try {
			ServerArquiver.updateGrupos();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return feito;
	}


	public GInfoResponse informacaoDoGrupo(String grupo, String username) {
		Grupo g = tabelaGrupos.get(grupo);
		if (g == null) {
			return new GInfoResponse(true);
		}
		return tabelaGrupos.get(grupo).informacaoDoGrupo(username);
	}

	public boolean enviarMensagem(String grupo, String utilizador, String mensagem) {
		Grupo g = tabelaGrupos.get(grupo);
		if (g == null || !g.contemUtilizador(utilizador)) {
			return false;
		}		
		Texto t = CatalogoMensagens.getInstance().novoTexto(utilizador, mensagem);
		boolean adicionou = g.adicionarMensagem(t);
		if (adicionou) {
			try {
				ServerArquiver.escreverMensagem(grupo + "\\caixa.txt", t, g);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	public boolean enviarFoto(String grupo, File foto, String utilizador) {
		Grupo g = tabelaGrupos.get(grupo);
		if (g == null || !g.contemUtilizador(utilizador)) {
			return false;
		}		
		Fotografia f = CatalogoMensagens.getInstance().novaFoto(utilizador, foto);
		boolean adicionou = g.adicionarFoto(f);
		if (adicionou) {
			try {
				ServerArquiver.escreverMensagem(grupo + "\\caixa.txt", f, g);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	public String verHistorico(String grupo, String utilizador) {
		Grupo g = tabelaGrupos.get(grupo);
		if (g == null || !g.contemUtilizador(utilizador)) {
			return null;
		}		
		StringBuilder sb = new StringBuilder();
		List<Texto> lista = g.getHistorico();
		for (Texto t : lista) {
			sb.append(t.getRemetente() + ":" + t.getConteudo() + "\n");
		}
		return sb.toString();
	}
}
