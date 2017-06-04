package exercito;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Color;
import java.io.IOException;
import java.util.*;
import javafx.scene.shape.Circle;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;
import static robocode.util.Utils.normalRelativeAngleDegrees;

/**
 *
 * @author gil
 */
public class General extends TeamRobot {
    
    private Map<String,String> posicao;
    private final String FILHO ="Filho";
    public void run() {
    	
		setColors(Color.BLACK,Color.BLACK,Color.BLACK); // body,gun,radar
		setBulletColor(Color.WHITE);
                this.posicao= new HashMap<>();
                
                while(true) {
		
                    
			turnGunLeft(360);
			
			
			System.out.println("-------- BattleField Info ---------");
			
			
                        System.out.println( " número de aliados "+this.posicao.size());
                        
		
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
        public void onHitByBullet(HitByBulletEvent e) {
    	//ver vida do robot 
    	//se tiver pouca pedir ajuda
    	if(this.getEnergy() < 100){
  	  try {
			broadcastMessage("Ajuda:"+getX()+":"+getY());
		} catch (IOException ex) {
			ex.printStackTrace();
		}}
		turnRight(30);
		ahead(90);
	}
        
        
   @Override
    public void onScannedRobot(ScannedRobotEvent e) {
	 
        double aliadoBearing = this.getHeading() + e.getBearing();
	int  aliadoX = (int)( getX() + e.getDistance() * Math.sin(Math.toRadians(aliadoBearing)));
        int aliadoY = (int) (getY() + e.getDistance() * Math.cos(Math.toRadians(aliadoBearing)));

        if(isTeammate(e.getName()) ==false){
		
                    try {
                               
                        broadcastMessage("NE:"+e.getName());
                        } catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
                        

                double absoluteBearing = getHeading() + e.getBearing();
		double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
                
		if (Math.abs(bearingFromGun) <= 3) {
			turnGunRight(bearingFromGun);

			if (getGunHeat() == 0 && safeToShoot(aliadoX,aliadoY)) {
				try {
					broadcastMessage("Atacar:"+aliadoX+":"+aliadoY);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
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
        
        else {
        
               this.posicao.put(FILHO,new String((int)aliadoX+","+(int)aliadoY));
               turnRight(90);
               ahead(200);
            
        }
        
       
        
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
          public void onBulletHit(BulletHitEvent event) {
       
              if(isTeammate(event.getName())){
		   turnRight(120);
		   ahead(200);
	   }
          }
          
    
@Override
public void onHitRobot(HitRobotEvent event) {
           
           if(isTeammate(event.getName())){
		   turnRight(90);
		   ahead(20);
	   }
           
}  
      
    
    
    public boolean safeToShoot(int enemyX,int enemyY){
    
        String[] c;
	Circle circulo = new Circle(enemyX, enemyY, 10);
        
        if(this.posicao.isEmpty()==false) {
        
            c = posicao.get(FILHO).split(",");
            int x = Integer.parseInt(c[0]);
            int y = Integer.parseInt(c[1]);


            if(Math.sqrt(Math.pow(x-circulo.getCenterX(),2)+Math.pow(y-circulo.getCenterY(), 2)) <Math.pow(circulo.getRadius(),2)) {
             System.out.println("Não é seguro disparar ;)");
                return false;  
            }
            
             circulo = new Circle((int)getX(), (int) getY(), 10);
             
             c = posicao.get(FILHO).split(",");
                x = Integer.parseInt(c[0]);
                y = Integer.parseInt(c[1]);
               if(Math.sqrt(Math.pow(x-circulo.getCenterX(),2)+Math.pow(y-circulo.getCenterY(), 2)) < Math.pow(circulo.getRadius(),2)){
                System.out.println("Não é seguro disparar");
                   return false;
               }
            
        }
       return true;
    
    }
    
    
    @Override
    public void onRobotDeath(RobotDeathEvent e) {
    
        if(isTeammate(e.getName())){
            this.posicao.clear();
      	  try {
				broadcastMessage("Aliado Morto");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
        }else {
        	 try {
 				broadcastMessage("Enemigo Morto");
 			} catch (IOException ex) {
 				ex.printStackTrace();
 			}
        }
        
    
    }
        
        
        
    
    
    
}
