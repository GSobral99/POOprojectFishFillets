package objects;

import java.util.List;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

public abstract class MovableObject extends GameObject implements GravityApplied{
	protected abstract boolean isHeavy();
	private boolean hasFallen;
	private boolean hasMovedH;
	protected abstract boolean canPassWall();
	public MovableObject(Room room) {
		super(room);
		this.hasFallen=false;
		this.hasMovedH=false;
	}
	
	//aplicar gravidade se não tiver suporte
	public boolean applyGravity() {
		Point2D baixo= new Point2D(position.getX(),position.getY()+1);
		if (hasSupport(baixo))
			return false;
		return true;
	}
	public boolean hasSupport(Point2D pos) {
		List<GameObject> objects = room.getObjects();
		//verificar se os objetos têm suporte
		for(GameObject obj : objects) {
			if (obj.getPosition() != null && obj.getPosition().equals(pos)) {
				if (obj.isPassable() && this.canPassWall()) {
					if (obj instanceof Trap)
						return true;
	                continue; // ignora este objeto e continua procurando
	            }
				if(obj instanceof GameCharacter && this instanceof Trap) {
					return false;
				}
				
				if (!(obj instanceof Water)) {
					return true;
				}
				
			}
		}
		return false;
	}
	public boolean hasFallen() {
		return hasFallen;
	}
	public boolean hasMovedH() {
		return hasMovedH;
	}
	public void setHasMovedH(boolean f) {
		hasMovedH=f;
	}
	public void setHasFallen(boolean f) {
		hasFallen= f;
	}
		
}