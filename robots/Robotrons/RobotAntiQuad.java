package Robotrons;

import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashMap;

public class RobotAntiQuad extends  AdvancedRobot {

	int obs;
	int done = 0;
	boolean cornerB = true, hitBot = false,Started = false;
	static int corner = 270;
	HashMap<String, ScannedRobotEvent> obstaculos;
	private Point2D old_position; // Robot's last position
	private Point2D new_position; // New position
	private Double distancia; // distancia do percurso

	public void run() {

		
	
		// Set colors
		obstaculos = new HashMap<>();
		setBodyColor(new Color(221, 175, 19));
		setGunColor(new Color(11, 77, 113));
		setRadarColor(new Color(99, 228, 199));
		setBulletColor(new Color(255, 238, 0));
		setScanColor(new Color(255, 241, 46));

		goCorner();
		FindObstacles();

		// iniciar scan /contagem

	}
//vai para o canto
	public void goCorner() {
		// We don't want to stop when we're just turning...
		while (cornerB) {
			// turn to face the wall to the "right" of our desired corner.
			turnRight(normalRelativeAngleDegrees(corner - getHeading()));
			// Move to that wall
			ahead(5000);
			// Turn to face the corner
			turnLeft(90);
			// Move to the corner
			ahead(5000);
			// Se bateu nao esta no canto verdadeiro e faz de novo
			if (hitBot == false) {
				cornerB = false;
			}
			hitBot = false;
		}
	}
//procura inicial
	public void FindObstacles() {
		turnRight(360 - getHeading());
		obs = 0;
		System.out.println("*******************ENCONTRAR ***************");
		while (obs < 3) {
			turnRadarRight(360);
		}
		addCustomEvent(new MoveCompleteCondition(this));
		done = obs;
		Rotunda();

	}
	//procura os robots
	public void FindNext() {
	
		if(done >0 ) {
			Started = true;
		obs = 0;
		System.out.println("*******************Reprocurar ***************");
		while (obs < 3) {
			turnRadarRight(360);
		}
		Started = false;
		Rotunda();
		
		}//else volta para o 0 0
	}
//ver se ateu enquanto ia para o canto
	public void onHitRobot(HitRobotEvent e) {
		if (cornerB)
			hitBot = true;

	}

	//scanned robot se ja estiver a percorrer comportasse diferente
	public void onScannedRobot(ScannedRobotEvent e) {
	
		if(Started == false && obs < 3){
			
		obstaculos.put(e.getName(), e);
		}
		else if( obs < 3){
			if(obstaculos.containsKey(e.getName())){
				obstaculos.put(e.getName(), e);
				System.out.println("susbst");
			}
			
		}
		obs++;
	}

	
	//vai ate um robot e contorna-o
	public void vaiAte(ScannedRobotEvent ob) {
		
		//Vai ate ao robot 
		setTurnRight(ob.getBearing());
		setAhead(ob.getDistance()- 100);
		
		waitFor(new MoveCompleteCondition(this));
		
		//dalhe a vlta?
		setTurnLeft(45);;
		setAhead(80);
		waitFor(new MoveCompleteCondition(this));
		setTurnRight(45);
		setAhead(50);
		waitFor(new MoveCompleteCondition(this));
		
		//vai para o proximo
		FindNext();

	}

	//Contar a distancia so quando vai andar a volta dos robots
	public void onCustomEvent(CustomEvent e) {
		
		
		if (cornerB == false) {
		new_position = new Point2D.Double(getX(), getY());
		double euclidian = Math.sqrt(Math.pow((old_position.getX() - new_position.getX()), 2) + Math.pow((old_position.getY() - new_position.getY()), 2));
        distancia += euclidian;
        old_position = new Point2D.Double(new_position.getX(), new_position.getY());
       // System.out.println("Percorrido: "+ distancia);
		}
	}
	
	
	//escolhe o prox robot para onde ir 
	public void Rotunda() {
		
		old_position = new Point2D.Double(getX(), getY());
	    distancia = 0.0;
		String nome = new String();
		double aux = getBattleFieldWidth();
		
		for (String a : obstaculos.keySet()) {
			ScannedRobotEvent px = obstaculos.get(a);
			if(px.getDistance() < aux ){
				nome = a;
				aux = px.getDistance();
			}
				System.out.println("E o : " + a);
		}
		ScannedRobotEvent ob1 = obstaculos.get(nome);
		obstaculos.remove(nome);
		done--;
		vaiAte(ob1);
		


	}
	
	



}
