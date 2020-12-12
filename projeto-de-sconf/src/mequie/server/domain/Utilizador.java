package mequie.server.domain;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import mequie.server.catalogs.CatalogoGrupos;

public class Utilizador {

	private String nome;
	private transient String password;
	private ArrayList<Grupo> grupos;

	public Utilizador(String utilizador, String password) {
		this.nome = utilizador;
		this.password = password;
		this.grupos = new ArrayList<>();
	}

	public String getNome() {
		return nome;
	}

	public String getPassword() {
		return password;
	}

	public boolean adicionarGrupo(Grupo grupo) {
		return grupos.add(grupo);
	}
	
	public List<Grupo> getGrupos(){
		return grupos;
	}

	public List<String> informacaoDoUtilizador() {
	  	StringBuilder sb = new StringBuilder();
		this.grupos = (ArrayList<Grupo>) CatalogoGrupos.getInstance().getGrupos();
		List<String> lista = new ArrayList<>();
		lista.add("MembroDe:");
		for (Grupo grupo : grupos) {
			if (grupo.getUtilizadores().contains(this.nome)) {
				lista.add(grupo.getNome());
			}
		}
		lista.add("middle");
		lista.add("DonoDe:");
		for (Grupo grupo : grupos) {
			if (grupo.ehDono(nome)) {
				lista.add(grupo.getNome());
			}
		}
		return lista;	
	}
}
