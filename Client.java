/**
 * @author : IRAGUHA VALENS 
 **/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Client extends WindowAdapter implements ActionListener {
	JFrame frameClient;
	
	JButton charger;
	JTextField monPortTF;
	
	JLabel listeClientL;
	DefaultListModel<String> modelClient;
	JList<String> listeClient;
	JScrollPane scrollPaneClient;
	
	JLabel listeMessageL;
	DefaultListModel<String> modelMessage;
	JList<String> listeMessage;
	JScrollPane scrollPaneMessage;
	
	JLabel messageL;
	JTextField messageTF;
	
	JLabel portClientL;
	JTextField portClientTF;
	
	JLabel siteClientL;
	JTextField siteClientTF;
	
	JButton recharger;
	JButton contacter;
	JButton rejouer;
	JButton terminer;
	
	JOptionPane dialogBox; 
	
	Socket socketWithServer; 
	
	String port, messageWithServer, address; 
	
	BufferedReader inPutFromServer;
	PrintWriter outPutToServer;
	
	String messageWithServerPrefix = "CODI";
	
	Client(String clientAddress,int clientPort, String serverAddress) throws IOException{ 
		frameClient = new JFrame("JEU TIC-TAC-TOE");
		JPanel panelClient = new JPanel();
		this.port = Integer.toString(clientPort);
		this.address = clientAddress;
		
		
		listeClientL = new JLabel("Liste des clients connectés : ");
		panelClient.add(listeClientL);
		listeClientL.setBounds(20,40,200,30);
		modelClient = new DefaultListModel<String>();
		listeClient = new JList<String>(modelClient);
		scrollPaneClient = new JScrollPane(listeClient);
		panelClient.add(scrollPaneClient);
		scrollPaneClient.setBounds(20,70,200,160);
		
		listeMessageL = new JLabel("Espace - Jeu : ");
		panelClient.add(listeMessageL);
		listeMessageL.setBounds(240,40,200,30); 
		
		GridLayout matriceLayout = new GridLayout(3,3);
		final JPanel matricePanel = new JPanel();
		matricePanel.setLayout(matriceLayout);
		
		JButton element00 = new JButton("");
		JButton element01 = new JButton("");
		JButton element02 = new JButton("");
		JButton element10 = new JButton("");
		JButton element11 = new JButton("");
		JButton element12 = new JButton("");
		JButton element20 = new JButton("");
		JButton element21 = new JButton("");
		JButton element22 = new JButton("");
		
		matricePanel.add(element00);
		matricePanel.add(element01);
		matricePanel.add(element02);
		matricePanel.add(element10);
		matricePanel.add(element11);
		matricePanel.add(element12);
		matricePanel.add(element20);
		matricePanel.add(element21);
		matricePanel.add(element22);
        
        panelClient.add(matricePanel);
        matricePanel.setBounds(240,70,260,160); 
        
        contacter = new JButton("Contacter");
		panelClient.add(contacter);
		contacter.setBounds(80,240,90,30);
		contacter.addActionListener(this);
		
		rejouer = new JButton("Rejouer");
		panelClient.add(rejouer);
		rejouer.setBounds(280,240,80,30);
		rejouer.addActionListener(this);
		
		terminer = new JButton("Terminer");
		panelClient.add(terminer);
		terminer.setBounds(370,240,80,30);
		terminer.addActionListener(this);
		
		frameClient.add(panelClient);
		panelClient.setLayout(null);
		frameClient.setSize(520,400);
		frameClient.setVisible(true);
		frameClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameClient.addWindowListener(this);
		
		this.messageWithServer = messageWithServerPrefix + " " + clientAddress + " " + clientPort; 
		
		
		String ServerName = serverAddress;
		int ServerPort = 1027;
		
		try { // Connecion et ouverture des « flux avancées »
			socketWithServer = new Socket(ServerName,ServerPort);
			outPutToServer = new PrintWriter(socketWithServer.getOutputStream(), true);
			inPutFromServer = new BufferedReader(new InputStreamReader(socketWithServer.getInputStream()));
		} 
		catch (UnknownHostException e) {
			System.err.println("Address of server can not be found!");
			//Boîte du message d'erreur
			dialogBox = new JOptionPane();
			dialogBox.showMessageDialog(null, "Message", "Erreur", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		} 
		catch (IOException e) { 
			dialogBox = new JOptionPane();
			dialogBox.showMessageDialog(null, "Message", "Erreur", JOptionPane.ERROR_MESSAGE);
			System.err.println("Pb a la connection/ouverture des flux");
			System.exit(1);
		}
		
		// le thread pour conserver les clients
		Thread threadClients = new Thread(new Clients(outPutToServer,modelClient,messageWithServer,inPutFromServer));
		threadClients.start();
		
	}
	
	public void actionPerformed(ActionEvent evenement){ 
		
		if(evenement.getSource() == contacter) { 
			try { 
				messageWithServerPrefix = "OCCU"; 
				this.messageWithServer = messageWithServerPrefix + " " + address + " " + port;
				outPutToServer.println(messageWithServer);
			}
			catch(Exception oe) { 
				dialogBox = new JOptionPane();
				dialogBox.showMessageDialog(null, oe, "Erreur", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if(evenement.getSource() == terminer) { 
			try { 
				messageWithServerPrefix = "DISP"; 
				this.messageWithServer = messageWithServerPrefix + " " + address + " " + port;
				outPutToServer.println(messageWithServer);
			}
			catch(Exception oe) { 
				dialogBox = new JOptionPane();
				dialogBox.showMessageDialog(null, oe, "Erreur", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	public void windowClosing(WindowEvent w){
		try { 
			messageWithServerPrefix = "QUIT"; 
			this.messageWithServer = messageWithServerPrefix + " " + address + " " + port;
			outPutToServer.println(messageWithServer);
			Thread.currentThread().sleep(1000);
			System.exit(1);
		}
		catch(Exception oe) { 
			dialogBox = new JOptionPane();
			dialogBox.showMessageDialog(null, oe, "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}	
}

//classe permettant de gerer la liste des clients connectés envoyés par le serveur
class Clients implements Runnable{
	PrintWriter outPutToServer;
	DefaultListModel<String> modelClient;
	BufferedReader inPutFromServer;
	
	String messageOut, clientInfo;
	ArrayList<String> listClient;
	
	int i=0;
	
	Clients(PrintWriter outPutToServer, DefaultListModel<String> modelClient, String messageWithServer,BufferedReader inPutFromServer){
		this.outPutToServer = outPutToServer;
		this.modelClient = modelClient;
		this.messageOut = messageWithServer;
		this.inPutFromServer = inPutFromServer;
	}
	public void run(){
		try {
				try { // Ecriture d'un message au serveur
					this.outPutToServer.println(messageOut);
				} 
				catch (Exception e) {
					System.err.println("erreur sur le write ");
					System.exit(1);
				}
				
				try { // Lecture de la reponse du serveur
					String messageIn = this.inPutFromServer.readLine();
					listClient = new ArrayList<String>(Arrays.asList(messageIn.split(" "))); 
					
					String clientConnectedHost = " ", clientConnectedPort = " ";
					
					Iterator<String> iterator = listClient.iterator();
					int cpt = 0;
					while(iterator.hasNext()) {  
						if(cpt == 1) 
							clientConnectedHost = (String)iterator.next();
						
						if(cpt == 2)
							clientConnectedPort = (String)iterator.next(); 
							
						cpt++;
					}
					clientInfo = clientConnectedHost + " : " + clientConnectedPort;
					modelClient.addElement(clientInfo);
				} catch (IOException e) {
					System.err.println("erreur reception ");
					System.exit(1);
				}
		}
		catch(Exception oe) { 
			
		}
	}
}
