
package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import java.io.File;
import java.util.Observer;

public interface IModel{
    public void generateMaze(int row,int col);
    public void updateCharacterLocation(int direction);
    public void assignObserver(Observer o);
    public Maze getMaze();
    public int getColChar();
    public int getRowChar();
    public void solveMaze(Maze maze);
    public Solution getSolution();
    public void startServers();
    public void setPlayer(int row, int col);
    public  void saveMaze(File file);

    public boolean LoadMaze(File f);

    void CloseApp();
}



