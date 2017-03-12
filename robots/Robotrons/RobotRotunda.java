package Robotrons;
import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.Color;
public class RobotRotunda extends AdvancedRobot {
	 int obs ;
	 boolean cornerB = true,hitBot = false;
	 static int corner = 270;
	
	
	   public  void run() {
		// Set colors
			setBodyColor(new Color(221, 175, 19));
			setGunColor(new Color(11,77,113));
			setRadarColor(new Color(99,228,199));
			setBulletColor(new Color(255,238,0));
			setScanColor(new Color(255,241,46));
		
			goCorner();
	        FindObstacles();
	      
	        //iniciar scan /contagem
	        
	    
	    }
	   
	   public void goCorner() {
           // We don't want to stop when we're just turning...
       while(cornerB )   {  
           // turn to face the wall to the "right" of our desired corner.
           turnRight(normalRelativeAngleDegrees(corner - getHeading()));
           // Ok, now we don't want to crash into any robot in our way...
           
           // Move to that wall
           ahead(5000);
           // Turn to face the corner
           turnLeft(90);
           // Move to the corner
           ahead(5000);
           System.out.println("x:"+ getX() + "y:"+getY());
           if(hitBot == false ) {cornerB = false; }
       } 
     }
	   
	   public void FindObstacles(){
		   turnRight(360 -getHeading());
		   obs = 0 ;
		   
		   while(obs<3){
			   setTurnRadarRight(360); 
		   }
		   
		   
		   
	   }
		public void onHitRobot(HitRobotEvent e) {
		if(cornerB) hitBot = true;
			
		}
		
		public void onScannedRobot(ScannedRobotEvent e) {
			obs++;
			 smartFire(e.getDistance());
		}
		 public void smartFire(double robotDistance) {
	            if (robotDistance > 200 || getEnergy() < 15) {
	                  fire(1);
	            } else if (robotDistance > 50) {
	                  fire(2);
	            } else {
	                  fire(3);
	            }
	      }
}
