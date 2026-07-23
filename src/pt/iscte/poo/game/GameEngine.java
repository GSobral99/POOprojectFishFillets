package pt.iscte.poo.game;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import objects.*;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class GameEngine implements Observer {
	
	private Map<String,Room> rooms;
	private Room currentRoom;
	private int lastTickProcessed = 0;
	private GameCharacter fishAtual; //retorna o fish que está atualmente a ser movimentado
	private int currentLevel = 0; //nivel atual
	private long gameTime;  //tempo de jogo
	private int smallFishMoves;  //movimentos do smallFish
	private int bigFishMoves; // movimentos do bigFish
	private boolean gameComplete= false; //o jogo começa como não completado
	
	public GameEngine() {
		gameTime = System.currentTimeMillis();
		rooms = new HashMap<String,Room>();
		startLevel(0);
		fishAtual= SmallFish.getInstance();
		updateMessage();
		updateGUI();
	}
	
	@Override
	public void update(Observed source) {
		if (gameComplete)
			return;
		if (ImageGUI.getInstance().wasKeyPressed()) {
			int k = ImageGUI.getInstance().keyPressed();
			ImageGUI.getInstance().update();
			//se pressionar 'R' restart level
			if(k == KeyEvent.VK_R) {
				restartLevel();
				return;
			}
			//se pressionar 'SPACE' switch character
			if (k == KeyEvent.VK_SPACE) {
				switchFish();
			}
			//teclas de direção dão vetores direção que vão mover os peixes
			if ( k == KeyEvent.VK_DOWN || k == KeyEvent.VK_UP || k == KeyEvent.VK_LEFT || k == KeyEvent.VK_RIGHT) {
				//se o peixe ainda não saiu tenta mover-se
				if(!fishAtual.hasExited()) {
					fishAtual.move(Direction.directionFor(k).asVector());
					//se morreu restart level
					if (checkDeath()) {
						restartLevel();
						return;
					}
					//os inimigos movem-se se os peixes se moverem
					moveEnemies();
					if(fishAtual instanceof SmallFish) {
						smallFishMoves++;
					}else {
						bigFishMoves++;
					}
					//se o peixe saiu trocar de peixe
					if(fishAtual.hasExited())
						switchFish();
				}
				
				//se o nivel acabou passar para o proximo
				if (checkLevelComplete()) {
					ImageGUI.getInstance().showMessage("Nível Completo!", "Parabéns! A avançar para o próximo nível...");
					nextLevel();
					return;
				}
			}
		}
		int t = ImageGUI.getInstance().getTicks();
		while (lastTickProcessed < t) {	
			processTick();
			ImageGUI.getInstance().update();
			//a cada tick processar gravidade remoção de blocos como Log ou bomba e o spawn de inimigos
			applyGravityAll(); //aplicar gravidade
			removeBreakable(); // remover objetos que foram partidos
			bombExplosion(); // remover objetos explosivos e outros blocos caso ele tenha explodido
			spawnEnemies(); //spawnar inimigos
			moveBuoys(); // mover boias
			checkGameCollisions(); //verificar colisões de inimigos
			//verificar morte a cada tick
			if(checkDeath()) {
				return;
			}
		}
		updateMessage();
		ImageGUI.getInstance().update();

	}
	
	//iniciar nivel
	private void startLevel(int levelNumber) {
		currentLevel = levelNumber;
		String roomFileName = "room" + currentLevel + ".txt";
		
		//carrega o nível do ficheiro
		File roomFile = new File("./rooms/" + roomFileName);
		//se não existe aquele nivel é porque o jogo acabou
		if (!roomFile.exists()) {
			showGameComplete();
			return;
		}
		//lê o room e substitui no mapa
		currentRoom = Room.readRoom(roomFile, this);
		rooms.put(roomFileName, currentRoom);
		
		//iniciar os peixes
		SmallFish.getInstance().setRoom(currentRoom);
		SmallFish.getInstance().reset();
		
		BigFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().reset();
		
		fishAtual = SmallFish.getInstance();
		lastTickProcessed = ImageGUI.getInstance().getTicks();
		
	}
	
	//iterar o numero do nivel
	private void nextLevel() {
		startLevel(currentLevel + 1);
		updateGUI();
	}
	
	//verificar se o nivel acabou (se os peixes já sairam)
	private boolean checkLevelComplete() {
		SmallFish smallFish = SmallFish.getInstance();
		BigFish bigFish = BigFish.getInstance();
		//se amboas os peixes sairam o nivel acaba
		if (smallFish.hasExited() && bigFish.hasExited()) {
			return true;
		}
		return false;
	}
	
	//reiniciar level
	private void restartLevel() {
		File roomFile = new File("./rooms/" + currentRoom.getName());
		currentRoom = Room.readRoom(roomFile, this);
		rooms.put(currentRoom.getName(), currentRoom);
		
		SmallFish.getInstance().setRoom(currentRoom);
		SmallFish.getInstance().reset();
		
		BigFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().reset();
		
		fishAtual = SmallFish.getInstance();
		lastTickProcessed = ImageGUI.getInstance().getTicks();
		
		updateGUI();
	}
	//verificar morte dos peixes
	private boolean checkDeath() {
		SmallFish smallFish= SmallFish.getInstance();
		BigFish bigFish = BigFish.getInstance();
		if(smallFish.isDead()) {
			ImageGUI.getInstance().showMessage("Small Fish","Game Over!");
			restartLevel();
			return true;
		}
		if (bigFish.isDead()) {
			ImageGUI.getInstance().showMessage("Big Fish died","Game Over!");
			restartLevel();
			return true;
		}
		return false;
	}

	//trocar o peixe
		private void switchFish() {
			if (fishAtual instanceof SmallFish) {
				if(!BigFish.getInstance().hasExited() && !BigFish.getInstance().isDead() ) {
					fishAtual = BigFish.getInstance();
				}
			}else {
				if (!SmallFish.getInstance().hasExited() && !SmallFish.getInstance().isDead()) {
					fishAtual=SmallFish.getInstance();
				}
			}
		}
	
	//aplicar gravidade
	private void applyGravityAll() {
		Vector2D v=new Vector2D(0,1);
		for (GameObject obj: currentRoom.getObjects()) {
			//a cada objeto movivel aplicar a gravidade
			if(obj instanceof MovableObject) {
				MovableObject n = (MovableObject) obj;
				//mover o objeto para baixo e colocar hasFallen como true
				if (n.applyGravity()) {
					((MovableObject) obj).setHasFallen(true);
					n.setPosition(n.getPosition().plus(v));		
				}
			}else if(obj instanceof GravityApplied) {
				//se for um GameCharacter  e GravityApplied mover para baixo (neste caso o crab)
				if (((GameCharacter)obj).validMove(v)) {
					obj.setPosition(obj.getPosition().plus(v));	
				}
			}
		}
	}
	//remover os Log se for preciso
	private void removeBreakable() {
		  List<GameObject> objToRemove = new ArrayList<>();
		    for (GameObject obj : currentRoom.getObjects()) {
		        if (obj instanceof Breakable) {
		            if (((Breakable) obj).broken()) {
		                objToRemove.add(obj);
		            }
		        }
		    }		    
		    for (GameObject log : objToRemove) {
		        currentRoom.removeObject(log);
		    }
	}
	//remover bomba e blocos asjacentes
	private void bombExplosion() {
		List<GameObject> remover = new ArrayList<>();
		for (GameObject obj : currentRoom.getObjects()) {
			 if (obj instanceof Explosive) { 
				 Explosive exp= (Explosive) obj;
				 if (exp.deveExplodir()) {
					 remover.addAll(exp.explode());
				 }
			 }
		}
		//remover os blocos necessários
		for (GameObject r : remover) {
		        if (r != null) {
		            currentRoom.removeObject(r);
		        }
		        if(r instanceof GameCharacter && !(r instanceof Enemy)) {
			 		ImageGUI.getInstance().showMessage("You died","Game Over!");
			 		restartLevel();
			 	}
		 }
	}
	//spanwar inimigos (crab)
	public void spawnEnemies() {
		List<Crab> crabsToAdd = new ArrayList<>();
	    for (GameObject obj : currentRoom.getObjects()) {
	        if (obj instanceof Stone) {
	            Stone stone = (Stone) obj;
	            //se a pedra consegue spawnar o crab adiciona-o ao jogo
	            if (stone.shouldSpawnCrab()) {
	                Crab newCrab = stone.spawnCrab();
	                crabsToAdd.add(newCrab);
	            }
	        }
	    }
	    for (Crab crab : crabsToAdd) {
	        currentRoom.addObject(crab);
	    }
	}
	//mover todos os inimigos
	private void moveEnemies() {
		for (GameObject obj : currentRoom.getObjects()) {
			if (obj instanceof Enemy) {
				Enemy enemy = (Enemy) obj;
				enemy.move();
			}
		}
	}
	
	//verificar colisões
	private void checkGameCollisions() {
		List<GameObject> remove= new ArrayList<>();
		BigFish bigFish = BigFish.getInstance();
		SmallFish smallFish = SmallFish.getInstance();
		for (GameObject obj : currentRoom.getObjects()) {
			if (obj instanceof Enemy) {
				Point2D enemyPos = obj.getPosition();
				Enemy enemy = (Enemy) obj;
				
				// Verificar colisão com SmallFish
				if (enemy.canKill(smallFish) && enemyPos != null && smallFish.getPosition() != null && !smallFish.hasExited() &&
						enemyPos.equals(smallFish.getPosition())) {
						smallFish.setDead(true);
						remove.add(smallFish);
						return;
				}
				//verificar colisão com o BigFish
				if (enemy.canBeKilledBy(bigFish) && bigFish.getPosition() != null && !bigFish.hasExited() && enemyPos != null &&
						enemyPos.equals(bigFish.getPosition())) {
						remove.add((GameObject) enemy);
				}
			}
			// Verificar Trap matar Crab
			if (obj instanceof Trap) {
				Trap trap = (Trap) obj;
				for (GameObject other : currentRoom.getObjects()) {
					if (other instanceof Crab && other.getPosition() != null && other.getPosition().equals(trap.getPosition())) {
						remove.add(other);
					}
				}	
 			} 
		}
		for (GameObject r : remove) {
	        if (r != null) {
	            currentRoom.removeObject(r);
	        }
		}
	}
	//mover boias
	public void moveBuoys() {
		for (GameObject obj: currentRoom.getObjects()) {
			if (obj instanceof Buoy)
				((Buoy) obj).moveUp();
		}
	}
	//autalizar mensagem de Status na barra informativa
	private void updateMessage() {
		long time = (System.currentTimeMillis() - gameTime)/1000;
		long minutes = time/60;
		long seconds= time % 60;
		
		String message= "Nível " + currentLevel + " | Tempo: " +"Min "+ minutes + " Sec " + seconds + " | Big: " + bigFishMoves + " | Small: " + smallFishMoves;
		ImageGUI.getInstance().setStatusMessage(message);
	}
	
	//mostrar a tabela do fim de jogo
	private void showGameComplete() {
		if (gameComplete) {
			return;
		}
		gameComplete=true;
		
		long totalTimeSeconds = (System.currentTimeMillis() - gameTime)/ 1000;
		int totalMoves = bigFishMoves + smallFishMoves;
		
		boolean isTopTen = HighScore.addHighScore(totalTimeSeconds, totalMoves);
		
		String message = "Parabéns! Completaste o jogo! \n" + "Tempo: " + totalTimeSeconds + " segundos\n" + "Movimentos: " + totalMoves + "\n";
		
		if (isTopTen) {
			message += "Entraste para o TOP 10 \n";
		}
		
		message += HighScore.getHighScoreTable();
		
		ImageGUI.getInstance().showMessage("Jogo Completo!", message);
		
	}
	
	//fish atual
	public GameCharacter getFishAtual() {
		return fishAtual;
	}
	private void processTick() {		
		lastTickProcessed++;
	}

	public void updateGUI() {
		if(currentRoom!=null) {
			ImageGUI.getInstance().clearImages();
			ImageGUI.getInstance().addImages(currentRoom.getObjects());
		}
	}
}
