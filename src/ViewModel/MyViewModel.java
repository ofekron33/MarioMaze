
package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    private IModel model;
    private Maze maze;
    private int rowChar;
    private int colChar;
    private Solution sol;

    public void setPlayer(int row , int col) {
        model.setPlayer(row,col);
    }



    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this);
        this.maze = null;
        rowChar=model.getRowChar();
        colChar=model.getColChar();
    }


    public Maze getMaze() {
        return maze;
    }


    public int getRowChar() {
        return rowChar;
    }

    public int getColChar() {
        return colChar;
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof IModel)
        {
            if(arg.equals(1)) //generate maze
            {
                this.maze = model.getMaze();
                rowChar=model.getRowChar();
                colChar=model.getColChar();
            }
            else if(arg.equals(2)) //move char
            {
                Maze maze = model.getMaze();
                int rowChar = model.getRowChar();
                int colChar = model.getColChar();
                this.rowChar = rowChar;
                this.colChar = colChar;
            }
            else if(arg.equals(3)) //get solution
            { sol=model.getSolution();
            }
            else if(arg.equals(4)) //load maze
            {
                int rowChar = model.getRowChar();
                int colChar = model.getColChar();
                this.maze=model.getMaze();
            }
//

            setChanged();
            notifyObservers(arg);
        }

    }


    public void generateMaze(int row,int col)
    {
        this.model.generateMaze(row,col);
        rowChar=model.getRowChar();
        colChar=model.getColChar();
    }


    public void moveCharacter(KeyCode keyCode)
    {
        int direction = -1;
        switch (keyCode){
            case NUMPAD1: //Lower-left diagonal
                direction = 1;
                break;
            case NUMPAD2: //Down
                direction = 2;
                break;
            case S:
                direction = 2;
                break;
            case NUMPAD3: //Lower-right diagonal
                direction = 3;
                break;
            case NUMPAD4: //Left
                direction = 4;
                break;
            case A: //Left
                direction = 4;
                break;
            case NUMPAD6: //Right
                direction = 6;
                break;
            case D: //Right
                direction = 6;
                break;
            case NUMPAD7: //Upper-left diagonal
                direction = 7;
                break;
            case NUMPAD8: //Up
                direction = 8;
                break;
            case W: //Up
                direction = 8;
                break;

            case NUMPAD9: //Upper-right diagonal
                direction = 9;
                break;
            case CONTROL: //Upper-right diagonal
                direction = 20;
                break;
        }

        model.updateCharacterLocation(direction);
    }


    public void solveMaze(Maze maze)
    {
        model.solveMaze(maze);
    }

    public Solution getSolution()
    {
        return sol;
    }

    public void saveMaze(File file){
        model.saveMaze(file);
    }

    public boolean LoadMaze(File f) {

            if(model.LoadMaze(f)== false){
                return false;

            }
            else
                return true;

    }

    public void CloseApp() {
        model.CloseApp();
    }
}
