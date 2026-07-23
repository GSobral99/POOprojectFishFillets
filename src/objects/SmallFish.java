package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class SmallFish extends GameCharacter {

	private static SmallFish sf = new SmallFish(null);
	
	private SmallFish(Room room) {
		super(room);
	}

	public static SmallFish getInstance() {
		return sf;
	}
	
	//so pode empurrar um objeto e se ele não for pesado
	@Override
	protected boolean canPushObject(MovableObject obj, Vector2D dir) {
		if (obj.isHeavy())
			return false;
		Point2D nextPos = obj.getPosition().plus(dir);
		GameObject nextObj = getObjectAt(nextPos);
		if (nextObj instanceof MovableObject)
			return false;
		return true;
	}
	
	
	@Override
	public boolean validMove(Vector2D dir) {
	    Point2D futureMove = getPosition().plus(dir);
	    GameObject object = getObjectAt(futureMove);
	    
	    // Se não existir nenhum objeto pode mover
	    if (object == null) {
	        return true;
	    }
	    
	    // TRAP: SmallFish atravessa
	    if (object instanceof Trap) {
	        return true;
	    }
	    
	    // Buoy: não pode mover para baixo
	    if (object instanceof Buoy && dir.getY() == 1) {
	        return false;
	    }
	    
	    // Enemy: pode mover
	    if (object instanceof Enemy) {
	        return true;
	    }
	    // Outros objetos passáveis que não atravessa
	    if (object.isPassable() && canPassWall()) {
	        return true;
	    }

	    // Se o objeto não for movable, não pode passar
	    if (!(object instanceof MovableObject)) {
	        return false;
	    }
	    
	    // Se for movable, ver se o objeto pode ser empurrado
	    return canPushObject((MovableObject) object, dir);
	}
	
	
	@Override
	protected boolean checkDeath() {
		if (position == null) 
			return false;
		if (hasExited()) {
	        return false;
	    }
		Vector2D up = new Vector2D(0, -1);
		int objCount = 0; // contador de objetos leves
		boolean hasHeavyObject = false; // tem objeto pesado
		
		Point2D checkPos = position.plus(up);
		GameObject objAcima = getObjectAt(checkPos);
		
		// Verificar todos os objetos acima do peixe
		while (objAcima instanceof MovableObject) {
			MovableObject movable = (MovableObject) objAcima;
			
			// Ignorar armadilhas (são passáveis para o peixe pequeno)
			if (objAcima instanceof Trap) {
				checkPos = checkPos.plus(up);
				objAcima = getObjectAt(checkPos);
				continue;
			}
			if (objAcima instanceof Buoy) {
			    checkPos = checkPos.plus(up);
			    objAcima = getObjectAt(checkPos);
			    continue;
			}
			
			// Se for pesado, marca e sai (já basta para matar)
			if (movable.isHeavy()) {
				hasHeavyObject = true;
				break;
			
			} else {
				// Se for leve, conta
				objCount++;
			}
			
			// Passar para o próximo objeto acima
			checkPos = checkPos.plus(up);
			objAcima = getObjectAt(checkPos);
		}
		
		// Morre se tiver um objeto pesado OU mais de 1 objeto leve em cima
		return (hasHeavyObject || objCount > 1);
	}
	@Override
	public String getName() {
		if (isDead()) {
	        return "blood";
	    }
		if (direcao== Direction.LEFT) {
			return "smallFishLeft";
		}
		else {
			return "smallFishRight";
		}
	}
	@Override
	public boolean canPassWall() {
		return true;
	}

	@Override
	public int getLayer() {
		return 5;
	}

	@Override
	public boolean isPassable() {
		return false;
	}
}
