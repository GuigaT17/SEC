package mequie.client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import mequie.client.response.CollectResponse;
import mequie.client.response.GInfoResponse;
import mequie.client.response.HistoryResponse;
import mequie.client.response.UInfoResponse;
import mequie.server.domain.Fotografia;
import mequie.server.domain.Mensagem;
import mequie.server.domain.Texto;

public class Mequie {
	//private static Socket clientSocket;
	private static String serverAddress;
	private static String ip;
	private static String porto;
	private static String utilizador;
	private static int portoInt;
	private static String username;
	private static String password;


	public static void main(String[] args) throws Exception{
		Scanner meuScanner = new Scanner(System.in);
		boolean posPass = true;

		//pode-se por sem a passe mas tem que se pedir depois
		if (args.length < 2) {
			System.out.println("Faltam um ou mais argumentos! É preciso um endereço do servidor, um username e uma password");
			System.out.println("Um username consiste de letras, numeros, _ e .");
			System.out.println("Exemplo: Mequie 1.2.3.4:5678 nerd1nformatic0 pass_Secreta!");


		} else if (args.length == 2) {
			posPass = false;
		}

		serverAddress = args[0];
		String[] dividido = serverAddress.split(":");
		ip = dividido[0];
		porto = dividido[1];
		username = args[1];

		while (dividido.length < 2 || 3 != ip.chars().filter(ch -> ch == '.').count() || porto.length() < 4) {
			System.out.println("O endereco:porto nao estah no formato correcto. Insira um de formato 1.2.3.4:5678");
			if (meuScanner.hasNext()) {
				serverAddress = meuScanner.next();
				dividido = serverAddress.split(":");
				ip = dividido[0];
				porto = dividido[1];
			}
		}
		portoInt = Integer.parseInt(porto);

		//PEGAR PASSWORD
		String password;
		if (!posPass) {
			System.out.println("Insira sua password!");
			password = meuScanner.next();
		} else {
			password = args[2];
		}
		Cliente cliente = new Cliente(username, ip, portoInt);
		//AUTENTICAR
		int autenticado = cliente.autenticacao(password);
		if(autenticado == -1) {
			System.out.println("Password incorreta!");
			meuScanner.close();
			return;
		} else if(autenticado == 0) {
			System.out.println("Utilizador nao existente, mas agora registrado!");
		}
		System.out.println("Utilizador autenticado com sucesso!");

		boolean flag = true;
		String linha = "";
		System.out.println("Insira um dos seguintes comandos:");
		System.out.println("create <groupID> - criar um chat de grupo");
		System.out.println("addu <userID> <groupID> - adicionar um utilizador a um grupo");
		System.out.println("removeu <userID> <groupID> - remover um utilizador de um grupo");
		System.out.println("ginfo <groupID> - mostrar informacao sobre um grupo");
		System.out.println("uinfo - mostrar informacao sobre o utilizador");
		System.out.println("msg <groupID> <msg> - enviar uma mensagem para o grupo");
		System.out.println("photo <groupID> <photo> - enviar uma foto para o grupo");
		System.out.println("collect <groupID> - ver as mensagens de grupo nao vistas");
		System.out.println("history <groupID> - ver historico de mensagens do grupo");
		while (flag) {

			//LENDO O COMANDO
			while(linha.equals("")){
				linha = meuScanner.nextLine();
			} 

			String[] comando = linha.split(" ");

			//EXECUTANDO O COMANDO
			switch (comando[0]) {
			//CRIANDO GRUPO
			case "create":
				if (comando.length != 2) {
					System.out.println("Insira o comando no formato: create <groupID>");
				} else {
					String groupName = comando[1];
					boolean groupCommand = cliente.create(groupName);
					if(groupCommand) {
						System.out.println("Grupo criado com sucesso!");
					} else {
						System.out.println("Erro na criacao do grupo!");
					}
				}
				break;

				//ADDUSER AO GRUPO
			case "addu":
				if(comando.length != 3) {
					System.out.println("Insira o comando no formato: addu <userID> <groupID>");
				} else {
					String userID = comando[1];
					String groupID = comando[2];
					if (groupID.equals("Geral")) {
						System.out.println("User nao pode ser adicionado ao grupo!");
						break;
					}
					boolean addUser = cliente.addu(userID, groupID);
					if(addUser) {
						System.out.println("User adicionado ao grupo com sucesso!");
					} else {
						System.out.println("User nao pode ser adicionado ao grupo!");
					}
				}
				break;

				//REMOVER USER
			case "removeu":
				if(comando.length != 3) {
					System.out.println("Insira o comando no formato: removeu <userID> <groupID>");
				} else {
					String userID = comando[1];
					String groupID = comando[2];
					if (groupID.equals("Geral")) {
						System.out.println("User nao pode ser removido do grupo!");
						break;
					}
					boolean removeUser = cliente.removeu(userID, groupID);
					if(removeUser) {
						System.out.println("User removido do grupo com sucesso!");
					} else {
						System.out.println("User nao pode ser removido do grupo!");
					}
				}
				break;

				//GROUP INFO
			case "ginfo":
				if(comando.length != 2) {
					System.out.println("Insira o comando no formato: ginfo <groupID>");
				} else {
					String groupID = comando[1];
					GInfoResponse ginfo = cliente.ginfo(groupID);
					//TODO imprimir as informações do grupo
					if(ginfo.isError()) {
						System.out.println("Erro ao pegar informacoes do grupo!");
					} else {
						System.out.println(ginfo.toString());
					}
				}
				break;

				//USER INFO
			case "uinfo":
				if(comando.length != 1) {
					System.out.println("Insira o comando no formato: uinfo");
				} else {
					UInfoResponse uinfo = cliente.uinfo();
					if(uinfo.getError()) {
						System.out.println("Utilizador não pertence e nem é dono de nenhum grupo");
					}else if(uinfo.getMemberGroups().size() != 0 && uinfo.getOwnerGroups().size() == 0) {
						System.out.println(uinfo.toString());
						System.out.println("Membro de: "+uinfo.getMemberGroups());
						System.out.println("Utilizador não é dono de nenhum grupo!");
					}else if(uinfo.getMemberGroups().size() == 0 && uinfo.getOwnerGroups().size() != 0) {
						System.out.println(uinfo.toString());
						System.out.println("Dono de: "+uinfo.getOwnerGroups());
						System.out.println("Utilizador não pertence a nenhum grupo!");
					}else {
						System.out.println(uinfo.toString());
						System.out.println("Membro de: "+uinfo.getMemberGroups());
						System.out.println("Dono de: "+uinfo.getOwnerGroups());
					}
				}
				break;

				//MESSAGE
			case "msg":
				if(comando.length < 3) {
					System.out.println("Insira o comando no formato: msg <groupID> <msg>");
				} else {
					String groupID = comando[1];
					if (groupID.equals("Geral")) {
						System.out.println("User nao pode enviar mensagem para esse grupo!");
						break;
					}
					
					boolean enviado = cliente.msg(groupID, linha.trim().substring(5 + groupID.length(), linha.length()));
					if(enviado) {
						System.out.println("Mensagem enviada com sucesso!");
					} else {
						System.out.println("Falha ao enviar a mensagem!");
					}
				}
				break;

				//PHOTO
			case "photo":
				if(comando.length != 3) {
					System.out.println("Insira o comando no formato: photo <groupID> <msg>");
				} else {
					String groupID = comando[1];
					if (groupID.equals("Geral")) {
						System.out.println("User nao pode enviar foto para esse grupo!");
						break;
					}
					boolean enviado = cliente.photo(groupID, comando[2]);
					if(enviado) {
						System.out.println("Foto enviada com sucesso!");
					} else {
						System.out.println("Falha ao enviar a foto!");
					}
				}
				break;

				//COLLECT
			case "collect":
				if(comando.length != 2) {
					System.out.println("Insira o comando no formato: collect <groupID>");
				} else {
					String groupID = comando[1];
					CollectResponse collect = cliente.collect(groupID);
					ArrayList<Mensagem> msgs = new ArrayList<>(collect.getMensagens());
					for (Mensagem m : msgs) {
						if (m instanceof Texto) {
							System.out.println(m.getRemetente() + ":" + ((Texto) m).getConteudo());
						} else if (m instanceof Fotografia) {
							System.out.println(m.getRemetente() + ":" + ((Fotografia) m).getConteudo().getPath());
						}
					}
					if(collect.isError()) {
						System.out.println("Erro ao buscar as mensagens!");
					} else {
						System.out.println("Sucesso!");
					}
				}
				break;


				//HISTORY
			case "history":
				if(comando.length != 2) {
					System.out.println("Insira o comando no formato: history <groupID>");
				} else {
					String groupID = comando[1];
					HistoryResponse history = cliente.history(groupID);
					ArrayList<Texto> msgs = new ArrayList<>(history.getHistory());
					for (Texto t : msgs) {
						System.out.println(t.getRemetente() + ":" + t.getConteudo());
					}
					if(history.isError()) {
						System.out.println("Erro ao buscar as mensagens!");
					} else {
						System.out.println("Sucesso!");
					}
				}
				break;

				//EXIT
			case "exit":
				System.out.println("Encerrando o programa!");
				cliente.quit();
				flag = false;

				//DEFAULT
			default:
				
			}
			linha = "";
		}
		meuScanner.close();
	}
}
