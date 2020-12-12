package mequie.server.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javafx.util.Pair;
import mequie.server.domain.TuploMensagem;

public class ServerRestorer {
	
	public static Hashtable<String, String> paresNoFicheiro(String path) throws IOException{
		Hashtable<String,String> rt = new Hashtable<>();
		File file = new File(path);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String st;
		while((st = br.readLine()) != null) {
			String[] dados = st.split(":");
			rt.put(dados[0], dados[1]);
		}
		br.close();
		return rt;
}
	
	public static Hashtable<String, List<String>> listasNoFicheiro(String path) throws IOException {
		Hashtable<String, List<String>> rt = new Hashtable<>();
		File file = new File(path);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String st;
		while((st = br.readLine()) != null) {
			String[] groupData = st.split(":");
			String nome = groupData[0];
			List<String> ls = new ArrayList<>();
			for(int i = 1; i < groupData.length; i++) {
				ls.add(groupData[i]);
			}
			rt.put(nome, ls);
		}
		br.close();
		return rt;
	}
	
	public static List<TuploMensagem> mensagensNoFicheiro(String path) throws IOException {
		List<TuploMensagem> rt = new ArrayList<>();
		File file = new File(path);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String st;
		while((st = br.readLine()) != null) {
			String[] messageData = st.split(":");
			String messageID = messageData[0];
			String tipo = messageData[1];
			String remetente = messageData[2];
			String mensagem = messageData[3];
			if (tipo.equals("foto")){
				System.out.println(path);
				String[] caminho = path.split("\\\\");
				mensagem = caminho[0] + "\\" + mensagem;
			}
			ArrayList<String> users = new ArrayList<>();
			for (int i = 4; i < messageData.length; i++) {
				users.add(messageData[i]);
			}
			TuploMensagem tuplo = new TuploMensagem(messageID, tipo, remetente, mensagem, users);
			rt.add(tuplo);
		}
		br.close();
		return rt;
	}
}
