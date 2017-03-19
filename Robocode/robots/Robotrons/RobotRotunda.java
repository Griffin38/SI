package Robotrons;

import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashMap;

public class RobotRotunda extends AdvancedRobot {

	int obs;
	boolean cornerB = true, hitBot = false;
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

	public void FindObstacles() {
		turnRight(360 - getHeading());
		obs = 0;
		System.out.println("*******************ENCONTRAR ***************");
		while (obs < 3) {
			turnRadarRight(360);
		}
		
		Rotunda();

	}

	public void onHitRobot(HitRobotEvent e) {
		if (cornerB)
			hitBot = true;

	}

	public void onScannedRobot(ScannedRobotEvent e) {
		obs++;
		obstaculos.put(e.getName(), e);

	}

	public void vaiAte(ScannedRobotEvent ob) {
		
		//setTurnRight(normalRelativeAngleDegrees(ob.getBearing() + 90));
		setTurnRight(ob.getBearing());
		setAhead(ob.getDistance()- 100);
		
		waitFor(new MoveCompleteCondition(this));
		setTurnLeft(45);;
		setAhead(80);
		waitFor(new MoveCompleteCondition(this));
		setTurnRight(45);
		//setAhead(50);
		waitFor(new MoveCompleteCondition(this));
		// calcular nova distancia
		// adicionar percorrido
	}

	public void onCustomEvent(CustomEvent e) {
		
		
		if (cornerB == false) {
		new_position = new Point2D.Double(getX(), getY());
		double euclidian = Math.sqrt(Math.pow((old_position.getX() - new_position.getX()), 2) + Math.pow((old_position.getY() - new_position.getY()), 2));
        distancia += euclidian;
        old_position = new Point2D.Double(new_position.getX(), new_position.getY());
        System.out.println("Percorrido: "+ distancia);
		}
	}
	
	public void Rotunda() {
		addCustomEvent(new MoveCompleteCondition(this));
		old_position = new Point2D.Double(getX(), getY());
	    distancia = 0.0;
		String nome = new String();
		double aux = getBattleFieldWidth();
		
		for (String a : obstaculos.keySet()) {
			ScannedRobotEvent px = obstaculos.get(a);
			// Calculate angle
			double angle = Math.toRadians((getHeading() + px.getBearing()) % 360);
			// Calculate the coordinates of the robot
			double scannedX = (getX() + Math.sin(angle) * px.getDistance());

			if (scannedX < aux) {
				nome = a;
				aux = scannedX;
			}
		}
		ScannedRobotEvent ob1 = obstaculos.get(nome);
		vaiAte(ob1);
		obstaculos.remove(nome);

		// Vai para o y maior
		System.out.println("vou para o : " + nome);

		for (String a : obstaculos.keySet()) {
			ScannedRobotEvent px = obstaculos.get(a);
			// Calculate angle
			double angle = Math.toRadians((getHeading() + px.getBearing()) % 360);
			// Calculate the coordinates of the robot
			int scannedY = (int) (getY() + Math.cos(angle) * px.getDistance());

			if (scannedY > aux) {
				nome = a;
				aux = scannedY;
			}
		}
		System.out.println("vou para o : " + nome);
		ScannedRobotEvent ob2 = obstaculos.get(nome);
		//vaiAte(ob2);

	}
	
	



}
