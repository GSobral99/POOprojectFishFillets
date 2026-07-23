package pt.iscte.poo.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class HighScore implements Comparable<HighScore>{
	private long totalTime; //tempo total
	private int totalMoves; //movimentos totais
	private static final String HIGHSCORE_FILE = "highScores.txt"; //ficheiro de highscores
	private static final int MAX_HIGHSCORES = 10; //numero max de scores
	
	public HighScore(long totalTime, int totalMoves) {
		this.totalTime= totalTime;
		this.totalMoves=totalMoves;
	}
	//comparar por tempo e desempatar com movimentos
	@Override
	public int compareTo(HighScore o) {
        int timeCompare = Long.compare(this.totalTime, o.totalTime);
        if (timeCompare != 0){
        	return timeCompare;
        }
        return Integer.compare(this.totalMoves, o.totalMoves);
	}
	
	//adicionar highscore
	public static boolean addHighScore(long totalTime, int totalMoves) {
		HighScore o = new HighScore(totalTime, totalMoves);
		List<HighScore> scores= loadHighScores();
		
		scores.add(o);
		
		Collections.sort(scores);
		
		boolean isTopTen = scores.indexOf(o) < MAX_HIGHSCORES;
		
		if (scores.size() > MAX_HIGHSCORES) {
			scores = scores.subList(0, MAX_HIGHSCORES);
		}
		saveHighScores(scores);
		
		return isTopTen;
		
	}
	
	//listar os highscores
	public static List<HighScore> loadHighScores(){
		List<HighScore> scores = new ArrayList<>();
		File file= new File(HIGHSCORE_FILE);
		
		if (!file.exists()) {
			return scores;
		}
		try {
			Scanner scn= new Scanner(file);
			while(scn.hasNextLine()) {
				String line= scn.nextLine();
				if (!line.isEmpty()) {
					String[] parts = line.split(":");
					if (parts.length == 2) {
						long time = Integer.parseInt(parts[0]);
						int moves = Integer.parseInt(parts[1]);
						scores.add(new HighScore(time, moves));
					}
				}
			}
			scn.close();
		} catch (FileNotFoundException e){
			System.err.println("Erro ao carregar ficheiro" + e.getMessage());
		}
		return scores;
	}
	//escrever novo highscore
	private static void saveHighScores(List<HighScore> scores) {
		File file= new File(HIGHSCORE_FILE);
		try {
			PrintWriter writer = new PrintWriter(file);
			for (HighScore score: scores) {
				writer.println(score.totalTime + ":" + score.getTotalMoves());
			}
			writer.close();
		} catch (IOException e){
			System.err.println("Erro ao guardar highscores: " + e.getMessage());
		}
	}
	
	//escrever a tabela de highscores
	public static String getHighScoreTable() {
		List<HighScore> scores= loadHighScores();
		
		String table = "_____________________________________________";
		table += "\n                             Top 10 HighScores            \n";
		table += "     Tempo(Segundos)        |   Movimentos     \n";
		
		for (int i =0; i < scores.size(); i++) {
			HighScore score = scores.get(i);
			table += "                  " + score.getTotalTime() + "                          |             "  
			+ score.getTotalMoves() + "    \n";
		}
		table += "_____________________________________________";
		
		return table;
	}
	
	public long getTotalTime() {
        return totalTime;
    }
	public int getTotalMoves() {
        return totalMoves;
    }
}
