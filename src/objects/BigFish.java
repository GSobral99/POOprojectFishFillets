package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class BigFish extends GameCharacter {

	private static BigFish bf = new BigFish(null);
	
	private BigFish(Room room) {
		super(room);
	}

	public static BigFish getInstance() {
		return bf;
	}
	
	//verificar movimento válido
	@Override
	public boolean validMove(Vector2D dir) {
	    Point2D futureMove = getPosition().plus(dir);
	    GameObject object = getObjectAt(futureMove);
	    
	    // Se não existir nenhum objeto pode mover
	    if (object == null) {
	        return true;
	    }
	    
	    // Trap: BigFish entra (e morre)
	    if (object instanceof Trap) {
	        return true;
	    }
	    
	    // Enemy: pode mover
	    if (object instanceof Enemy) {
	        return true;
	    }
	    
	    // Outros objetos passáveis que não atravessa
	    if (object.isPassable() && !canPassWall()) {
	        return false;
	    }
	    
	    // Se o objeto não for movable, não pode passar
	    if (!(object instanceof MovableObject)) {
	        return false;
	    }
	    
	    // Se for movable, ver se o objeto pode ser empurrado
	    return canPushObject((MovableObject) object, dir);
	}
	@Override
	protected boolean canPushObject(MovableObject obj, Vector2D dir) {
		if (dir.getX() == 0) {  // Movimento vertical (dir.getY() será -1 ou 1)
	        Point2D nextPos = obj.getPosition().plus(dir);
	        GameObject beyond = getObjectAt(nextPos);
	        
	        // Se há outro objeto móvel além deste, não pode empurrar
	        if (beyond instanceof MovableObject) {
	            return false; // Já há outro objeto, não pode empurrar
	        }
	        
	        // Verifica se há espaço livre para empurrar
	        if (beyond == null || beyond.isPassable()) {
	            return true; // Pode empurrar
	        }
	        
	        return false; // Há obstáculo fixo
	    }
		return true;
	}
	//verificar se morreu
	@Override
	protected boolean checkDeath() {
		if (position == null) 
			return false;
		if (hasExited()) {
	        return false;
	    }
		// 1. Verificar se está em uma armadilha (trap)
		for (GameObject obj : room.getObjects()) {
			if (obj instanceof Trap && obj.getPosition() != null && obj.getPosition().equals(position)) {
				return true; // Morre na armadilha
			}
		}
		
		// 2. verificar objetos pesados em cima
		Vector2D up = new Vector2D(0, -1);
		int heavyCount = 0; // contador de objetos pesados acima
		Point2D checkPos = position.plus(up);
		GameObject objAcima = getObjectAt(checkPos);
		
		// verificar todos os objetos acima do peixe
		while (objAcima instanceof MovableObject) {
			MovableObject movable = (MovableObject) objAcima;
			
			// Contar objetos pesados
			if (movable.isHeavy()) {
				heavyCount++;
			}
			
			// Passar para o próximo objeto acima
			checkPos = checkPos.plus(up);
			objAcima = getObjectAt(checkPos);
		}
		// Morre se tiver mais de 1 objeto pesado em cima
		return heavyCount > 1;
	}
	@Override
	public String getName() {
		//se morrer amostra sangue
		if (isDead()) {
	        return "blood";
	    }
		if (direction== Direction.LEFT) {
			return "bigFishLeft";
		}
		else {
			return "bigFishRight";
		}
	}

	@Override
	public int getLayer() {
		return 5;
	}
	@Override
	public boolean canPassWall() {
		return false;
	}
	@Override
	public boolean isPassable() {
		return false;
	}
}