package src;

import cartas.GameTexasHoldem;
import cartas.*;
import cartas.IPlayer;
import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.*;


public class Main {
	Runtime rt;
	ContainerController container;
        
        
        
	public void initMainContainer(String host, String port) {
	this.rt = Runtime.instance();
	Profile prof = new ProfileImpl();
	prof.setParameter(Profile.MAIN_HOST, host);
	prof.setParameter(Profile.MAIN_PORT, port);
	prof.setParameter(Profile.MAIN, "true");
	prof.setParameter(Profile.GUI, "true");
	this.container = rt.createMainContainer(prof);
	rt.setCloseVM(true);
	}
	public static void main(String[] args) {
		/*
		Main mc = new Main();
		mc.initMainContainer("127.0.0.1", "1099");
		mc.startAgentInPlatform("Dealer", "Dealer");
		//input quantos jogadores
		int x = 3;
		for(int i= 0;i<x; i++){
			mc.startAgentInPlatform("Jogador "+ i, "Player");
		}
         *******/
                GameTexasHoldem game = new GameTexasHoldem();
                IPlayer p = (IPlayer) new Player();
                IPlayer p1 = (IPlayer) new Player();
                IDeck d = (IDeck) new Deck();
                
                game.newGame(d,p,p1);
              
                

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
