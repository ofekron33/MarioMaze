package View;

import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class  MazeDisplayer extends Canvas {

    private Maze m;
    private int row_player = 1;
    private int col_player = -1;
    private SolDisplayer solDisplayer;
    private double for_zoom =1.05;
  //  public static int d=0;
    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNamePlayer = new SimpleStringProperty();
    StringProperty imageFileNameEnd = new SimpleStringProperty();



    public MazeDisplayer() {
        widthProperty().addListener(e->draw());
        heightProperty().addListener(e->draw());
    }

    public boolean reacedEnd(int row , int col){
        return (row == m.getGoalPosition().getRowIndex()&& col == m.getGoalPosition().getColumnIndex());
    }

    public int getRow_player() {
        return row_player;
    }

    public int getCol_player() {
        return col_player;
    }

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    public StringProperty imageFileNameWallProperty() {
        return imageFileNameWall;
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }

    public StringProperty imageFileNamePlayerProperty() {
        return imageFileNamePlayer;
    }

    public void setImageFileNamePlayer(String imageFileNamePlayer) {
        this.imageFileNamePlayer.set(imageFileNamePlayer);
    }

    public void setImageFileNameCharacter(String imageFileNamePlayer) {
        this.imageFileNamePlayer.set(imageFileNamePlayer);
    }

    public String getImageFileNameEnd() {
        return imageFileNameEnd.get();
    }

    public StringProperty imageFileNameEndProperty() {
        return imageFileNameEnd;
    }

    public void setImageFileNameEnd(String imageFileNameEnd) {
        this.imageFileNameEnd.set(imageFileNameEnd);
    }

    public void set_player_position(int row, int col){
        this.row_player = row;
        this.col_player = col;
        draw();


    }

    public void set_player_position_without_draw(int row, int col){
        this.row_player = row;
        this.col_player = col;

        }


    public void drawMaze(Maze maze)
    {
        this.m = maze;
        draw();
    }

    public void draw()
    {
        if( m!=null)
        {
            if(row_player == -1 && col_player == -1){
                row_player = m.getStartPosition().getRowIndex();
                col_player = m.getStartPosition().getColumnIndex();
            }

            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int row = m.getNumOfRows();
            int col = m.getNumOfCols();
            double cellHeight = canvasHeight/row;
            double cellWidth = canvasWidth/col;
            GraphicsContext graphicsContext = getGraphicsContext2D();
            graphicsContext.clearRect(0,0,canvasWidth,canvasHeight);
            graphicsContext.setFill(Color.CHOCOLATE);
            double w,h;
            int [][] matrix=m.getMatrix();
            //Draw Maze
            Image wallImage = null;
            try {
                wallImage = new Image(new FileInputStream(getImageFileNameWall()));
            } catch (FileNotFoundException e) {
                System.out.println("There is no file....");
            }
            for(int i=0;i<row;i++)
            {
                for(int j=0;j<col;j++)
                {
                    if(matrix[i][j] == 1) // Wall
                    {
                        h = i * cellHeight;
                        w = j * cellWidth;
                        if (wallImage == null){
                            graphicsContext.fillRect(w,h,cellWidth,cellHeight);
                        }else{
                            graphicsContext.drawImage(wallImage,w,h,cellWidth,cellHeight);
                        }
                    }

                }
            }
            {
                double h_player=this.getRow_player() * cellHeight;
                double w_player=this.getCol_player() * cellWidth;
                Image playerImage = null;
                try {
                    playerImage = new Image(new FileInputStream(getImageFileNamePlayer()));
                } catch (FileNotFoundException e) {
                    System.out.println("There is no Image player....");
                }
                graphicsContext.drawImage(playerImage,w_player,h_player,cellWidth,cellHeight);
            }

            double h_end = this.m.getGoalPosition().getRowIndex() * cellHeight;
            double w_end = this.m.getGoalPosition().getColumnIndex() * cellWidth;
            Image endImage = null;
            try {
                endImage = new Image(new FileInputStream(getImageFileNameEnd()));
            } catch (FileNotFoundException e) {
                System.out.println("There is no Image player....");
            }
            graphicsContext.drawImage(endImage,w_end,h_end,cellWidth,cellHeight);
        }
    }
    public Maze getM() {
        return m;
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
                setHeight(getHeight() / for_zoom);
                setWidth(getWidth() / for_zoom);
                draw();
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



                    setHeight(getHeight()*for_zoom);
                    setWidth(getWidth() * for_zoom);
                    draw();
                }
            }
        }
    }

}


