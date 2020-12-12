package mequie.server.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mequie.server.catalogs.CatalogoGrupos;
import mequie.server.domain.Fotografia;
import mequie.server.domain.Grupo;
import mequie.server.domain.Mensagem;
import mequie.server.domain.Texto;
import mequie.server.domain.TuploMensagem;
import mequie.server.domain.Utilizador;

public class ServerArquiver {

	private static String filePathGrupos = "grupos.txt";

	public static void escreverPar(String filePath, String username, String password) throws IOException {
		File file = new File(filePath);
		FileWriter fileWriter = new FileWriter(file, true);
		BufferedWriter writer = new BufferedWriter(fileWriter);
		writer.write(username + ":" + password + "\n");
		writer.close();
		fileWriter.close();
	}

	public static void escreverGrupo(Grupo grupo) throws IOException {
		File file = new File(filePathGrupos);
		FileWriter fileWriter = new FileWriter(file, true);
		BufferedWriter writer = new BufferedWriter(fileWriter);
		StringBuilder sb = new StringBuilder();
		for (String utilizador : grupo.getUtilizadores()) {
			sb.append(utilizador + ":");
		}
		if (sb.length() > 1) {
			sb.deleteCharAt(sb.length() - 1);
		}
		writer.write(grupo.getNome() + ":" + grupo.getDono() + ":" + sb.toString() + "\n");
		writer.close();
		fileWriter.close();
	}

	public static void updateGrupos() throws IOException {
		File file = new File(filePathGrupos);
		file.delete();
		List<Grupo> grupos = CatalogoGrupos.getInstance().getGrupos();
		for (Grupo g : grupos) {
			ServerArquiver.escreverGrupo(g);
		}

	}

	public static void escreverMensagem(String filePath, Mensagem mensagem, Grupo grupo) throws IOException {
		boolean diretorio = new File(grupo.getNome()).mkdir();
		File file = new File(filePath);
		FileWriter fileWriter = new FileWriter(file, true);
		BufferedWriter writer = new BufferedWriter(fileWriter);
		StringBuilder sb = new StringBuilder();
		String tipo = "";
		String conteudo = "";
		if (mensagem instanceof Texto) {
			tipo = "texto";
			conteudo = ((Texto) mensagem).getConteudo();
		} else if (mensagem instanceof Fotografia) {
			tipo = "foto";
			String pathPasta = ((Fotografia) mensagem).getConteudo().getPath();
			conteudo = pathPasta.substring(pathPasta.indexOf("\\") + 1);
		}
		TuploMensagem tuplo = new TuploMensagem(mensagem.getMessageID(), tipo, mensagem.getRemetente(), conteudo, mensagem.getJaViram());
		sb.append(tuplo.getMessageID() + ":");
		sb.append(tuplo.getTipo() + ":");
		sb.append(tuplo.getRemetente() + ":");
		sb.append(tuplo.getConteudo() + ":");
		for (int i = 1; i < tuplo.getUtilizadores().size(); i++) {
			sb.append(tuplo.getUtilizadores().get(i) + ":");
		}
		if (sb.length() > 1) {
			sb.deleteCharAt(sb.length() - 1);
		}		
		writer.write(sb.toString() + "\n");
		writer.close();
		fileWriter.close();
	}

	public static void updateMensagens(Grupo grupo) throws IOException {
		String pathCaixa = grupo.getNome() + "\\caixa.txt";
		String pathHistorico = grupo.getNome() + "\\historico.txt";
		File fileCaixa = new File(pathCaixa);
		fileCaixa.delete();
		File fileHistorico = new File(pathHistorico);
		fileHistorico.delete();
		for (Mensagem m : grupo.getCaixa()) {
			ServerArquiver.escreverMensagem(pathCaixa, m, grupo);
		}
		for (Mensagem m : grupo.getHistorico()) {
			ServerArquiver.escreverMensagem(pathHistorico, m, grupo);
		}
	}
}
