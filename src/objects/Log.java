package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;
public class Log extends GameObject implements Breakable{
	public Log(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "trunk";
	}	

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public boolean isPassable() {
		return false;
	}
	//verificar se o tronco deve partir
	@Override
	public boolean broken() {
	    Vector2D up = new Vector2D(0, -1);
	    Point2D checkPos = position.plus(up);
	    while (checkPos.getY() >= 0) {
	        GameObject objAcima = getObjectAt(checkPos);
	        
	        if (!(objAcima instanceof MovableObject)) {
	            break;  // Para quando não há mais objetos móveis
	        }
	        
	        MovableObject movable = (MovableObject) objAcima;
	        
	        // Se encontrar qualquer objeto pesado que caiu, quebra
	        if (movable.isHeavy() && movable.hasFallen()) {
	            return true;
	        }
	        
	        // Continua para o próximo acima
	        checkPos = checkPos.plus(up);
	    }
	    return false;
	}
}
