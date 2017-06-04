/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



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
public class Soldado extends TeamRobot {
    
    public double lastX = 0.0;
    public double lastY = 0.0;
    private List<String> nomeEnimigos;
    boolean movingForward;
    boolean go;
    int courage;
    int personality;
    private int minperso;
    @Override
    public void run() {
    	
        this.nomeEnimigos=new ArrayList<>();
        /****************************************************************************************/
        Random r = new Random();
        //couragem base 
        this.courage =  r.nextInt(100) + 40;
        
        //personalidades
        this.personality = r.nextInt(3) + 1;
        switch(this.personality){
        //Medricas
        case 1: setGunColor(Color.green);
        	this.personality = 15;
        		this.minperso = 5;
        		break;
        //Normal
        case 2:setGunColor(Color.white);
        	this.personality = 10;
        		this.minperso = 3;
        		break;
        //Heroi
        case 3 :setGunColor(Color.orange);
        	this.minperso = 1;
        		break;
        }
        /****************************************************************************************/
        
    	lastX = getX();
    	lastY = getY();
	
    	setBodyColor(Color.green);
	
        while(true) {
          go =false;
       
            while(this.nomeEnimigos.size()>1) 
                goTo();
       
            System.out.println(this.nomeEnimigos.size());
            go=false;
           
            
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
		 this.courage = this.courage - this.personality;
	}
    
     @Override
   public void onHitWall(HitWallEvent e) {
		// Bounce off!
    	 this.courage = this.courage - this.minperso;
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

	public void OnBulletMissed(BulletMissedEvent e) {
		turnLeft(30);
		ahead(90);
		 this.courage = this.courage - this.minperso;
	}
                              
   @Override
          public void onBulletHit(BulletHitEvent event) {
       
              if(isTeammate(event.getName())){
		   turnRight(120);
		   ahead(200);
		   this.courage = this.courage - this.personality;
	   }else {
    	   this.courage = this.courage + this.personality;
        turnRight(90);
	   ahead(200);
           
       }
          }
    
    
    
    
     @Override
    public void onHitRobot(HitRobotEvent event) {
           
           if(isTeammate(event.getName())){
		   turnRight(100);
		   ahead(20);
		   this.courage = this.courage - this.minperso;
	   }
           
           
           else {
        	   this.courage = this.courage + this.minperso;
            turnRight(90);
		   ahead(200);
               
           }
           
           }
    
    
        
    
    private void colorCheck(){
    	
    	if(this.courage < 60 && this.courage > 35){
    		setBodyColor(Color.yellow) ;

    	}else if(this.courage <=35){
    		setBodyColor(Color.red) ;
    	}else {setBodyColor(Color.green) ;}
    }
    
    private void goTo(){
		// go to 0,0
    	this.colorCheck();
                
		while(getX() != 18 || getY() != 18 ){
                    if(this.nomeEnimigos.size()<=1)
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
			String[] parts = r.split(":");
			switch(parts[0]){
			case "Atacar":  
				break;
			case "Ajuda":
				break;
			case "NE": this.nomeEnimigos.add(parts[2]);
				break;
			case "Aliado Morto":
					this.courage = this.courage - this.personality;
					break;
			case "Enemigo Morto":
				this.courage = this.courage + this.personality + 10;
					break;
			}
				
			
	
                        
                              
               }
            
	}
    
    
    
        @Override
    public void onRobotDeath(RobotDeathEvent e) {
    
        
        if(this.nomeEnimigos.contains(e.getName()))
            this.nomeEnimigos.remove(e.getName());
        
        System.out.println(this.nomeEnimigos.size());
    
    }
    
    
    
}
