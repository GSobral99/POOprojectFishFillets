package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class Buoy extends MovableObject{

	public Buoy(Room room) {
		super(room);
	}
	//movimentar para cima ou para baixo consuante a condição
	public void moveUp() {
        if (isSupportingMovableObj()) {
            Vector2D down = new Vector2D(0, 1);
            Point2D posAbaixo = position.plus(down);
            GameObject objAbaixo = getObjectAt(posAbaixo);
            
            if (objAbaixo == null) {
                setPosition(posAbaixo);
            }
            return;
        }
        Vector2D up = new Vector2D(0, -1);
        Point2D posAcima = position.plus(up);
        GameObject objAcima = getObjectAt(posAcima);

        if (objAcima == null) {
            setPosition(posAcima);
        }
    }
	//verificar se tem algum objeto em cima
	public boolean isSupportingMovableObj() {
		Vector2D up = new Vector2D(0,-1);
		Point2D posAcima = position.plus(up);
		for (GameObject obj : room.getObjects()) {
			if (obj == this)
				continue;
			if(obj instanceof Buoy)
				return false;
			if (obj instanceof MovableObject) {
				if(obj.getPosition() != null && obj.getPosition().equals(posAcima)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String getName() {
		return "buoy";
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
	protected boolean isHeavy() {
		return false;
	}
	
	@Override
	public boolean applyGravity() {
		return false;
	}

	@Override
	public boolean gravityApllies() {
		return false;
	}

	@Override
	protected boolean canPassWall() {
		return false;
	}
	
}