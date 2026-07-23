package objects;

import pt.iscte.poo.game.Room;

public class Anchor extends MovableObject{

	public Anchor(Room room, boolean hasMoved) {
		super(room);
	}
	
	@Override
	public String getName() {
		return "anchor";
	}	

	@Override
	public int getLayer() {
		return 2;
	}
	
	@Override
	public boolean isPassable() {
		return false;
	}
	@Override
	public boolean isHeavy() {
		return true;
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
