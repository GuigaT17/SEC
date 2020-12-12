package mequie.server.catalogs;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import mequie.server.domain.Fotografia;
import mequie.server.domain.Mensagem;
import mequie.server.domain.Texto;

public class CatalogoMensagens {

	private static CatalogoMensagens singleInstance = null;
	ArrayList<Mensagem> listaMensagens;
	
	private CatalogoMensagens() {
		this.listaMensagens = new ArrayList<>();
	}

	public static CatalogoMensagens getInstance() {
		if (singleInstance == null) 
			singleInstance = new CatalogoMensagens(); 

		return singleInstance;
	}

	public Texto novoTexto(String remetente, String conteudo) {
		Texto t = new Texto(conteudo, remetente, UUID.randomUUID().toString());
		t.jaViu(remetente);
		listaMensagens.add(t);
		return t;
	}
	
	public Fotografia novaFoto(String remetente, File conteudo) {
		Fotografia f = new Fotografia(conteudo, remetente, UUID.randomUUID().toString());
		f.jaViu(remetente);
		listaMensagens.add(f);
		return f;
	}
}
