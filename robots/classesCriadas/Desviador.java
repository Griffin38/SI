/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classesCriadas;

import java.awt.Color;
import robocode.*;


/**
 *
 * @author gil
 */
public class Desviador {

    
    public static void anda(Robot r) {
        double lastX=r.getX();
        double lastY=r.getY();
        double distancia= Math.sqrt(Math.pow(lastX-0,2)+ Math.pow(lastY-0,2));
        while(r.getX() != 0 || r.getY() != 0){
			r.turnLeft(r.getHeading());
			double alpha = Math.toDegrees(Math.atan(r.getX()/r.getY()));
			r.turnLeft(180 - alpha);
			r.ahead(distancia);
			
			
		}
        
        
        
        
    
    }
    
    
    
    
    
    
    
    
    
    
    
    
	
}
