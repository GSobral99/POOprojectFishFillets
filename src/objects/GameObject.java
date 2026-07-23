package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Point2D;

public abstract class GameObject implements ImageTile, Passable{
	protected Point2D position;
	protected Room room;
	
	public GameObject(Room room) {
		this.room = room;
	}
	
	public GameObject(Point2D position, Room room) {
		this.position = position;
		this.room = room;
	}

	public void setPosition(int i, int j) {
		position = new Point2D(i, j);
	}
	
	public void setPosition(Point2D position) {
		this.position = position;
	}

	@Override
	public Point2D getPosition() {
		return position;
	}
	
	public Room getRoom() {
		return room;
	}
	
	public void setRoom(Room room) {
		this.room = room;
	}
	// função para obter objetos em uma certa posição
	public GameObject getObjectAt(Point2D futurePos) {
		for (GameObject obj : room.getObjects()) {
			if (futurePos.equals(obj.getPosition()) && obj.getLayer() != 0) {
				return obj;
			}
		}
		return null;
	}


}
