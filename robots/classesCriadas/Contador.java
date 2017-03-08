/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classesCriadas;

/**
 *
 * @author gil
 */
public class Contador {
    private double distanciaJogo;
    private double distanciaJogoX;
    private double distanciaJogoY;
    private static double distanciaPercorrida=0;
    
    
    
    public Contador() {
    
        this.distanciaJogo=-1;
        
    }
    
    public void incrementaDistancia(double distX,double distY) {
    
        if(this.distanciaJogo==-1) {
           this.distanciaJogoX=distX;
           this.distanciaJogoY=distY;
           this.distanciaJogo=0;
        }
        else {
            
       this.distanciaJogo+= Math.sqrt(Math.pow(this.distanciaJogoX-distX,2)+ Math.pow(this.distanciaJogoY-distY,2));
       this.distanciaJogoX=distX;
       this.distanciaJogoY=distY;
        
        }
    
    }
    
    public double retornaDistancia() {
    
         return this.distanciaJogo;
    
    }
    
    public void somaDistancia() {
    
        Contador.distanciaPercorrida+=this.distanciaJogo;
        
    
    }
  
    
     public double distanciaTotal() {
    
        return Contador.distanciaPercorrida;
        
    
    }
    
    
    
    
    
    
    
    
}
