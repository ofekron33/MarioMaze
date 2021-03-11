package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class SolDisplayer extends Canvas {

    private ArrayList<AState> solutionPath;
    private Maze m;
    private StringProperty ImageFileSolvePath = new SimpleStringProperty();
    private StringProperty imageFileNameEnd = new SimpleStringProperty();

    public String getImageFileNameEnd() {
        return imageFileNameEnd.get();
    }

    public StringProperty imageFileNameEndProperty() {
        return imageFileNameEnd;
    }

    public void setImageFileNameEnd(String imageFileNameEnd) {
        this.imageFileNameEnd.set(imageFileNameEnd);
    }

    public SolDisplayer() {
        widthProperty().addListener(e->redraw());
        heightProperty().addListener(e->redraw());
    }




    public void redraw(){
        if(solutionPath!=null){
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / m.getNumOfRows();
            double cellWidth = canvasWidth / m.getNumOfCols();
            Image solImage=null;
            Image endImage = null;
            try {
                GraphicsContext graphicsContext = getGraphicsContext2D();
                graphicsContext.clearRect(0, 0, getWidth(), getHeight());
                solImage = new Image(new FileInputStream(ImageFileSolvePath.get()));
                 endImage = new Image(new FileInputStream(imageFileNameEnd.get()));

                for(int i=0; i<solutionPath.size();i++){
                    MazeState state=(MazeState)solutionPath.get(i);
                    int row=state.getPosition().getRowIndex();
                    int col=state.getPosition().getColumnIndex();
                    graphicsContext.drawImage(solImage, col * cellWidth, row * cellHeight,cellWidth, cellHeight);
                }
                graphicsContext.drawImage(endImage, m.getGoalPosition().getColumnIndex() * cellWidth, m.getGoalPosition().getRowIndex()* cellHeight, cellWidth, cellHeight);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }



        }
    }


    public void setMaze(Maze Maze) {
        this.m = Maze;
    }
    public  void setSolution(ArrayList<AState>solutionPath ){
        this.solutionPath=solutionPath;
        redraw();

    }

    public String getImageFileSolvePath() {
        return ImageFileSolvePath.get();
    }

    public void setImageFileSolvePath(String imageFileNameWall) {
        this.ImageFileSolvePath.set(imageFileNameWall);
    }


    public void clear(){
        GraphicsContext graphicsContext = getGraphicsContext2D() ;
        graphicsContext.clearRect(0,0,getWidth(),getHeight());


    }

    public void zoom (ScrollEvent scrollEvent){
        if(scrollEvent.isControlDown()) {
            if (scrollEvent.getDeltaY() < 0)
            {
                if(getWidth()<63 || getHeight()<63)
                {
                    Alert alert1=new Alert(Alert.AlertType.INFORMATION,"can not zoom out anymore... where is Mario?!?!?");
                    alert1.show();                }
                else {
                    setHeight(getHeight() / 1.05);
                    setWidth(getWidth() / 1.05);
                    redraw();
                }
            }
            if (scrollEvent.getDeltaY() > 0) {
                if(getWidth()>6300 || getHeight()>6300)
                {
                    Alert alert1=new Alert(Alert.AlertType.INFORMATION,"can not zoom in anymore... if needed i can set up an appointment with an eye doctor?!?!?");
                    alert1.show();                }
                else {
                    int mouse_x= MouseInfo.getPointerInfo().getLocation().x;
                    int mouse_y= MouseInfo.getPointerInfo().getLocation().x;



                    setHeight(getHeight()*1.05);
                    setWidth(getWidth() * 1.05);
                    redraw();
                }
            }
        }
    }


}
