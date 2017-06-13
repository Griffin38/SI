/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sampleteam;


import java.awt.Color;
import java.util.*;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.Droid;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.MessageEvent;
import robocode.RobotDeathEvent;
import robocode.TeamRobot;
import robocode.TurnCompleteCondition;

/**
 *
 * @author gil
 */
public class Filho extends TeamRobot implements Droid{
    
    public double lastX = 0.0;
    public double lastY = 0.0;
    private List<String> nomeInimigos;
    boolean movingForward;
    boolean go;
    
    @Override
    public void run() {
        
        this.nomeInimigos=new ArrayList<>();
    
    	lastX = getX();
    	lastY = getY();
	
        setColors(Color.GREEN,Color.GREEN,Color.GREEN); // body,gun,radar
	
        while(true) {
          go =false;
       
            while(this.nomeInimigos.size()>1) 
                goTo();
       
            System.out.println(this.nomeInimigos.size());
            go=false;
           
            setColors(Color.GREEN,Color.GREEN,Color.GREEN);
            setAhead(40000);
            movingForward = true;
            setTurnRight(90);
            waitFor(new TurnCompleteCondition(this));
            // Note:  We are still moving ahead now, but the turn is complete.
            // Now we'll turn the other way...
            setTurnLeft(180);
            // ... and wait for the turn to finish ...
            waitFor(new TurnCompleteCondition(this));
            // ... then the other way ...
            setTurnRight(180);
            // .. and wait for that turn to finish.
            waitFor(new TurnCompleteCondition(this));
           
        }
       
       
       
       
    }
    
    
    
    
    @Override
        public void onHitByBullet(HitByBulletEvent e) {
		turnRight(30);
		ahead(90);
	}
    
     @Override
   public void onHitWall(HitWallEvent e) {
		// Bounce off!
                if(go==false) { 
		turnRight(90);
		   ahead(200);
                }
	}

	/**
	 * reverseDirection:  Switch from ahead to back & vice versa
	 */
	public void reverseDirection() {
		if (movingForward) {
			setBack(40000);
			movingForward = false;
		} else {
			setAhead(40000);
			movingForward = true;
		}
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
		   turnRight(100);
		   ahead(20);
	   }
           
           
           else {
            turnRight(90);
		   ahead(200);
               
           }
           
           }
    
    
        
    
    
    
    private void goTo(){
		// go to 0,0
                setColors(Color.RED,Color.RED,Color.RED);
                
		while(getX() != 18 || getY() != 18 ){
                    if(this.nomeInimigos.size()<=1)
                        break;
                    else {  
                    go=true;
			turnLeft(getHeading());
			double alpha = Math.toDegrees(Math.atan(getX()/getY()));
			turnLeft(180 - alpha);
			ahead(10000);
			
			if(alpha > 45) {
				ahead(lastX);
			}
			else {
				ahead(lastY);
			}
		}
                }
	}
    
    
     @Override
	public void onMessageReceived(MessageEvent e) {
            if(isTeammate(e.getSender())){
		
			String r = (String) e.getMessage();
                        if(this.nomeInimigos.contains(r)==false)
                            this.nomeInimigos.add(r);
                        
                              
               }
            
	}
    
    
    
        @Override
    public void onRobotDeath(RobotDeathEvent e) {
    
        
        if(this.nomeInimigos.contains(e.getName()))
            this.nomeInimigos.remove(e.getName());
        
        //System.out.println(this.nomeInimigos.size());
    
    }
    
    
    
}
