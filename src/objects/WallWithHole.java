package objects;

import pt.iscte.poo.game.Room;

public class WallWithHole extends GameObject{
	public WallWithHole(Room room) {
		super(room);
	}
	@Override
	public String getName() {
		return "holedWall";
	}	

	@Override
	public int getLayer() {
		return 1;
	}
	@Override
	public boolean isPassable() {
		return true;
	}
}
