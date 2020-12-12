package mequie.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import mequie.client.response.CollectResponse;
import mequie.client.response.GInfoOwnerResponse;
import mequie.client.response.GInfoResponse;
import mequie.client.response.HistoryResponse;
import mequie.client.response.UInfoResponse;
import mequie.server.catalogs.CatalogoGrupos;
import mequie.server.domain.Fotografia;
import mequie.server.domain.Texto;

public class Cliente{

	private String utilizador;
	private static Socket clientSocket;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;
	private OutputStream output;
	private InputStream input;

	public Cliente(String utilizador, String ip, int port) {
		this.utilizador = utilizador;
		try {
			clientSocket = new Socket(ip, port);
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new ObjectInputStream(clientSocket.getInputStream());
			output = clientSocket.getOutputStream();
			input = clientSocket.getInputStream();
			System.out.println("Connected");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/*
	 * Metodo que autentica ou nao o cliente
	 * @param password = senha
	 * @return -1 sse existir o utilizador, mas a password for errada
	 * 			 0 sse nao existir o utilizador e o mesmo foi registrado no sistema
	 * 			 1 sse foi autenticado com sucesso
	 */
	public int autenticacao(String password) {
		int autenticado = -1;
		String resposta = "";
		try {
			out.writeObject("autenticacao");
			out.writeObject(utilizador);
			out.writeObject(password);
			resposta = (String) in.readObject();
			autenticado = Integer.parseInt(resposta);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return autenticado;
	}


	/*
	 * Metodo para criar um novo grupo
	 * @param name = nome do grupo
	 * @return true sse o grupo foi criado com sucesso
	 */
	public boolean create(String name) {
		boolean criado = false;
		try {
			out.writeObject("create");
			out.writeObject(name);
			String resposta = (String) in.readObject();
			int x = Integer.parseInt(resposta);
			criado = x == 1;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return criado;	
	}


	/*
	 * Metodo para adicionar um utilizador a um grupo
	 * @param user = nome do usuario a adicionar
	 * 		  group = nome do grupo
	 * @return true sse o user foi adicionado ao grupo com sucesso
	 */
	public boolean addu(String user, String group) {
		boolean adicionou = false;
		try {
			out.writeObject("addu");
			out.writeObject(user);
			out.writeObject(group);
			String resposta = (String) in.readObject();
			int x = Integer.parseInt(resposta);
			adicionou = x == 1;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return adicionou;
	}

	/*
	 * Metodo para remover um utilizador de um grupo
	 * @param userID = nome do usuario a adicionar
	 * 		  groupID = nome do grupo
	 * @return true sse o user foi removido do grupo com sucesso
	 */
	public boolean removeu(String userID, String groupID) {
		boolean removeu = false;
		try {
			out.writeObject("removeu");
			out.writeObject(userID);
			out.writeObject(groupID);
			String resposta = (String) in.readObject();
			int x = Integer.parseInt(resposta);
			removeu = x == 1;		
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return removeu;
	}

	/*
	 * Metodo que devolve a informacao de um dado grupo
	 * @param groupID = nome do grupo
	 * @return informacao do grupo, GInfoResponse com boolean de erro caso nao tenha informacao
	 */
	public GInfoResponse ginfo(String groupID) {
		GInfoResponse ginfo = null;
		try {
			out.writeObject("ginfo");
			out.writeObject(groupID);
			boolean e = in.readBoolean();
			if(e) {
				ginfo = new GInfoResponse(true);
			} else {
				String owner = (String) in.readObject();
				int n = Integer.parseInt((String) in.readObject());
				if(owner.equals(utilizador)) {
					ginfo = new GInfoOwnerResponse(groupID, owner, n);
					int next = Integer.parseInt((String) in.readObject());
					while(next == 1) {
						String user = (String) in.readObject();
						((GInfoOwnerResponse) ginfo).addUser(user);
						next = Integer.parseInt((String) in.readObject());
					}
				} else {
					ginfo = new GInfoResponse(groupID, owner, n);
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return ginfo;
	}

	/*
	 * Metodo que devolve a informacao do cliente atual
	 */
	public UInfoResponse uinfo() {
		UInfoResponse uinfo = new UInfoResponse(utilizador);
		try {
			out.writeObject("uinfo");
			out.writeObject(utilizador);
			int next = Integer.parseInt((String) in.readObject());
			if(next == 0) uinfo.setError();
			int count = 0;
			while(next == 1) {
				String group = (String) in.readObject();
				if(!group.equals("MembroDe:") && !group.equals("DonoDe:")){
					if(!group.equals("middle")) {
						uinfo.addGroup(group);
					}else {
						count = 1;
					}
					if(count == 0) {
						uinfo.addMemberGroup(group);
					}else {
						if(!group.equals("middle")) uinfo.addOwnerGroup(group);
					}
				}
				next = Integer.parseInt((String) in.readObject());
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return uinfo;
	}

	/*
	 * Metodo que envia uma mensagem de texto
	 * @param groupID = grupo a que a mensagem eh enviada
	 * 		  substring = mensagem a ser enviada
	 * @return true sse a mensagem foi enviada com sucesso
	 */
	public boolean msg(String groupID, String substring) {
		boolean enviado = false;
		try {
			out.writeObject("msg");
			out.writeObject(groupID);
			out.writeObject(substring);
			enviado = ((String) in.readObject()).equals("1");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return enviado;
	}


	public boolean photo(String groupID, String ficheiro) {
		boolean foiEnviado = false;
		File file = new File(ficheiro);
		if (!file.exists()) {
			return false;
		}
		try {
			out.writeObject("photo");
			out.writeObject(groupID);
			out.writeObject(ficheiro);

			if(in.readObject().equals("-1")) {
				return false;
			}
			output = clientSocket.getOutputStream();
			BufferedImage image = ImageIO.read(file);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", byteArrayOutputStream);
			byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
			output.write(size);
			output.write(byteArrayOutputStream.toByteArray());
			String resposta = (String)in.readObject();
			int response = Integer.parseInt(resposta);
			if(response == 1) foiEnviado = true;
		} catch (IOException | ClassNotFoundException i) {
			i.printStackTrace();
			return false;
		}
		return true;
	}


	public CollectResponse collect(String groupID) {
		CollectResponse response = new CollectResponse();
		try {
			out.writeObject("collect");
			out.writeObject(groupID);
			if (((String) in.readObject()).equals(":inicio:")) {
				boolean naoParar = true;
				while (naoParar) {
					String remetente = (String) in.readObject();
					if (remetente.equals(":fim:")) {
						naoParar = false;
						break;
					}
					String tipo = (String) in.readObject();
					if (tipo.equals("Texto")) {
						String conteudo = (String) in.readObject();
						Texto t = new Texto(remetente, conteudo);
						response.addMensagem(t);
					}
					if (tipo.equals("Foto")){
						String path = (String) in.readObject();	
						byte[] sizeAr = new byte[4];
						input.read(sizeAr);
						int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
						byte[] imageAr = new byte[size];
						input.read(imageAr);
						BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
						File imagem = new File(path);
						ImageIO.write(image, path.substring(path.indexOf(".") + 1), imagem);
						Fotografia f = new Fotografia(remetente, imagem);
						response.addMensagem(f);
					}
				}
			} else {
				response.setError(true);
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return response;
	}

	public HistoryResponse history(String groupID) {
		HistoryResponse response = new HistoryResponse();
		try {
			out.writeObject("history");
			out.writeObject(groupID);
			if (((String) in.readObject()).equals(":inicio:")) {
				boolean naoParar = true;
				while (naoParar) {
					String remetente = (String) in.readObject();
					if (remetente.equals(":fim:")) {
						naoParar = false;
						break;
					}
					String conteudo = (String) in.readObject();
					Texto t = new Texto(remetente, conteudo);
					response.addTexto(t);
				}
			} else {
				response.setError(true);
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return response;
	}

	/*
	 * Metodo para fechar o cliente e avisar o servidor que o cliente vai encerrar
	 */
	public void quit() {
		try {
			out.writeObject("exit");
			out.close();
			in.close();
			output.close();
			input.close();
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}


