package byow.Core;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import byow.TileEngine.TERenderer;
import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;

public class Interactions {
    private MapGenerator world;
    private MapGenerator originalWorld;
    private String curr = "";
    private TERenderer ter;
    private FileWriter writer;
    private boolean replayStatus;
    private boolean quit;
    private boolean load;
    private boolean inputString;
    private String avatarName;
    private int x_mouse;
    private int y_mouse;
    public Interactions(MapGenerator world, boolean load, String avatarName) {
        this.inputString = false;
        this.originalWorld = world;
        if (avatarName == null) {
            this.avatarName = "Hero";
        } else {
            this.avatarName = avatarName;
        }
        this.load = load;
        this.world = world;
        ter = new TERenderer();
        StdDraw.enableDoubleBuffering();
        try {
            this.writer = new FileWriter("savedGame.txt");
            writer.write(world.getSeed() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void displayName() {
        StdDraw.clear(Color.BLACK);
        StdDraw.text(world.getWidth() / 2, world.getHeight() / 2 + 5, "Enter Name:");
        StdDraw.text(world.getWidth() / 2, world.getHeight() / 2 + 1, "(Press ] when done)");
        StdDraw.show();
    }
    public void filter(String typed) {
        String convert = typed;
        convert = convert.toLowerCase();

        try {
            if (!replayStatus && !(convert.equals("p"))) {
                writer.append(convert);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (convert.equals(":")) {
            quit = true;
        }
        if (convert.equals("p")) {
            displayName();
            String name = "";
            char typer;
            while (true) {
                if (StdDraw.hasNextKeyTyped()) {
                    typer = StdDraw.nextKeyTyped();
                    if (typer == ']') {
                        break;
                    }
                    name += "" + typer;
                    displayName();
                    StdDraw.text(world.getWidth() / 2, world.getHeight() / 2 + 3, name);
                    StdDraw.show();
                }
            }
            avatarName = name;
            if (!load) {
                ter.renderFrame(world.getWorld());
                hudDisplay("");
            }
        }
        if (convert.equals("r")) {
            replayStatus = true;
            In in = new In("oldSave.txt");
            String moves = in.readLine();
            moves = in.readLine();

            if (!(moves == null)) {
                try {
                    this.writer = new FileWriter("savedGame.txt");
                    writer.write(world.getSeed() + "\n");
                    moves = moves.substring(0, moves.length() - 2);
                    writer.append(moves);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                world = new MapGenerator(world.getSeed());
                ter.renderFrame(world.getWorld());
                for (int i = 0; i < moves.length(); i++) {
                    String move = "" + moves.charAt(i);
                    if (!move.equals("r") && !move.equals(":") && !move.equals("q")) {
                        if (!load) {
                            StdDraw.pause(200);
                        }
                        filter(move);

                    }

                }
            }
            replayStatus = false;
        }
        if (convert.equals("q") && quit) {
            try {
                writer.write("\n" + avatarName);
                writer.close();
                if (!inputString) {
                    System.exit(1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (convert.equals("w")  || convert.equals("a") || convert.equals("s")
                || convert.equals("d")) {
            quit = false;
            move(convert);
        }
    }
    public void loadingFinished() {
        load = false;
    }
    public void move(String letter) {
        if (letter.equals("w")) {
            world.move("UP");
        }
        if (letter.equals("a")) {
            world.move("LEFT");
        }
        if (letter.equals("d")) {
            world.move("RIGHT");
        }
        if (letter.equals("s")) {
            world.move("DOWN");
        }
        if (!load) {
            ter.renderFrame(world.getWorld());
            hudDisplay(curr);
        }
    }

    public void seedSelection() {
        // N
    }

    public void mouseDisplayHUD() {
        x_mouse = (int) StdDraw.mouseX();
        y_mouse = (int) StdDraw.mouseY();
        if (x_mouse < world.getWidth() && y_mouse < world.getHeight()) {
            TETile tile = world.getType(x_mouse, y_mouse);
            if (tile.equals(Tileset.FLOOR) && !curr.equals("FLOOR")) {
                curr = "FLOOR";
                hudDisplay("FLOOR");
            }
            if (tile.equals(Tileset.WALL) && !curr.equals("WALL")) {
                curr = "WALL";
                hudDisplay("WALL");
            }
            if (tile.equals(Tileset.GRASS) && !curr.equals("GRASS")) {
                curr = "GRASS";
                hudDisplay("GRASS");

            }
            if (tile.equals(Tileset.LOCKED_DOOR) && !curr.equals("DOOR")) {
                curr = "DOOR";
                hudDisplay("DOOR");

            }
            if (tile.equals(Tileset.AVATAR) && !curr.equals("AVATAR")) {
                curr = "AVATAR";
                hudDisplay("AVATAR");
            }
            if (tile.equals(Tileset.NOTHING) && !curr.equals("NOTHING")) {
                curr = "NOTHING";
                hudDisplay("");
            }

        }

    }
    public TETile[][] getWorld() {
        return world.getWorld();
    }
    public void inputString() {
        inputString = true;
    }
    public void hudDisplay(String s) {
        if (curr.equals("NOTHING")) {
            s = "";
        }
        String points = Integer.toString(world.getScore());
        StdDraw.clear(Color.BLACK);
        ter.renderFrame(world.getWorld());
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.textLeft(0.1, world.getHeight() - 0.5, s);
        StdDraw.textRight(world.getWidth() - 0.1,  world.getHeight() - 0.5, avatarName);
        StdDraw.text(world.getWidth() / 2, world.getHeight() - 0.5, "R : Replay    P : Change name");
        StdDraw.text(world.getWidth() / 4, world.getHeight() - 0.5, "Grass Touched : " + points);
        StdDraw.show();

    }



}
