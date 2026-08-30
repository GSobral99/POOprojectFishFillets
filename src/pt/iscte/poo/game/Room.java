package pt.iscte.poo.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import objects.Water;
import objects.BigFish;
import objects.GameObject;
import objects.SmallFish;
import objects.*;
import pt.iscte.poo.utils.Point2D;

public class Room {

	// Every room is a fixed 10x10 grid; GameCharacter also relies on this
	// value to detect when a fish has swum off the edge of the room.
	public static final int GRID_SIZE = 10;

	private List<GameObject> objects;
	private String roomName;
	private GameEngine engine;
	private Point2D smallFishStartingPosition;
	private Point2D bigFishStartingPosition;

	public Room() {
		objects = new ArrayList<GameObject>();
	}

	private void setName(String name) {
		roomName = name;
	}

	public String getName() {
		return roomName;
	}

	private void setEngine(GameEngine engine) {
		this.engine = engine;
	}

	public void addObject(GameObject obj) {
		objects.add(obj);
		engine.updateGUI();
	}

	public void removeObject(GameObject obj) {
		objects.remove(obj);
		engine.updateGUI();
	}

	public List<GameObject> getObjects() {
		return objects;
	}

	public void setSmallFishStartingPosition(Point2D heroStartingPosition) {
		this.smallFishStartingPosition = heroStartingPosition;
	}

	public Point2D getSmallFishStartingPosition() {
		return smallFishStartingPosition;
	}

	public void setBigFishStartingPosition(Point2D heroStartingPosition) {
		this.bigFishStartingPosition = heroStartingPosition;
	}

	public Point2D getBigFishStartingPosition() {
		return bigFishStartingPosition;
	}

	public static Room readRoom(File f, GameEngine engine) {
		Room r = new Room();
		r.setEngine(engine);
		r.setName(f.getName());
		fillWithWater(r);
		try {
			Scanner scn = new Scanner(f);
			int y = 0;
			while (scn.hasNextLine()) {
				String line= scn.nextLine();
				
				for (int x =0; x< line.length(); x++) {
					createGameObject(line.charAt(x), new Point2D(x,y), r);
				}
				y++;
			}
			scn.close();

		} catch (FileNotFoundException e) {
			System.err.println("Ficheiro nao encontrado: " + f.getName());
			e.printStackTrace();
		}
		return r;

	}

	private static void createGameObject(char c, Point2D position, Room room) {
		GameObject obj = null;
		switch (c) {
		case 'W':
			obj = new Wall(room);
			break;
		case 'H':
			obj = new SteelHorizontal(room);
			break;
		case 'V':
			obj = new SteelVertical(room);
			break;
		case 'C':
			obj = new Cup(room);
			break;
		case 'R':
			obj = new Stone(room);
			break;
		case 'A':
			obj = new Anchor(room, false);
			break;
		case 'b':
			obj = new Bomb(room);
			break;
		case 'T':
			obj = new Trap(room);
			break;
		case 'Y':
			obj = new Log(room);
			break;
		case 'X':
			obj = new WallWithHole(room);
			break;
		case 'S':
			obj = SmallFish.getInstance();
			break;
		case 'B':
			obj = BigFish.getInstance();
			break;
		case 'K':
			obj = new Crab(room);
			break;
		case 'F':
			obj= new Buoy(room);
			break;
		case ' ':
			break;
		default:
			System.err.println("Erro caracter (" + c +") inválido");
		}
		if (obj !=null) {
			obj.setPosition(position);
			room.addObject(obj);
		}
		
	}

	private static void fillWithWater(Room room) {
		for(int i=0; i<GRID_SIZE; i++){
			for(int j=0; j<GRID_SIZE; j++){
				Water water = new Water(room);
				water.setPosition(i, j);
				room.addObject(water);
			}
		}
				
	}

}