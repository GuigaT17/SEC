package mequie.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import mequie.client.Mequie;
import mequie.client.Cliente;
import mequie.server.catalogs.CatalogoGrupos;
import mequie.server.catalogs.CatalogoUtilizadores;
import mequie.server.connection.ServerThread;
import mequie.server.domain.Mensagem;

public class MequieServer {
	private static ServerSocket server;
	private static Socket socket;
	private static ObjectInputStream in;
	private static ObjectOutputStream out;

	public static void main(String[] args) {
		int porto = Integer.valueOf(args[0]);
		MequieServer servidor = new MequieServer();
		servidor.comecarServidor(porto);
	}
	
	public void comecarServidor (int porto){
		try {
			server = new ServerSocket(porto);
			System.out.println("MequieServer started");
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		boolean flag = true;
		while(flag) {
			try {
				System.out.println("Waiting for a client ...");
				socket = server.accept();
				System.out.println("Client accepted");
				Thread thread = new ServerThread(socket);
				thread.start();
			} catch(IOException i) {
				try {
					server.close();
					flag = false;
				} catch (IOException o) {
					o.printStackTrace();
				}
			}
		}
	}
}
