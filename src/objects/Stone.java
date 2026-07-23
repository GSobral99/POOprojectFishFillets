package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class Stone extends MovableObject{
	private boolean hasSpawned = false;
	public Stone(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "stone";
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
	
	//ver se deve spawnar Crab
	public boolean shouldSpawnCrab() {
		//verificar se ainda não spawnou
        if (hasSpawned) {
            return false;
        }
        Vector2D up = new Vector2D(0, -1);
        Point2D crabPos = position.plus(up);
        GameObject objAcima = getObjectAt(crabPos);
        //se a casa em cima estiver vazia e moveu-se horizontalmente spawna
        if ((objAcima==null || objAcima.isPassable()) && hasMovedH())
        	return true;
        
        return false;
    }
	//spawnar um crab
	public Crab spawnCrab() {
		Vector2D up = new Vector2D(0, -1);
        Point2D crabPos = position.plus(up);
        //criar um crab
        Crab c = new Crab(room);
        c.setPosition(crabPos);
        hasSpawned = true;
        
        return c;
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
