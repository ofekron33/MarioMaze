package Model;

import Client.Client;
import IO.MyDecompressorInputStream;
import Server.Server;
import algorithms.mazeGenerators.IMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.search.AState;
import algorithms.search.BreadthFirstSearch;
import algorithms.search.SearchableMaze;
import algorithms.search.Solution;
import Server.*;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import Client.*;
import IO.MyDecompressorInputStream;
import org.apache.log4j.*;

import sun.util.calendar.LocalGregorianCalendar;
import sun.util.logging.PlatformLogger;

import java.time.LocalDateTime;



public class MyModel extends Observable implements IModel{

    private Maze maze;
    private int rowChar;
    private int colChar;
    private Solution solution;
    Server mazeGeneratingServer;
    Server solveSearchProblemServer;

    public static Logger logger = Logger.getLogger(MyModel.class);

    public MyModel() {
        maze = null;
        rowChar =-1;
        colChar =-1;
        mazeGeneratingServer = new Server(5410, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5411, 1000, new ServerStrategySolveSearchProblem());

        SimpleLayout sl = new SimpleLayout();
        Appender a = null;
        try {
            a = new FileAppender(sl, "Logs/Log.txt");

        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.setLevel(Level.INFO);
        logger.addAppender(a);
        logger.info(String.format("%s - Creating Maze generating Server  ",LocalDateTime.now()));
        logger.info(String.format("%s - Creating Maze solving Server ",LocalDateTime.now()));


        //logger.info("test log");

    }

    public void setPlayer(int row, int col){
        rowChar = row;
        colChar=col;
        //setChanged();
        //notifyObservers();
    }

        @Override
    public void generateMaze(int row, int col) {
        // maze = mazeGenerator.generate(width,height);
            logger.info(String.format("%s - New Maze request!",LocalDateTime.now()));
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5410, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{row, col};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[])((byte[])fromServer.readObject());
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[((row*col)+10000)];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);
                        rowChar = maze.getStartPosition().getRowIndex();
                        colChar = maze.getStartPosition().getColumnIndex();
                        // solutionPath = null;

                       // toServer.flush();
                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }

                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        }
            setChanged();
            notifyObservers(1);

    }
//    @Override
//    public void generateMaze(int row, int col) {
//        IMazeGenerator g = new MyMazeGenerator();
//        this.maze=g.generate(row,col);
//        this.rowChar=this.maze.getStartPosition().getRowIndex();
//        this.colChar=this.maze.getStartPosition().getColumnIndex();
//        setChanged();
//        notifyObservers();
//
//    }

    public void startServers() {
        solveSearchProblemServer.start();
        logger.info(String.format("%s - Starting Maze generating Server  ",LocalDateTime.now()));
        mazeGeneratingServer.start();
        logger.info(String.format("%s - Starting Maze solving Server  ",LocalDateTime.now()));
    }

    @Override
    public void saveMaze(File file) {
        try {
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
            byte[] maze_in_bytes = maze.toByteArray();
            byte[] bytes_arr = new byte[maze.toByteArray().length+4];
            bytes_arr[0]=(byte)(rowChar%256);
            bytes_arr[1]=(byte)(rowChar/256);
            bytes_arr[2]=(byte)(colChar%256);
            bytes_arr[3]=(byte)(colChar/256);
            int i=4;
            int maze_byte_index=0;
            while(i<bytes_arr.length)
            {
            bytes_arr[i]=maze_in_bytes[maze_byte_index];
            i++;
            maze_byte_index++;
            }
            out.writeObject(bytes_arr);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(String.format("%s - Couldn't save maze ",LocalDateTime.now()));
        }
        logger.info(String.format("%s - Maze saved ",LocalDateTime.now()));
    }

    @Override
    public boolean LoadMaze(File f) {

        try {
            InputStream inputStream=new FileInputStream(f);
            ObjectInputStream input = new ObjectInputStream(inputStream);
            byte [] kelet= (byte[]) input.readObject();
            rowChar=(kelet[1]*256 + kelet[0]);
            colChar=(kelet[3]*256 + kelet[2]);
            int i=4;
            int maze_byte_index=0;
            byte[] maze_in_bytes=new byte[kelet.length-4];
            while(i<kelet.length)
            {
                maze_in_bytes[maze_byte_index]=kelet[i];
                i++;
                maze_byte_index++;
            }
            Maze maze_tmp=new Maze(maze_in_bytes);
            this.maze=maze_tmp;

        } catch (IOException | ClassNotFoundException e) {
            logger.info(String.format("%s - Couldn't load maze ",LocalDateTime.now()));
            return false;
            //e.printStackTrace();
        }
        setChanged();
        notifyObservers(4);
        logger.info(String.format("%s - Maze Loaded ",LocalDateTime.now()));
        return true;
    }

    @Override
    public void CloseApp() {
        mazeGeneratingServer.stop();
        logger.info(String.format("%s - Maze generating server stopped ",LocalDateTime.now()));
        solveSearchProblemServer.stop();
        logger.info(String.format("%s - Maze solving server stopped ",LocalDateTime.now()));
    }


    public void updateCharacterLocation(int direction) {
        if (direction == -1) {
            setChanged();
            notifyObservers(5);//wrong key for movement;
        } else {
            if(rowChar==-1 && colChar==-1){
                rowChar=maze.getStartPosition().getRowIndex();
                colChar=maze.getGoalPosition().getColumnIndex();
            }
            int[][] matrix = maze.getMatrix();
        /*
            direction = 1 -> lower-left diagonal
            direction = 2 -> Down
            direction = 3 -> lower-right diagonal
            direction = 4 -> left
            direction = 6 -> Right
            direction = 7 -> upper-left diagonal
            direction = 8 -> up
            direction = 9 -> upper-right diagonal
         */

            switch (direction) {
                case 1: //lower-left diagonal
                    if (colChar - 1 >= 0 && (rowChar + 1 < this.maze.getNumOfRows())) //not stepping outside board
                    {
                        if (matrix[rowChar + 1][colChar - 1] == 0) //diagonal is not 1
                        {
                            if (matrix[rowChar + 1][colChar] == 0 || matrix[rowChar][colChar - 1] == 0) //clear diagonal path
                            {
                                rowChar++;
                                colChar--;
                            }
                        }
                    }
                    break;
                case 2: //Down
                    if (rowChar + 1 < this.maze.getNumOfRows() && matrix[rowChar + 1][colChar] == 0) {
                        rowChar++;
                    }
                    break;
                case 3: //lower-right diagonal
                    if (colChar + 1 < this.maze.getNumOfCols() && (rowChar + 1 < this.maze.getNumOfRows())) //not stepping outside board
                    {
                        if (matrix[rowChar + 1][colChar + 1] == 0) //diagonal is not 1
                        {
                            if (matrix[rowChar + 1][colChar] == 0 || matrix[rowChar][colChar + 1] == 0) //clear diagonal path
                            {
                                rowChar++;
                                colChar++;
                            }
                        }
                    }
                    break;
                case 4: //left
                    if (colChar - 1 >= 0 && matrix[rowChar][colChar - 1] == 0) {
                        colChar--;
                    }
                    break;
                case 6: //Right
                    if (colChar + 1 < this.maze.getNumOfCols() && matrix[rowChar][colChar + 1] == 0) {
                        colChar++;
                    }
                    break;
                case 7: //upper-left diagonal
                    if (colChar - 1 >= 0 && rowChar - 1 >= 0) //not stepping outside board
                    {
                        if (matrix[rowChar - 1][colChar - 1] == 0) //diagonal is not 1
                        {
                            if (matrix[rowChar - 1][colChar] == 0 || matrix[rowChar][colChar - 1] == 0) //clear diagonal path
                            {
                                colChar--;
                                rowChar--;
                            }
                        }
                    }
                    break;
                case 8: //up
                    if (rowChar - 1 >= 0 && matrix[rowChar - 1][colChar] == 0) {
                        rowChar--;
                    }
                    break;
                case 9: // upper-right diagonal
                    if (rowChar - 1 >= 0 && colChar + 1 < this.maze.getNumOfCols()) //not stepping outside board
                    {
                        if (matrix[rowChar - 1][colChar + 1] == 0) //diagonal is not 1
                        {
                            if (matrix[rowChar - 1][colChar] == 0 || matrix[rowChar][colChar + 1] == 0) //clear diagonal path
                            {
                                rowChar--;
                                colChar++;
                            }
                        }
                    }
                    break;
            }
            setChanged();
            notifyObservers(2);
        }
    }

    public int getRowChar() {
        return rowChar;
    }

    public int getColChar() {
        return colChar;
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }

    @Override
    public void solveMaze(Maze maze){
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5411, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze);
                        toServer.flush();
                        Solution mazeSolution = (Solution)fromServer.readObject();
                        solution=mazeSolution;
                    } catch (Exception var11) {
                        var11.printStackTrace();
                    }

                }
            });
            logger.info(String.format("%s - Requesting Maze solution ",LocalDateTime.now()));
            client.communicateWithServer();
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
            logger.info(String.format("%s - Couldn't ",LocalDateTime.now()));
        }
        setChanged();
        notifyObservers(3);

    }


    @Override
    public Solution getSolution() {
        return this.solution;
    }


    public Maze getMaze() {
        return maze;
    }
}
