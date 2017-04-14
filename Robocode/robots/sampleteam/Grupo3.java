/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sampleteam;

import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import java.awt.Color;
import java.util.*;
import javafx.scene.shape.Circle;

/**
 *
 * @author gil
 */
public class Grupo3 extends TeamRobot
{      
       
	private Map<String, String> posicaoAliados;
        //private Map<String,String> posicaoEnimigos;
        private final String SCOUT ="Scout";
       
       
	
	
        @Override
	public void run() {
		setColors(Color.BLACK,Color.BLUE,Color.GREEN); // body,gun,radar
		setBulletColor(Color.YELLOW);
		posicaoAliados = new HashMap<>();
                //posicaoEnimigos=new HashMap<>();
		

		// Robot main loop
		while(true) {
			//ahead(100);
        	//turnRight(90);
                    
			turnGunLeft(360);
			
			
			System.out.println("-------- BattleField Info ---------");
			
			
                        System.out.println( " número de aliados "+this.posicaoAliados.size());
                        
		
			System.out.println("> My position: " + (int)getX() + "," + (int)getY());
			
			
			System.out.println("------------------------------------");
			
			
		}
	}

	public void smartFire(double robotDistance) {
		if (robotDistance > 200 || getEnergy() < 15) {
			fire(2);
		} else if (robotDistance > 50) {
			fire(2);
		} else {
			fire(3);
		}
	}

        @Override
	public void onScannedRobot(ScannedRobotEvent e) {
		if(isTeammate(e.getName()) ==false){
		
                    
			double enemyBearing = this.getHeading() + e.getBearing();
			
			int enemyX = (int) (getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing)));
			int enemyY = (int) (getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing)));

                        //this.posicaoEnimigos.put(e.getName(),new String((int)enemyX+","+(int)enemyY));
                  
                double absoluteBearing = getHeading() + e.getBearing();
		double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
                
		if (Math.abs(bearingFromGun) <= 3) {
			turnGunRight(bearingFromGun);

			if (getGunHeat() == 0 && safeToShoot(enemyX,enemyY)) {
				smartFire(Math.min(3 - Math.abs(bearingFromGun), getEnergy() - .1));
				ahead(40);
			}
                        
                        else {
                                 turnRight(120);
                                ahead(200);
                        
                        }
		}
		else {
			
                    turnGunRight(bearingFromGun);
		}
		if (bearingFromGun == 0) {
			scan();
		
                    }
                
                }
	}

        @Override
	public void onHitByBullet(HitByBulletEvent e) {
		turnRight(30);
		ahead(90);
	}

        @Override
	public void onHitWall(HitWallEvent e) {
		turnRight(180);
		ahead(70);
	}

	public void OnBulletMissed(BulletMissedEvent e) {
		turnLeft(30);
		ahead(90);
	}
	
        @Override
	public void onMessageReceived(MessageEvent e) {
            if(isTeammate(e.getSender())){
		
			String r = (String) e.getMessage();
			String[] p = r.split("\\|");
                        
                       if(p[0].equals(this.getName())==false && isTeammate(p[0]))
                           posicaoAliados.put(p[0], p[1]);
               }
            
	}
	
        @Override
	public void onRobotDeath(RobotDeathEvent e) {
	 /*
            if(posicaoEnimigos.containsKey(e.getName())){
			posicaoEnimigos.remove(e.getName());
                        return ;
		}
            */
            if(e.getName().contains(SCOUT)) {
                
                posicaoAliados.clear();
                return ;
            }
            
            if(posicaoAliados.containsKey(e.getName()))
                posicaoAliados.remove(e.getName());
            
                
	}
	
        @Override
	public void onBulletHit(BulletHitEvent event) {
       if(isTeammate(event.getName())){
		   turnRight(120);
		   ahead(200);
	   }
   }
	
	public boolean safeToShoot(int enemyX,int enemyY){
		
		String[] c;
		Circle circulo = new Circle(enemyX, enemyY, 10);
                
                
                
		if(posicaoAliados.size() > 0){
                    
				for(String p : posicaoAliados.keySet()) {
                                    
					c = posicaoAliados.get(p).split(",");
					int x = Integer.parseInt(c[0]);
					int y = Integer.parseInt(c[1]);
                                        
                                        
                                        if(Math.sqrt(Math.pow(x-circulo.getCenterX(),2)+Math.pow(y-circulo.getCenterY(), 2)) <Math.pow(circulo.getRadius(),2)) {
                                         System.out.println("Não é seguro disparar ;)");
                                            return false;  
                                        }
                                }
                               
                       circulo = new Circle((int)getX(), (int) getY(), 10);
                       
                       for(String p : posicaoAliados.keySet()) {
					c = posicaoAliados.get(p).split(",");
					int x = Integer.parseInt(c[0]);
					int y = Integer.parseInt(c[1]);
                                        if(Math.sqrt(Math.pow(x-circulo.getCenterX(),2)+Math.pow(y-circulo.getCenterY(), 2)) < Math.pow(circulo.getRadius(),2)){
                                         System.out.println("Não é seguro disparar");
                                            return false;
                                        }
                                }
                            
			}

		return true;
	}

        
      
        
        
}