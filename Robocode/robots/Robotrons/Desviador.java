/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Robotrons;

import java.awt.Color;
import robocode.*;


/**
 *
 * @author gil
 */
public class Desviador extends AdvancedRobot{

    
    public  void run() {
    	
        double lastX=getX();
        double lastY=getY();
        double distancia= Math.sqrt(Math.pow(lastX-0,2)+ Math.pow(lastY-0,2));
        
        while(getX() != 0 || getY() != 0){
        	
        	if(getX() == 0){
        		
        	}else if (getY() == 0){
        		
        	}
        	else{
			turnLeft(getHeading());
			double alpha = Math.toDegrees(Math.atan(getX()/getY()));
			turnLeft(180 - alpha);
			ahead(distancia);
        	}
			
		}
        
        
        
        
    
    }
    
    
    
    
    
    
    
    
    
    
    
    
	
}
