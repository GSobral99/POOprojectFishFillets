package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public abstract class GameCharacter extends GameObject implements CanPassWall{

	protected abstract boolean canPushObject(MovableObject obj, Vector2D dir); //verificar se o game character pode empurrar objeto 
																			   //(Override para cada peixe)
	protected Direction direction;
	protected abstract boolean checkDeath(); //verificar se morreu (Override para cada peixe)
	private boolean hasExited;
	private boolean isDead;
	
	public GameCharacter(Room room) {
		super(room);
		this.hasExited=false;
	}
	//movimento do personagem
	public void move(Vector2D dir){
		//dar sempre update da direção
		updateDirection(dir);
		//ver se o movimento é válidp
		if (!validMove(dir)) {
			return;
		}
		Point2D futurePos = getPosition().plus(dir);
		GameObject obj = getObjectAt(futurePos);
		//se o casa estiver livre pode-se mover
		if(obj == null) {
			setPosition(futurePos);
			checkIfExited();
			return;
		}
		if (obj instanceof Trap || obj instanceof Enemy) {
			setPosition(futurePos);
	        checkIfExited();
	        return;
	    }
	    if (obj instanceof WallWithHole) {
	        if (canPassWall()) {
	            setPosition(futurePos);
	            checkIfExited();
	        }
	        return;
	    }
		//se o objeto for movivel verificar se ele pode se mover ou não
		if (obj instanceof MovableObject) {
			//se o objeto não se pode mover o peixe não se move
			if (!pushObject((MovableObject) obj, dir)) {
				return; 
			}
		}
		setPosition(futurePos);
			
		checkIfExited();

	}
	// Each character has its own movement rules, so there is no sensible
	// default here — SmallFish, BigFish and Crab must each provide one.
	public abstract boolean validMove(Vector2D dir);
	//verfificar se pode empurrar um objeto
	protected boolean pushObject(MovableObject obj, Vector2D dir) {
		Point2D newPos = obj.getPosition().plus(dir);
		GameObject obstaculo = getObjectAt(newPos);
		//se a anchor já se moveu Horizontalmente não pode voltar a mover-se
		if (obj instanceof Anchor && obj.hasMovedH()){
			return false;
		}
		//se a proxima posição estiver vazia 
	    if (obstaculo == null) {
	    	//verificar que não se está a empurrar um objeto dentro de um objecto Passable com o BigFish 
	    	for (GameObject o: room.getObjects()) {
	    		if(o != obj && o instanceof WallWithHole && !canPassWall() && obj.getPosition().equals(o.getPosition())) {
	    			return false;
	    		}
	    	}
	        if (dir.getX() != 0)
	            obj.setHasMovedH(true);
	        obj.setPosition(newPos);
	        return true;
	    }
	    //	
		if (obstaculo instanceof WallWithHole && obj.canPassWall()) {
			if(dir.getX()!=0)
		        obj.setHasMovedH(true);
		    obj.setPosition(newPos);
		    return true;
	    }
		//se tiver o um obstulo não movivel o obj não se pode mover
		if (!(obj instanceof MovableObject)) {
			return false;
		}
		//se o obstaculo for movivel
		if (obstaculo instanceof MovableObject) {
			//empurar o proximo objeto tambem fazendo as verificações novemente usando a propria função
			if (pushObject((MovableObject) obstaculo, dir)) {
				if (dir.getX()!=0)
					obj.setHasMovedH(true);
				obj.setPosition(newPos);
				return true;
			}
			return false;
		}
		
		return false;
	}
	//verificar se o peixe está dentro de campo
	private boolean isOutOfBounds() {
		if (position== null)
			return false;
		int max = Room.GRID_SIZE - 1;
		return (position.getX() > max || position.getY() > max || position.getX() < 0 || position.getY() < 0);
	}
	
	//verificar se o peixe saiu
	private void checkIfExited() {
		if (isOutOfBounds() && !hasExited) {
			hasExited=true;
			room.removeObject(this);
	
			ImageGUI.getInstance().update();
		}
	}
	
	
	protected void updateDirection(Vector2D dir) {
		if (dir.getX() == 1) 
			direction = Direction.RIGHT;
		if (dir.getX() == -1)
			direction = Direction.LEFT;
	}
	
	public boolean isDead() {
		return isDead || checkDeath();
	}
	public void setDead(boolean dead) {
		this.isDead = dead;
	}
	@Override
	public int getLayer() {
		return 5;
	}
	public boolean hasExited() {
		return hasExited;
	}
	public void reset() {
		this.hasExited = false;
		this.isDead = false;
	}
}