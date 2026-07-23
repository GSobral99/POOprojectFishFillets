package objects;
import java.util.Random;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class Crab extends GameCharacter implements Enemy, GravityApplied{
	private static Random random = new Random();
	public Crab(Room room) {
		super(room);
	}

	//moviemnto do crab
	@Override
	public void move() {
		//ele pode ir para a esq ou dir
		int direction = random.nextBoolean() ? -1 : 1;
		Vector2D newPos= new Vector2D(direction, 0);
		
		Vector2D v = new Vector2D(direction, 0);
		//pode mover-se se o objeto for passavel ou se não existirem obstaculos
		if (validMove(v))
			this.setPosition(position.plus(newPos));
	}
	@Override
	public boolean validMove(Vector2D dir) {
		Point2D futureMove = getPosition().plus(dir);
		GameObject object = getObjectAt(futureMove);
		
		if (object == null) {
			return true;
		}
		if (object instanceof GameCharacter)
			return true;
		// se o objeto for passavel ver se o peixe pode passar
		if (object.isPassable()) 
			return canPassWall();
		//se existir algum objeto lá não consegue passar
		if (object instanceof GameObject)
			return false;
		return true;
	}
	
	//veridicar quem pode matar
	@Override
	public boolean canKill(GameCharacter fish) {
		if (fish instanceof BigFish)
			return false;
		if  (fish instanceof SmallFish)
			return true;
		return false;
	}
	//verificar pode morrer para quem
	@Override
	public boolean canBeKilledBy(GameCharacter fish) {
		if (fish instanceof BigFish)
			return true;
		if  (fish instanceof SmallFish)
			return false;
		return false;
	}
	@Override
	public String getName() {
		return "krab";
	}

	@Override
	public int getLayer() {
		return 3;
	}
	
	
	@Override
	public boolean isPassable() {
		return false;
	}
	@Override
	protected boolean canPushObject(MovableObject obj, Vector2D dir) {
		return false;
	}
	@Override
	public boolean canPassWall() {
		return true;
	}

	@Override
	protected boolean checkDeath() {
		return false;
	}

	@Override
	public boolean gravityApllies() {
		return true;
	}
}
