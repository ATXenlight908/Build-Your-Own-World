package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.io.FileWriter;
import java.io.IOException;

public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    private MapGenerator loadedWorld;

    private boolean inputString;
    private String avatarName;
    private final int BIGFONT = 30;
    private final int SMALLFONT = 20;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        String result = "";
        boolean startSeed = false;
        long seed = 0;
        try {
            FileWriter oldWriter = new FileWriter("oldSave.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, BIGFONT);
        Font fontsmall = new Font("Monaco", Font.BOLD, SMALLFONT);
        StdDraw.setFont(fontBig);

        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 7, "CS61B: THE GAME");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 1, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 1, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 3, "Quit (Q)");
        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                String type = "" + StdDraw.nextKeyTyped();
                if (type.toLowerCase().equals("n")) {
                    displaySeed();
                    String number = "";
                    char typer;
                    String convert;
                    while (true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            typer = StdDraw.nextKeyTyped();
                            convert = "" + typer;
                            if (convert.toLowerCase().equals("s")) {
                                break;
                            }
                            number += convert;
                            displaySeed();
                            StdDraw.text(WIDTH / 2, HEIGHT / 2 + 1, number);
                            StdDraw.show();
                        }
                    }
                    seed = Long.parseLong(number);

                    break;
                }
                if (type.toLowerCase().equals("q")) {
                    System.exit(1);
                }
                if (type.toLowerCase().equals("l")) {
                    load();
                }
            }
        }

        MapGenerator world = new MapGenerator(seed);
        Interactions interact = new Interactions(world, false, avatarName);

        TERenderer ter = new TERenderer();
        ter.initialize(world.getWidth(), world.getHeight());
        ter.renderFrame(world.getWorld());
        while (!result.endsWith(":Q")) {

            if (StdDraw.hasNextKeyTyped()) {
                String nextType = "" + StdDraw.nextKeyTyped();
                interact.filter(nextType);
                result = result + nextType;
            }
            interact.mouseDisplayHUD();

        }
    }
    public void displaySeed() {
        Font fontBig = new Font("Monaco", Font.BOLD, BIGFONT);
        Font fontsmall = new Font("Monaco", Font.BOLD, SMALLFONT);
        StdDraw.clear(Color.BLACK);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 7, "Enter Seed:");
        StdDraw.setFont(fontsmall);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 5, "(when done press 'S')");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 3, "Seed:");
        StdDraw.show();
    }
    public void load() {
        In in = new In("savedGame.txt");
        String stringSeed = in.readLine();
        if (stringSeed == null) {
            System.exit(0);
            return;
        }
        String keyPress = in.readLine();
        String newAvatarName = in.readLine();
        avatarName = newAvatarName;
        try {
            FileWriter oldWriter = new FileWriter("oldSave.txt");
            oldWriter.append(stringSeed + "\n");
            oldWriter.append(keyPress);
            oldWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long seed = Long.parseLong(stringSeed);
        MapGenerator world = new MapGenerator(seed);
        loadedWorld = world;
        Interactions interact = new Interactions(world, true, avatarName);


        for (int i = 0; i < keyPress.length() - 2; i++) {
            char typed = keyPress.charAt(i);
            interact.filter("" + typed);
        }
        TERenderer tera = new TERenderer();
        tera.initialize(world.getWidth(), world.getHeight());
        tera.renderFrame(world.getWorld());
        interact.loadingFinished();
        while (!inputString) {
            if (StdDraw.hasNextKeyTyped()) {
                String nextType = "" + StdDraw.nextKeyTyped();
                interact.filter(nextType);
            }
            interact.mouseDisplayHUD();
        }
    }
    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */

    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        input = input.toLowerCase();
        inputString = true;
        MapGenerator world;
        Interactions interact;
        int index = 0;
        String typed = "" + input.charAt(index);
        String number = "";
        if (input.startsWith("n")) {
            index += 1;
            while (true) {
                typed = "" + input.charAt(index);
                if (typed.equals("s")) {
                    break;
                }
                number += typed;
                index += 1;

            }
            world = new MapGenerator(Long.parseLong(number));
            interact = new Interactions(world, false, avatarName);
        } else {
            if (input.startsWith("l")) {
                index += 1;
                load();
            }
            world = loadedWorld;
            interact = new Interactions(world, true, avatarName);
        }
        interact.inputString();
        for (int i = index; i < input.length(); i++) {
            typed = "" + input.charAt(i);
            if (typed.equals("l")) {
                load();
            } else {
                interact.filter(typed);
            }
        }
        TETile[][] finalWorldFrame = world.getWorld();
        return finalWorldFrame;
    }
}
