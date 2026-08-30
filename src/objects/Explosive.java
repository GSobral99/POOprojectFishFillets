package objects;

import java.util.List;

public interface Explosive {
	boolean shouldExplode();
	List<GameObject> explode();
}