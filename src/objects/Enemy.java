package objects;

public interface Enemy {
	void move();
	boolean canKill(GameCharacter character);
    boolean canBeKilledBy(GameCharacter character);
}