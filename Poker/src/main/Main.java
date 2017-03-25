package main;


import cartas.GameTexasHoldem;
import cartas.*;
import cartas.IPlayer;
import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.*;


public class Main {
	private Runtime rt;
	public ContainerController container;
	protected Inicio myGui;    
        
        
	public void initMainContainer(String host, String port) {
	this.rt = Runtime.instance();
	Profile prof = new ProfileImpl();
	prof.setParameter(Profile.MAIN_HOST, host);
	prof.setParameter(Profile.MAIN_PORT, port);
	prof.setParameter(Profile.MAIN, "true");
	prof.setParameter(Profile.GUI, "true");
    prof.setParameter(Profile.CONTAINER_NAME, "Tester");
	this.container = rt.createMainContainer(prof);
	rt.setCloseVM(true);
	}
	public static void main(String[] args) {
		
		Main mc = new Main();
		mc.initMainContainer("127.0.0.1", "1080");
		mc.startAgentInPlatform("Dealer", "Dealer");
		mc.myGui = new Inicio(mc.container);
	    mc.myGui.setVisible(true);
		/*
		//input quantos jogadores
		int x = 3;
		for(int i= 0;i<x; i++){
			mc.startAgentInPlatform("Jogador "+ i, "Player");
		}
         *******/
                
            
              
                

	}
	
	public void startAgentInPlatform(String name, String classpath){
		try {
		AgentController ac = container.createNewAgent(
		name,
		classpath,
		new Object[0]);
		ac.start();
		} catch (Exception e) {
		e.printStackTrace();
		}
		}
}
