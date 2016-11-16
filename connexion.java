/**
 * @author : IRAGUHA VALENS 
 **/

import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class Connexion implements ActionListener {
	JFrame frame;
	JLabel pseudoL;
	JTextField pseudoTF;
	JLabel portL;
	JTextField portTF;
	JLabel serverL;
	JTextField serverTF;
	
	JButton connexion;
	
	public static void main(String[] args) { 
		new Connexion();
	}
	
	public Connexion() {
		frame = new JFrame("JEU TIC-TAC-TOE");
		pseudoL = new JLabel("Adresse client : ");
		pseudoTF = new JTextField();
		portL = new JLabel("Port client : ");
		portTF = new JTextField();
		serverL = new JLabel("Adresse serveur : ");
		serverTF = new JTextField();
		connexion = new JButton("Envoyer");
		
		JPanel panel = new JPanel();
		
		panel.add(pseudoL);
		panel.add(pseudoTF);
		panel.add(portL);
		panel.add(portTF);
		panel.add(serverL);
		panel.add(serverTF);
		panel.add(connexion);
		
		pseudoL.setBounds(40,40,120,30);
		pseudoTF.setBounds(180,40,160,30);
		portL.setBounds(40,80,120,30);
		portTF.setBounds(180,80,160,30);
		serverL.setBounds(40,120,120,30);
		serverTF.setBounds(180,120,160,30);
		connexion.setBounds(220,160,80,30);
		
		frame.add(panel);
		panel.setLayout(null);
		frame.setSize(400,250);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		connexion.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) { 
		String clientAddress = "";
		String serverAddress = "";
		
		try { 
			clientAddress = pseudoTF.getText();
			
			String clientPortStr = portTF.getText();
			int clientPort = Integer.parseInt(clientPortStr);
			
			serverAddress = serverTF.getText();
			
			new Client(clientAddress,clientPort,serverAddress);
		}
		catch(IOException exc) { 
			System.out.println("Exception : " +exc);
		}
	}

}
