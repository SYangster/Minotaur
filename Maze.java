import java.util.*;
import java.io.File;
import java.io.IOException;

public class Maze {

    public String FLOOR=".";
    public String WALL="@";
    public String COIN="c";
    public String DOUBLE_COIN="d";
    public String INVIS_POTION="i";
    public String TELEPORT_POTION="t";
    public String FREEZE_POTION="f";

    public int RIGHT=1;
    public int DOWN=2;
    public int LEFT=3;
    public int UP=4;

    public ArrayList<String> maps = new ArrayList<String>();
    public String[][] map2;
    public int height, width;

    public int score = 0;
    public int invisPotions = 0;
    public int invisTime = 0;
    public int freezePotions = 0;
    public int freezeTime = 0;
    public int teleports = 3;
    public int moveDistance = 1;
    public boolean isTeleporting = false;
    public boolean gameover = false;
    public int coin = 1;
    public int doublecoincounter = 200;

    public int distance = 1;

    public Position player;
    public Position fakeplayer;
    public String prevLocation;



    public Position[] enemies = new Position[2];





    public void readMap() {
        Scanner in;
        String piece = "";
        boolean divider = false;
        try {
            in  = new Scanner(new File("mapDesigns.txt"));
            while(in.hasNextLine()) {
                piece = "";
                divider = false;
                while(!divider) {
                    String input = in.nextLine();

                    if (in.hasNextLine()) {
                        if (!input.trim().isEmpty()) {
                            piece += (input + ",");
                        } else if (input.trim().isEmpty()) {
                            divider = true;
                            if (in.hasNextLine()) {
                                maps.add(piece);
                                String temp1 = in.nextLine();
                                String temp2 = in.nextLine();
                            } else {
                                maps.add(piece);
                                break;
                            }
                        }
                    } else {
                        piece += input;
                        maps.add(piece);
                        break;
                    }
                }
            }
        } catch (IOException i) {
            System.out.println("Error: " + i.getMessage());
        }
    }

    public String[] appendHorizontal(String[] map, int count) {
        if (count == 0) {
            return map;
        } else {
            ArrayList<String> rows = new ArrayList<String>();
            Random rand = new Random();
            int randX = rand.nextInt(maps.size()-1);

            String[] map2 = maps.get(randX).split(",");
            for (int y = 0; y < map.length; y++) {
                String row = map[y] + map2[y];
                rows.add(row);
            }

            return appendHorizontal(rows.toArray(new String[rows.size()]), count - 1);
        }
    }

    public void initializeModel() {
        this.score = 0;
        this.invisPotions = 0;
        this.invisTime = 0;
        this.freezePotions = 0;
        this.freezeTime = 0;
        this.teleports = 3;
        this.moveDistance = 1;
        this.isTeleporting = false;
        this.gameover = false;
        this.coin = 1;
        this.doublecoincounter = 200;

        this.distance = 1;


        ArrayList<String[]> blockRows = new ArrayList<String[]>();
        for (int y = 0; y < 3; y++) {
            Random rand = new Random();
            int randY = rand.nextInt(maps.size()-1);
            //String[] tempArray = (appendHorizontal(maps.get(randY).split(","), 4));
            blockRows.add(appendHorizontal(maps.get(randY).split(","), 4));
        }

        ArrayList<String> rows = new ArrayList<String>();

        String edge = "";
        for (int i = 0; i < blockRows.get(0)[0].length() + 2; i++) {
            edge += (WALL);
        }
        rows.add(edge);
        //rows.add(new String(new char[(blockRows.get(0)[0]).length()]).replace("\0", WALL) + WALL + WALL);
        //rows.append(WALL*(blockRows.get(0)[0]).length() + WALL*2);
        for (String[] row: blockRows) {
            for (String r: row) {
                rows.add(WALL + r + WALL);
            }
        }
        //rows.append(WALL*(blockRows.get(0)[0]).length() + WALL*2);

        edge = "";
        for (int i = 0; i < blockRows.get(0)[0].length() + 2; i++) {
            edge += (WALL);
        }
        rows.add(edge);
        //rows.add(new String(new char[(blockRows.get(0)[0]).length()]).replace("\0", WALL) + WALL + WALL);

        ArrayList<String[]> map3 = new ArrayList<String[]>();

        for (int i = 0; i < rows.size(); i++) {
            map3.add(rows.get(i).split("(?!^)"));
        }



        this.height = map3.size();
        this.width = map3.get(0).length;

        map2 = new String[map3.size()][map3.get(0).length];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                map2[i][j] = map3.get(i)[j];
            }
        }

        Position temp = new Position(width/2, height/2);
        if (map2[width/2][height/2].equals(WALL)) {
            player = new Position(neighbors(temp).get(0).x, neighbors(temp).get(0).y);
        } else {
            player = new Position(width/2, height/2);
        }


        fakeplayer = new Position(1,1);
        String prevLocation = FLOOR;

        Random rand = new Random();
        for (int i = 0; i < 3; i++) {
            boolean validPos = false;
            Position randPos = new Position(rand.nextInt(this.width - 2) + 1, rand.nextInt(this.height - 2) + 1);
            while (!validPos) {
                randPos = new Position(rand.nextInt(width - 2) + 1, rand.nextInt(height - 2) + 1);
                if (map2[randPos.y][randPos.x].equals(FLOOR) && !(player.contains(randPos, 8))) {
                    validPos = true;
                }
            }
            map2[randPos.y][randPos.x] = INVIS_POTION;
        }

        for (int i = 0; i < 3; i++) {
            boolean validPos = false;
            Position randPos = new Position(rand.nextInt(width - 2) + 1, rand.nextInt(height - 2) + 1);
            while (!validPos) {
                randPos = new Position(rand.nextInt(width - 2) + 1, rand.nextInt(height - 2) + 1);
                if (map2[randPos.y][randPos.x].equals(FLOOR) && !(player.contains(randPos, 8))) {
                    validPos = true;
                }
            }
            map2[randPos.y][randPos.x] = FREEZE_POTION;
        }

        Random random = new Random();
        Position randPos = new Position( random.nextInt(width - 1) + 1, random.nextInt(height - 1) + 1 );
        if (coin == 0) {
            for (int i = 0; i < 1; i++) {
                boolean validPos = false;
                while (!validPos) {
                    randPos = new Position( random.nextInt(width - 1) + 1, random.nextInt(height - 1) + 1 );
                    if ((map2[randPos.y][randPos.x].equals(FLOOR)) && !(player.contains(randPos, 8))) {
                        validPos = true;
                    }
                }
                map2[randPos.y][randPos.x] = COIN;
                coin -= 1;
            }
        }

        randPos = new Position( random.nextInt(width - 2) + 1, random.nextInt(height - 2) + 1 );
        if (coin == 0) {
            for (int i = 0; i < 1; i++) {
                boolean validPos = false;
                while (!validPos) {
                    randPos = new Position( random.nextInt(width - 2) + 1, random.nextInt(height - 2) + 1 );
                    if (map2[randPos.y][randPos.x].equals(FLOOR) && !(player.contains(randPos, 8))) {
                        validPos = true;
                    }
                }
                map2[randPos.y][randPos.x] = DOUBLE_COIN;
                doublecoincounter = 200;
            }
        }

        //Position[] enemies = new Position[3];
        for (int i = 0; i < 2; i++) {
            boolean validPos = false;
            randPos = new Position(rand.nextInt(width - 2) + 1, rand.nextInt(height - 2) + 1);
            while (!validPos) {
                randPos = new Position(rand.nextInt(width - 2) + 1, rand.nextInt(height - 2) + 1);
                if (map2[randPos.y][randPos.x].equals(FLOOR) && !(player.contains(randPos, 8))) {
                    validPos = true;
                }
            }
            enemies[i] = randPos;
        }
    }

    public boolean nextTo(Position enemy, Position player) {
        return Math.abs(enemy.x - player.x) <= 1 && Math.abs(enemy.y - player.y) <= 1;
    }

    public String move(Position character, int direction, int distance) {
        String prev = " ";
        if (direction == UP) {
            if (character.y-distance < 0) {
                distance = character.y;
            }
            while (map2[character.y-distance][character.x].equals(WALL)) {
                distance = distance - 1;
            }
            prev = map2[character.y-distance][character.x];
            map2[character.y][character.x] = FLOOR;
            character.y = character.y-distance;
        }

        if (direction == LEFT) {
            if (character.x-distance < 0) {
                distance = character.x;
            }
            while (map2[character.y][character.x-distance].equals(WALL)) {
                distance = distance - 1;
            }
            prev = map2[character.y][character.x-distance];
            map2[character.y][character.x] = FLOOR;
            character.x = character.x-distance;
        }

        if (direction == DOWN) {
            if (character.y+distance > height - 1) {
                distance = height - 1 - character.y;
            }
            while (map2[character.y+distance][character.x].equals(WALL)) {
                distance = distance - 1;
            }
            prev = map2[character.y+distance][character.x];
            map2[character.y][character.x] = FLOOR;
            character.y = character.y+distance;
        }

        if (direction == RIGHT) {
            if (character.x+distance > width - 1) {
                distance = width - 1 - character.x;
            }
            while (map2[character.y][character.x + distance].equals(WALL)) {
                distance = distance - 1;
            }
            prev = map2[character.y][character.x + distance];
            map2[character.y][character.x] = FLOOR;
            character.x = character.x+distance;
        }
        return prev;
    }

    public void randomMove(Position enemy, Position player) {
        int direction = 0;
        Random rand = new Random();
        if (enemy.y < player.y && enemy.x < player.x) {
            int choice = rand.nextInt(1);
            if (choice == 0) {
                direction = RIGHT;
            } else {
                direction = DOWN;
            }
        } else if (enemy.y < player.y && enemy.x > player.x) {
            int choice = rand.nextInt(1);
            if (choice == 0) {
                direction = LEFT;
            } else {
                direction = DOWN;
            }
        } else if (enemy.y > player.y && enemy.x > player.x) {
            int choice = rand.nextInt(1);
            if (choice == 0) {
                direction = LEFT;
            } else {
                direction = UP;
            }
        } else if (enemy.y > player.y && enemy.x < player.x) {
            int choice = rand.nextInt(1);
            if (choice == 0) {
                direction = RIGHT;
            } else {
                direction = UP;
            }
        } else if (enemy.y == player.y) {
            if (enemy.x > player.x) {
                direction = LEFT;
            } else {
                direction = RIGHT;
            }
        } else if (enemy.x == player.x) {
            if (enemy.y > player.y) {
                direction = UP;
            } else {
                direction = DOWN;
            }
        }
        move(enemy,direction,this.moveDistance);
    }

    public void moveTowards(Position enemy, Position player) {
        if (enemy.equals(player)) {
            randomMove(enemy,player);
        } else {
            ArrayList<Map> results = astar(enemy, player);
            Map<Position, Position> came_from = results.get(0);
            Map<Position, Integer> cost_so_far = results.get(1);

            Position tmp = player;

            while (came_from.get(tmp) != enemy) {
                tmp = came_from.get(tmp);
            }
            map2[enemy.y][enemy.x] = FLOOR;

            enemy.x = tmp.x;
            enemy.y = tmp.y;
        }
    }

    public int heuristic(Position a, Position b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    public ArrayList<Position> neighbors(Position current) {
        ArrayList<Position> validNeighbors = new ArrayList<Position>();
        if (!(map2[current.y + 1][current.x].equals(WALL))) {
            validNeighbors.add(new Position(current.x,current.y+1));
        }
        if (!(map2[current.y][current.x+1].equals(WALL))) {
            validNeighbors.add(new Position(current.x+1,current.y));
        }
        if (!(map2[current.y - 1][current.x].equals(WALL))) {
            validNeighbors.add(new Position(current.x,current.y-1));
        }
        if (!(map2[current.y][current.x-1].equals(WALL))) {
            validNeighbors.add(new Position(current.x-1,current.y));
        }
        return validNeighbors;
    }

    public int cost(Position me, Position current, Position next) {
        int window = 5;
        int penalty = 20;
        int cost = 1;
        for (Position enemy: enemies) {
            if (enemy.equals(me)) {
                continue;
            }
            if (enemy.contains(next, window)) {
                cost = penalty;
            }
        }
        return cost;
    }

    public ArrayList<Map> astar(Position start, Position goal) {
        PriorityQueue<PriorityPosition> frontier = new PriorityQueue<PriorityPosition>();
        frontier.add(new PriorityPosition(0,start));

        Map<Position, Position> came_from = new HashMap<Position, Position>();

        Map<Position, Integer> cost_so_far = new HashMap<Position, Integer>();

        came_from.put(start, null);
        cost_so_far.put(start, 0);

        //ArrayList<Integer> came_from = new ArrayList<Integer>();
        //ArrayList<Integer> cost_so_far = new ArrayList<Integer>();
        //came_from.set(start,null);
        //cost_so_far.set(start,0);

        while (!frontier.isEmpty()) {
            PriorityPosition getFrontier = frontier.remove();
            Position current = getFrontier.position;
            int priority = getFrontier.priority;
            if (current.equals(goal)) {
                break;
            }
            for (int i = 0; i < neighbors(current).size(); i++) {
                int new_cost;
                if (cost_so_far.get(current) != null) {
                    new_cost = cost_so_far.get(current) + cost(start, current, neighbors(current).get(i));
                } else {
                    new_cost = cost(start, current, neighbors(current).get(i));
                }

                if (!cost_so_far.containsKey(neighbors(current).get(i)) || new_cost < cost_so_far.get(neighbors(current).get(i))) {
                    cost_so_far.put(neighbors(current).get(i), new_cost);
                    priority = new_cost + heuristic(goal, neighbors(current).get(i));
                    frontier.add(new PriorityPosition(priority, neighbors(current).get(i)));
                    came_from.put(neighbors(current).get(i), current);
                }

                //int[][] priorityArray = new int[2][0];
                //priorityArray[0] =
                //priorityArray[1] = ArrayUtils.toPrimitive((Integer[])cost_so_far.toArray());
            }
            //int[][] priorityArray = new int[2][0];
            //return priorityArray;
        }
        ArrayList<Map> priorityArray = new ArrayList<Map>();
        priorityArray.add(came_from);
        priorityArray.add(cost_so_far);

        return priorityArray;
    }
}
