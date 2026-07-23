package objects;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class Bomb extends MovableObject implements Explosive{
	public Bomb(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "bomb";
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
	//verificar se a bomba deve explodir
	@Override
	public boolean deveExplodir() {
		Vector2D down = new Vector2D(0, 1);
        Point2D posAbaixo = position.plus(down);
        //verificar se o objeto caiu e verificar se já tem suporte novamente
		if (((MovableObject)this).hasFallen() && hasSupport(posAbaixo)) {
			//se for um dos peixes não explode
			if (getObjectAt(posAbaixo) instanceof GameCharacter && !(getObjectAt(posAbaixo) instanceof Enemy)){
				setHasFallen(false);
				return false;
			}
			return true;
		}
		return false;	
	}
	//remover os objetos da explosão
	@Override
	public List<GameObject> explode() {
		//criar array de objetos a remover
		List<GameObject> remove = new ArrayList<>();
		List<Point2D> directions = position.getNeighbourhoodPoints();
		if (deveExplodir()) {
			//se a bomba deve explodir ver os pontos adjacentes e adiciona-los para remoção
			for (Point2D p: directions) {
				remove.add(getObjectAt(p));
			}
			remove.add(this);
		}
		return remove;
		
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
