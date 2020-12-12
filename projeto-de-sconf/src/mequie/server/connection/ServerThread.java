package mequie.server.connection;

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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import mequie.client.response.GInfoOwnerResponse;
import mequie.client.response.GInfoResponse;
import mequie.client.response.UInfoResponse;
import mequie.server.catalogs.CatalogoGrupos;
import mequie.server.catalogs.CatalogoMensagens;
import mequie.server.catalogs.CatalogoUtilizadores;
import mequie.server.domain.Fotografia;
import mequie.server.domain.Grupo;
import mequie.server.domain.Mensagem;
import mequie.server.domain.Texto;

//Threads utilizadas para comunicacao com os clientes
public class ServerThread extends Thread {
	private Socket socket = null;
	private String user;
	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;
	private InputStream input;
	private OutputStream output;

	public ServerThread() {
		super();
	}

	public ServerThread(Socket socket) {
		super();
		this.socket = socket;
	}

	public ServerThread(String user) {
		super();
		this.user = user;
	}

	public ServerThread(Socket socket, String user) {
		super();
		this.socket = socket;
		this.user = user;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void run() {
		String op = null;
		String username = null;
		String password = null;
		try {
			outStream = new ObjectOutputStream(socket.getOutputStream());
			inStream = new ObjectInputStream(socket.getInputStream());
			input = socket.getInputStream();
			output = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			op = (String) inStream.readObject();
			username = (String) inStream.readObject();
			password = (String) inStream.readObject();
			int autResult = autentica(username, password);
			outStream.writeObject(Integer.toString(autResult));
			if(autResult != 1) {
				return;
			} else {
				this.setUser(username);
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		boolean flag = true;
		while(flag) {
			try {
				op = (String) inStream.readObject();
			} catch (ClassNotFoundException | IOException e) {
				flag = false;
				e.printStackTrace();
			}

			switch(op) {
			//CREATE
			case "create":
				try {
					String name = (String) inStream.readObject();
					int criado = createGroup(name) ? 1 : -1;
					outStream.writeObject(Integer.toString(criado));
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
				break;

				//ADDUSER
			case "addu":
				try {
					String u = (String) inStream.readObject();
					String g = (String) inStream.readObject();
					int adicionou = adicionarAGrupo(g, u) ? 1 : -1;
					outStream.writeObject(Integer.toString(adicionou));
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
				break;

				//REMOVE
			case "removeu":
				try {
					String u = (String) inStream.readObject();
					String g = (String) inStream.readObject();
					int removeu = removerDoGrupo(g, u) ? 1 : -1;
					outStream.writeObject(Integer.toString(removeu));
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
				break;

			case "ginfo":
				try {
					String g = (String) inStream.readObject();
					GInfoResponse ginfo = this.informacaoGrupo(g);
					boolean error = ginfo.isError();
					outStream.writeBoolean(error);
					if(!error) {
						outStream.writeObject(ginfo.getOwner());
						outStream.writeObject(ginfo.getNumberUsers() + "");
						if(ginfo.getOwner().equals(username)) {
							List<String> nomes = ((GInfoOwnerResponse) ginfo).getAllUsers();
							if(nomes.size() > 0) {
								outStream.writeObject("1");
							} else {
								outStream.writeObject("0");
							}
							for(int i = 0; i < nomes.size(); i++) {
								outStream.writeObject(nomes.get(i));
								if(i == nomes.size() - 1) {
									outStream.writeObject("0");
								} else {
									outStream.writeObject("1");
								}
							}
						}
					}
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
				break;

			case "uinfo":
				try {
					List<String> uinfo = this.informacaoUser();
					outStream.writeObject("1");
					for(int i = 0; i < uinfo.size(); i++) {
						if(uinfo.size() == 0) outStream.writeObject("0");
						if(i == uinfo.size()-1) {
							outStream.writeObject(uinfo.get(i));
							outStream.writeObject("-1");
						}
						outStream.writeObject(uinfo.get(i));
						outStream.writeObject("1");
					}

				} catch (IOException e) {
					e.printStackTrace();
				} 
				break;

			case "msg":
				try {
					String g = (String) inStream.readObject();
					String msg = (String) inStream.readObject();
					int enviada = CatalogoGrupos.getInstance().enviarMensagem(g, user, msg) ? 1 : -1;
					outStream.writeObject(Integer.toString(enviada));
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();

				}
				break;

			case "collect":
				try {
					String g = (String) inStream.readObject();
					List<Mensagem> mensagens = CatalogoGrupos.getInstance().getGrupo(g).getCaixa();
					outStream.writeObject(":inicio:");
					for (Mensagem m : mensagens) {
						outStream.writeObject(m.getRemetente());
						if (m instanceof Texto) {
							outStream.writeObject("Texto");
							outStream.writeObject(((Texto) m).getConteudo());
						} else {
							String path = ((Fotografia) m).getConteudo().getPath();
							String[] fullPath = path.split("\\\\");
							String nomeSemPasta = fullPath[1];
							String extensao = path.substring(path.indexOf(".") + 1);
							outStream.writeObject("Foto");
							outStream.writeObject(nomeSemPasta);
							File localizacaoVerdadeira = new File(path);
							BufferedImage image = ImageIO.read(localizacaoVerdadeira);

							ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
							ImageIO.write(image, extensao, byteArrayOutputStream);
							byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
							output.write(size);
							output.write(byteArrayOutputStream.toByteArray());
						}
					}
					outStream.writeObject(":fim:");
					for (Mensagem m : mensagens) {
						m.verMensagem(user);
					}
					CatalogoGrupos.getInstance().getGrupo(g).refrescarHistorico();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
				break;

			case "history":
				try {
					String g = (String) inStream.readObject();
					List<Texto> mensagens = CatalogoGrupos.getInstance().getGrupo(g).getHistorico();
					outStream.writeObject(":inicio:");
					for (Texto m : mensagens) {
						outStream.writeObject(m.getRemetente());
						outStream.writeObject(m.getConteudo());
					}
					outStream.writeObject(":fim:");
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
				break;

			case "photo":
				try {
					String grupo = (String) inStream.readObject();

					String path = (String) inStream.readObject();					
					if (CatalogoGrupos.getInstance().getGrupo(grupo) == null){
						outStream.writeObject("-1");
						break;
					} else {
						outStream.writeObject("1");
					}
					input = socket.getInputStream();
					byte[] sizeAr = new byte[4];
					input.read(sizeAr);
					int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
					byte[] imageAr = new byte[size];
					input.read(imageAr);
					BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));

					File pasta = new File(grupo);
					if (!(pasta.exists() && pasta.isDirectory())) {
						pasta.mkdir();
					}
					File imagem = new File(grupo + "\\" + path);
					ImageIO.write(image, path.substring(path.indexOf(".") + 1), imagem);

					Boolean envia = CatalogoGrupos.getInstance().enviarFoto(grupo, imagem, user);
					outStream.writeObject("1");
				}catch(IOException | ClassNotFoundException i) {
					i.printStackTrace();
				}
				break;
			
			case "exit":
				try {
					outStream.close();
					inStream.close();
					output.close();
					input.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	private List<String> informacaoUser() {
		return CatalogoUtilizadores.getInstance().informacaoDoUtilizador(user);
	}

	private GInfoResponse informacaoGrupo(String g) {
		return CatalogoGrupos.getInstance().informacaoDoGrupo(g, user);
	}

	private int autentica(String u, String p) {
		return CatalogoUtilizadores.getInstance().autenticacao(u, p) ? 1 : -1;
	}


	private boolean createGroup(String name) {
		return CatalogoGrupos.getInstance().adicionarGrupo(name, user);
	}


	private boolean adicionarAGrupo(String grupo, String utilizador) {
		return CatalogoGrupos.getInstance().adicionarAoGrupo(grupo, user, utilizador);		
	}


	private boolean removerDoGrupo(String grupo, String utilizador) {
		return CatalogoGrupos.getInstance().removerDoGrupo(grupo, user, utilizador);		
	}
}