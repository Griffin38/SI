package Robotrons;

import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashMap;

public class RobotAntiQuad extends  AdvancedRobot {

	int obs;
	
	
	private boolean  done = false,Started = true;
	private HashMap<String, Event> obstaculos;
	private Point2D old_position; // Robot's last position
	private Point2D new_position; // New position
	private double distanciaP = 0.0; // distancia do percurso
	public double angulo;
	public void run() {

		
		old_position  = new Point((int) getX(),(int)getY());
    	
		// Set colors
		obstaculos = new HashMap<>();
		setBodyColor(new Color(221, 175, 19));
		setGunColor(new Color(11, 77, 113));
		setRadarColor(new Color(99, 228, 199));
		setBulletColor(new Color(255, 238, 0));
		setScanColor(new Color(255, 241, 46));
		
		goCorner();
		addCustomEvent(new MoveCompleteCondition(this));
		Started = false;
		distanciaP = 0.0;
		FindObstacles();
		
		setTurnRight(50);
		ahead(100);
		execute();
		goCorner();
		done = true;
		// iniciar scan /contagem
		System.out.println("Percorrido: "+ distanciaP);
	}
//vai para o canto
	public void goCorner() {
		// go to 0,0
		while(getX() != 18 || getY() != 18){
			turnLeft(getHeading());
			double alpha = Math.toDegrees(Math.atan(getX()/getY()));
			turnLeft(180 - alpha);
			ahead(10000);
			
			if(alpha > 45) {
				ahead(old_position.getX());
			}
			else {
				ahead(old_position.getY());
			}
		}
	}
	
	public void ProcuraBot(){
		boolean doneS = false;
		while(!doneS){
			if(!Started){	
				while(!Started){
					turnRight(2);
				}
			} else {
				Started = false;
				doneS = true;
			}
		}
	}
//procura 
	public void FindObstacles() {
		turnRight(360 - getHeading());
		obs = 0;
		System.out.println("*******************ENCONTRAR ***************");
		while (obs < getOthers()) {
			ProcuraBot();
			obs++;
		}
		
	
		

	}
	
//bateu
	public void onHitRobot(HitRobotEvent e) {
		back(10);
        turnLeft(90);
		ahead(30);
		turnRight(90);
		ahead(30);

	}
	
	//paredes
	public void onHitWall(HitWallEvent event) {
	   	double ang = event.getBearing();
		if(obstaculos.keySet().size() == 0){
	   		if(ang > 0){
				turnRight(ang);
			   	turnLeft(90);
	   		}
		   	else {
			   	turnLeft(-ang);
				turnRight(90);
		   	}
		}
		
		if(obstaculos.keySet().size() == getOthers()){
			if(ang > 0){
				turnRight(ang);
			   	turnLeft(90);
	   		}
		   	else {
			   	turnLeft(-ang);
				turnRight(90);
		   	}
			obstaculos.put("Last",event);
			ahead(10000);
		}
   	}
	
	//scanned robot 
	public void onScannedRobot(ScannedRobotEvent e) {
	
			
		
		if(!Started && !(obstaculos.containsKey(e.getName()))){		
			Started = true;	
			
			
			if(obstaculos.size()>=1){
				
				//voltar a posicao
				double h = getHeading() - angulo;
				turnLeft(h);
				//rodar 
				double total = 300/(360/h);
			
				setTurnRight(h-7);
				ahead(total);
				execute();
				
				double distancia = Math.sqrt(Math.pow(60,2) + Math.pow(e.getDistance(),2));
				ahead(distancia-total/4);
			}
			else {
				
				
				double distancia = Math.sqrt(Math.pow(60,2) + Math.pow(e.getDistance(),2));
				angulo = Math.toDegrees(Math.atan(60/e.getDistance()));
				
				turnLeft(angulo);
				ahead(distancia + 30);
			}
			
			obstaculos.put(e.getName(),e);
			//Guardar o angulo para onde esta virado
			angulo = getHeading();
		}
		
		if(done){
			Started = true;
			
			/*******************************************************************/
	
	}
	}
	
	
	//Contar a distancia so quando vai andar a volta dos robots
	public void onCustomEvent(CustomEvent e) {
		
		
		
		new_position = new Point2D.Double(getX(), getY());
		double euclidian = Math.sqrt(Math.pow((old_position.getX() - new_position.getX()), 2) + Math.pow((old_position.getY() - new_position.getY()), 2));
        distanciaP += euclidian;
        old_position = new Point2D.Double(new_position.getX(), new_position.getY());
       
		
	}
	
	
	
	



}
