package objects;

import pt.iscte.poo.game.Room;

public class Trap extends MovableObject{
	public Trap(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "trap";
	}	

	@Override
	public int getLayer() {
		return 2;
	}

	@Override
	public boolean isPassable() {
		return true;
	}

	@Override
	public boolean isHeavy() {
		return true;
	}
	//se o bigFish estiver na trap morre
	public boolean checkBigFishDeath() {
		BigFish b= BigFish.getInstance();
		if (b.getPosition().equals(position)) {
			return true;
		}
		return false;
	}
	//verificar a morte do crab	
	public boolean checkCrabDeath() {
	    for(GameObject obj : room.getObjects()) {
	    	if(obj instanceof Crab && obj.getPosition() != null && 
	           obj.getPosition().equals(position)) {
		       return true;
	    	}
	    }
	    return false;
	}

	@Override
	public boolean gravityApllies() {
		return true;
	}

	@Override
	protected boolean canPassWall() {
		return false;
	}
}
