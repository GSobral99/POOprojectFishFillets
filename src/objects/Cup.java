package objects;

import pt.iscte.poo.game.Room;

public class Cup extends MovableObject {
	public Cup(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "cup";
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
		return false;
	}

	@Override
	public boolean gravityApllies() {
		return true;
	}

	@Override
	protected boolean canPassWall() {
		return true;
	}
}
