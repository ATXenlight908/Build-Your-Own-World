package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;


public class MapGenerator {
    private int[] originalPos;
    private TETile standing;
    private int load;
    private static Random random;
    private int width = 80;
    private int height = 40 - 2;
    private ArrayList<int[]> walls;
    private static final long SEED = 1381927391;
    private int[] avatarPos;
    private TETile[][] map;
    private String direction;
    private long seed;
    private int score = 0;

    /**
     - Create a new ArrayList of wall cords
     - Initiate the world with blank tiles and random generators
     - Create initial room on the third quadrant
     - Repeatedly generate halls and rooms until it reaches the random upper bond or
     hit the load factor
     */
    public MapGenerator(long seed) {
        walls = new ArrayList<>();
        this.seed = seed;
        random = new Random(seed);
        this.map = new TETile[width][height];
        standing = Tileset.FLOOR;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = Tileset.NOTHING;
            }
        }
        int x = random.nextInt(width - 10);
        int y = random.nextInt(height - 10);
        int roomHeight = random.nextInt(5) + 5;
        int roomWidth = random.nextInt(5) + 5;
        createRoom(x, y, roomWidth, roomHeight);
        map[x + 2][y + 2] = Tileset.AVATAR;
        avatarPos = new int[] {x + 2, y + 2};
        originalPos = new int[] {x + 2, y + 2};
        int times = random.nextInt(15) + 25;
        int index = 0;
        int xHall = 0;
        int yHall = 0;
        int length = 0;
        for (int i = 0; i < times; i++) {
            if (load < (this.width * this.height) * 0.75) {
                index = random.nextInt(walls.size());
                xHall = walls.get(index)[0];
                yHall = walls.get(index)[1];
                length = random.nextInt(10) + 5;
                while (!validHallway(xHall, yHall, length)) {
                    index = random.nextInt(walls.size());
                    xHall = walls.get(index)[0];
                    yHall = walls.get(index)[1];
                }
                createRoomWithHall(xHall, yHall, length);
                walls.remove(index);
            }
        }
        for (int i = 0; i < 3; i++) {
            doorAdder();
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (map[i][j] == Tileset.NOTHING) {
                    int grassOrNoGrass = random.nextInt(4);
                    if (grassOrNoGrass == 1) {
                        map[i][j] = Tileset.GRASS;
                    }
                }
            }
        }
    }

    public void doorAdder() {
        int portal = random.nextInt(walls.size());
        int xPortal = walls.get(portal)[0];
        int yPortal = walls.get(portal)[1];
        int countNo = 0;
        if (xPortal < height - 1 && map[xPortal + 1][yPortal] == Tileset.NOTHING) {
            countNo += 1;
        }
        if (xPortal > 0 && map[xPortal - 1 ][yPortal] == Tileset.NOTHING) {
            countNo += 1;
        }
        if  (yPortal > 0 && map[xPortal][yPortal - 1] == Tileset.NOTHING) {
            countNo += 1;
        }
        if (yPortal < height - 1 && map[xPortal][yPortal + 1] == Tileset.NOTHING) {
            countNo += 1;
        }

        while (!(map[xPortal][yPortal] == Tileset.WALL && countNo == 1)) {
            portal = random.nextInt(walls.size());
            xPortal = walls.get(portal)[0];
            yPortal = walls.get(portal)[1];
            countNo = 0;
            if (yPortal < height - 1 && map[xPortal][yPortal + 1] == Tileset.NOTHING) {
                countNo += 1;
            }
            if (xPortal > 0 && map[xPortal - 1 ][yPortal] == Tileset.NOTHING) {
                countNo += 1;
            }
            if  (yPortal > 0 && map[xPortal][yPortal - 1] == Tileset.NOTHING) {
                countNo += 1;
            }
            if (xPortal < height - 1 && map[xPortal + 1][yPortal] == Tileset.NOTHING) {
                countNo += 1;
            }
        }
        walls.remove(portal);
        map[xPortal][yPortal] = Tileset.LOCKED_DOOR;
    }


    public TETile[][] getWorld() {
        /**
         - Return the world map
         */
        return map;
    }
    public void createRoomWithHall(int x, int y, int length) {
        /**
         - Randomly generate room dimensions
         - Check if a room of this size can be successfully generated with the given "direction"
         and the starting coordinates (the cords of the end of a hallway that was just constructed)
         - If so create a room, and unseal the hallway to connect the room with the hall
         */

        int roomHeight = random.nextInt(8) + 5;
        int roomWidth = random.nextInt(8) + 5;
        createHallway(x, y, length);
        if (direction.equals("right")) {
            if (validRoom(x + length, y - (roomHeight / 2), roomWidth, roomHeight)) {
                createRoom(x + length, y - (roomHeight / 2), roomWidth, roomHeight);
                map[x + length][y] = Tileset.FLOOR;
            }
        }
        if (direction.equals("left")) {
           if (validRoom(x - length - roomWidth, y - (roomHeight / 2), roomWidth, roomHeight)) {
               createRoom(x - length - roomWidth, y - (roomHeight / 2), roomWidth, roomHeight);
               map[x - length][y] = Tileset.FLOOR;
               map[x - length - 1][y] = Tileset.FLOOR;
           }
        }
        if (direction.equals("down")) {
            if (validRoom(x - (roomWidth / 2), y - length - roomHeight, roomWidth, roomHeight)) {
                createRoom(x - (roomWidth / 2), y - length - roomHeight, roomWidth, roomHeight);
                map[x][y - length] = Tileset.FLOOR;
                map[x][y - length - 1] = Tileset.FLOOR;
            }
        }
        if (direction.equals("up")) {
            if (validRoom(x - (roomWidth / 2), y + length, roomWidth, roomHeight)) {
                createRoom(x - (roomWidth / 2), y + length, roomWidth, roomHeight);
                map[x][y + length] = Tileset.FLOOR;
            }
        }
    }
    public void createRoom(int x, int y, int roomWidth, int roomHeight) {
        /**
         - Create the rectangular room based on the given starting cords and dimensions
         (Passed in the constructor during the first call)
         (Passed in during the createRoomWithHall method during subsequent calls)
         - Add the non-corner wall cords to the ArrayList of Walls
         - Add to the load factor for tiles created
         */
        for (int i = 0; i < roomWidth; i++) {
            for (int j = 0; j < roomHeight; j++) {
                if (i == 0 || j == 0 || i == roomWidth - 1 || j == roomHeight - 1) {
                    map[x + i][y + j] = Tileset.WALL;
                    load += 1;
                    boolean first = j == 0 && i == 0;
                    boolean second = j == 0 && i == roomWidth - 1;
                    boolean third = j == roomHeight - 1 && i == 0;
                    boolean fourth = j == roomHeight - 1 && i == roomWidth - 1;
                    if (!first && !second && !third && !fourth) {
                        walls.add(new int[]{ x + i, y + j});
//                            map[x+i][y+j] = Tileset.LOCKED_DOOR;
                    }
                } else {
                    map[x + i][y + j] = Tileset.FLOOR;
                    load += 1;
                }
            }
        }
    }
//        public void createRoomAfterHallway(int x, int y) {
//            int roomHeight = random.nextInt(8) + 3;
//            int roomWidth = random.nextInt(8) + 3;
//            for (int i = 0; i < roomWidth;i++) {
//                for (int j = 0; j < roomHeight; j++) {
//                    if (i == 0 || j == 0 || i == roomWidth - 1 || j == roomHeight - 1) {
//                        map[x + i][y + j] = Tileset.WALL;
//                        boolean first = j == 0 && i == 0;
//                        boolean second = j == 0 && i == roomWidth -1;
//                        boolean third = j == roomHeight -1 && i == 0;
//                        boolean fourth = j == roomHeight - 1 && i == roomWidth - 1;
//                        if (!first && !second && !third && !fourth) {
//
//                            walls.add(new int[]{ x + i, y + j} );
//                        }
//                    } else {
//                        map[x + i][y + j] = Tileset.FLOOR;
//                    }
//                }
//            }
//        }

    /**
     - Select the direction of the hallway based on which direction is available for expansion
     - Then create the hallway and add walls to the ArrayList of walls coordinates
     - Then finally Seal off the hallway and set global variable "direction" to the direction
     that the hallway is constructed
     - Add to load factor during process
     */
    public void createHallway(int x, int y, int length) {
        if (map[x + 1][y] == Tileset.NOTHING) {
            for (int i = 0; i < length; i++) {
                map[x + i][y] = Tileset.FLOOR;
                map[x + i][y + 1] = Tileset.WALL;
                walls.add(new int[]{ x + i, y + 1});
                map[x + i][y - 1] = Tileset.WALL;
                walls.add(new int[]{ x + i, y - 1});
                load += 3;
            }
            if (map[x + length][y + 1] != Tileset.FLOOR) {
                map[x + length][y + 1] = Tileset.WALL;
                load += 1;
            }
            if (map[x + length][y - 1] != Tileset.FLOOR) {
                map[x + length][y - 1] = Tileset.WALL;
                load += 1;
            }
            if (map[x + length][y] != Tileset.FLOOR) {
                map[x + length][y] = Tileset.WALL;
                load += 1;
            }
            direction = "right";
        }
        if (map[x - 1][y] == Tileset.NOTHING) {
            for (int i = 0; i < length; i++) {
                map[x - i][y] = Tileset.FLOOR;
                map[x - i][y + 1] = Tileset.WALL;
                walls.add(new int[]{ x - i, y + 1});
                map[x - i][y - 1] = Tileset.WALL;
                walls.add(new int[]{ x - i, y - 1});
                load += 3;
            }
            if (map[x - length][y + 1] != Tileset.FLOOR) {
                map[x - length][y + 1] = Tileset.WALL;
                load += 1;
            }
            if (map[x - length][y - 1] != Tileset.FLOOR) {
                map[x - length][y - 1] = Tileset.WALL;
                load += 1;
            }
            if (map[x - length][y] != Tileset.FLOOR) {
                map[x - length][y] = Tileset.WALL;
                load += 1;
            }
            direction = "left";
        }
        if (map[x][y + 1] == Tileset.NOTHING) {
            for (int i = 0; i < length; i++) {
                map[x][y + i] = Tileset.FLOOR;
                map[x + 1][y + i] = Tileset.WALL;
                walls.add(new int[]{ x + 1, y + i});
                map[x - 1][y + i] = Tileset.WALL;
                walls.add(new int[]{ x - 1, y + i});
                load += 3;
            }
            if (map[x + 1][y + length] != Tileset.FLOOR) {
                map[x + 1][y + length] = Tileset.WALL;
                load += 1;
            }
            if (map[x - 1][y + length] != Tileset.FLOOR) {
                map[x - 1][y + length] = Tileset.WALL;
                load += 1;
            }
            if (map[x][y + length] != Tileset.FLOOR) {
                map[x][y + length] = Tileset.WALL;
                load += 1;
            }
            direction = "up";
        }
        if (map[x][y - 1] == Tileset.NOTHING) {
            for (int i = 0; i < length; i++) {
                map[x][y - i] = Tileset.FLOOR;
                map[x - 1][y - i] = Tileset.WALL;
                walls.add(new int[]{ x - 1, y - i});
                map[x + 1][y - i] = Tileset.WALL;
                walls.add(new int[]{ x + 1, y - i});
                load += 3;
            }
            if (map[x + 1][y - length] != Tileset.FLOOR) {
                map[x + 1][y - length] = Tileset.WALL;
                load += 1;
            }
            if (map[x - 1][y - length] != Tileset.FLOOR) {
                map[x - 1][y - length] = Tileset.WALL;
                load += 1;
            }
            if (map[x][y - length] != Tileset.FLOOR) {
                map[x][y - length] = Tileset.WALL;
                load += 1;
            }
            direction = "down";
        }
    }

    public TETile getStanding() {
        return standing;
    }

    public boolean validHallway(int x, int y, int length) { // Fix checking up and down.
        /**
         - Check for out of bound cases and if the first tile of expansion is empty
         - Then check if the hallway fits in the direction (will be on a call loop in constructor
         if false until true so a new hall can be created)
         */
        if (x + length < width && map[x + 1][y] == Tileset.NOTHING
                && y - 1 > 0 && y + 1 != height) {
            for (int i = 0; i < length; i++) {
                if (map[x + i][y] == Tileset.FLOOR || map[x + i][y + 1]
                        == Tileset.FLOOR  || map[x + i][y - 1] == Tileset.FLOOR) {
                    return false;
                }
            }
            return true;
        }
        if (x - length > 0 && map[x - 1][y] == Tileset.NOTHING && y - 1 > 0 && y + 1 != height) {
            for (int i = 0; i < length; i++) {
                boolean first = map[x - i][y] == Tileset.FLOOR;
                boolean second = map[x - i][y + 1] == Tileset.FLOOR;
                boolean third = map[x - i][y - 1] == Tileset.FLOOR;
                if (first || second  || third) {
                    return false;
                }
            }
            return true;
        }
        if (y + length < height && map[x][y + 1] == Tileset.NOTHING) {
            for (int i = 0; i < length; i++) {
                if (map[x][y + i] == Tileset.FLOOR || map[x + 1][y + i] == Tileset.FLOOR
                        || map[x - 1][y + i] == Tileset.FLOOR) {
                    return false;
                }
            }
            return true;
        }
        if (y - length > 0 && map[x][y - 1] == Tileset.NOTHING) {
            for (int i = 0; i < length; i++) {
                if (map[x][y - i] == Tileset.FLOOR || map[x + 1][y - i]
                        == Tileset.FLOOR  || map[x - 1][y - i] == Tileset.FLOOR) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    public boolean validRoom(int x, int y, int width, int height) {
        /**
         - Checking for out of bounds cases (unlike validHall, this is not in a call loop, [aka.
         if a room cannot be created at the end of a hall then we don't generate room, and we
         move back to the validHall call loop in the constructor and generate new halls)
         - Return true if there are sufficient space to generate a room
         */
        if (x + width >= this.width || y + height >= this.height || x < 0 || y < 0) {
            return false;
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (map[x + i][y + j] == Tileset.FLOOR) {
                    return false;
                }
            }
        }
        return true;
    }
    public TETile getType(int x, int y) {
        if (x < width && y < height) {
            return map[x][y];
        } else {
            return Tileset.NOTHING;
        }
    }
    public int[] getAvatarPos() {
        return avatarPos;
    }

    public int getScore() {
        return score;
    }
    public int getHeight() {
        return 40;
    }
    public int getWidth() {
        return 80;
    }

    public void move(String s) {
        if (s.equals("LEFT")) {
            if (!(avatarPos[0] == 0 || map[avatarPos[0] - 1][avatarPos[1]] == Tileset.WALL)) {
                map[avatarPos[0]][avatarPos[1]] = standing;
                if (map[avatarPos[0] - 1][avatarPos[1]] == Tileset.GRASS) {
                    standing = Tileset.NOTHING;
                    score += 1;
                } else {
                    standing = map[avatarPos[0] - 1][avatarPos[1]];
                }
                avatarPos = new int[]{avatarPos[0] - 1, avatarPos[1]};
                map[avatarPos[0]][avatarPos[1]] = Tileset.AVATAR;
            }
        }
        if (s.equals("RIGHT")) {
            if (!(avatarPos[0] == width - 1 || map[avatarPos[0] + 1][avatarPos[1]]
                    == Tileset.WALL)) {
                map[avatarPos[0]][avatarPos[1]] = standing;
                if (map[avatarPos[0] + 1][avatarPos[1]] == Tileset.GRASS) {
                    standing = Tileset.NOTHING;
                    score += 1;
                } else {
                    standing = map[avatarPos[0] + 1][avatarPos[1]];
                }
                    avatarPos = new int[]{avatarPos[0] + 1, avatarPos[1]};
                    map[avatarPos[0]][avatarPos[1]] = Tileset.AVATAR;
            }
        }
        if (s.equals("UP")) {
            if (!(avatarPos[1] == height - 1 || map[avatarPos[0]][avatarPos[1] + 1]
                    == Tileset.WALL)) {
                map[avatarPos[0]][avatarPos[1]] = standing;
                if (map[avatarPos[0]][avatarPos[1] + 1] == Tileset.GRASS) {
                    standing = Tileset.NOTHING;
                    score += 1;
                } else {
                    standing = map[avatarPos[0]][avatarPos[1] + 1];
                }
                avatarPos = new int[] {avatarPos[0], avatarPos[1] + 1};
                map[avatarPos[0]][avatarPos[1]] = Tileset.AVATAR;
            }
        }
        if (s.equals("DOWN")) {
            if (!(avatarPos[1] == 0 || map[avatarPos[0]][avatarPos[1] - 1] == Tileset.WALL)) {
                map[avatarPos[0]][avatarPos[1]] = standing;
                if (map[avatarPos[0]][avatarPos[1] - 1] == Tileset.GRASS) {
                    standing = Tileset.NOTHING;
                    score += 1;
                } else {
                    standing = map[avatarPos[0]][avatarPos[1] - 1];
                }
                avatarPos = new int[] {avatarPos[0], avatarPos[1] - 1 };
                map[avatarPos[0]][avatarPos[1]] = Tileset.AVATAR;
            }
        }
    }

    public long getSeed() {
        return seed;
    }
    public void reset() {
        map[avatarPos[0]][avatarPos[1]] = standing;
        standing = map[originalPos[0]][originalPos[1]];
        avatarPos = originalPos;
        map[avatarPos[0]][avatarPos[1]] = Tileset.AVATAR;
    }

    public static void main(String[] args) {
        MapGenerator world = new MapGenerator(13213132);

        TERenderer ter = new TERenderer();
        ter.initialize(world.width, world.height);
        ter.renderFrame(world.getWorld());
    }
}
