/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sampleteam;


import robocode.*;
import java.awt.Color;
import java.io.IOException;

/**
 *
 * @author gil
 */
public class Scout extends TeamRobot {
   boolean movingForward;
    
    @Override
	public void run() {
		setColors(Color.BLUE,Color.PINK,Color.darkGray); // body,gun,radar
		setBulletColor(Color.GREEN);
                
		
        
       try {
           broadcastMessage(new String(this.getName()+ "|" +(int)getX()+","+(int)getY()));
       } catch (IOException ex) {
           System.out.println(ex.getMessage());
       }

                

		// Robot main loop
		while(true) {
                   
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
                        
                         
			
			System.out.println("-------- BattleField Info ---------");
			
		
                        
		
                        
			System.out.println("> My position: " + (int)getX() + "," + (int)getY());
			
                        
			
			System.out.println("------------------------------------");
			
			
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
		reverseDirection();
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
    public void onScannedRobot(ScannedRobotEvent e) {
		if(isTeammate(e.getName())){
		
                
                
                    try {
                        double aliadoBearing = this.getHeading() + e.getBearing();
			
			double aliadoX = getX() + e.getDistance() * Math.sin(Math.toRadians(aliadoBearing));
			double aliadoY = getY() + e.getDistance() * Math.cos(Math.toRadians(aliadoBearing));
                        
                            
                               broadcastMessage(new String(e.getName()+ "|" +(int)aliadoX+","+(int)aliadoY));
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
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
		   turnRight(90);
		   ahead(20);
	   }
           
           
           else {
            turnRight(180);
		   ahead(200);
               
           }
           
           } 
       
                
                
                
    
}
    
    
