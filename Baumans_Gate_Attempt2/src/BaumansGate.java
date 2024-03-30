import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

abstract class Unit {
    protected int[] stats = new int[6];
    protected int curr_health;
    final protected String[] stat_names = {"Health","Attack","Range","Defence","Movement","Cost"};
    protected int[] coordinates = new int[2];
    protected String name;
    protected char symbol;
    protected char alt_symbol;
    protected int identifier;
    protected int class_identifier;
    protected int StatNameToIndex(String stat){
        for(int i = 0; i < stat_names.length; i++){
            if (stat_names[i].equals(stat)){
                return i;
            }
        }
        return -1;
    }
    public int getStatByName(String stat){
        int stat_index = StatNameToIndex(stat);
        if (stat_index == -1){
            return -1;
        }
        return stats[stat_index];
    }
    public int getCurrentHealth(){
        return curr_health;
    }
    public boolean recieveDamage_Death(int damage){
        curr_health = curr_health - damage;
        if (curr_health <= 0){
            return true;
        }
        return false;
    }
    public int getX(){
        return coordinates[0];
    }
    public int getY(){
        return coordinates[1];
    }
    public void modifyCoordinates(int x, int y){
        coordinates[0] = x;
        coordinates[1] = y;
    }
    @Override
    public String toString(){
        String output = "Unit: " + name;
        for (int i = 0; i < stats.length; i++){
            output = output + "\n" + stat_names[i] + ": " + stats[i];
        }
        output = output + "\n" + "Current health: " + curr_health;
        return output;
    }
    public String shortToString(){
        return "Unit: " + name + ", current health: " + curr_health + ", available movement: " + getStatByName("Movement") + ", symbol: " + symbol;
    }
    public char getSymbol(){
        return symbol;
    }
    public void swapSymbol(){
        symbol = alt_symbol;
    }
    public int getIdentifier(){
        return identifier;
    }
    public int getCurr_health(){
        return curr_health;
    }
    public String getName(){
        return name;
    }
    public int getClass_identifier(){
        return class_identifier;
    }
}
class Infantry extends Unit{
    Infantry(int type, int x, int y){
        modifyCoordinates(x,y);
        name = "Infantry type ";
        if(type == 1){
            stats = new int[]{50, 100, 100, 8, 3, 10};
            name = name + "Swordman";
            symbol = 'ĩ';
            alt_symbol = 'Ĩ';
            class_identifier = 1;
        }
        else if (type == 2) {
            stats = new int[]{35,3,1,4,6,15};
            name = name + "Spearman";
            symbol = 'ī';
            alt_symbol = 'Ī';
            class_identifier = 2;
        }
        else if (type == 3) {
            stats = new int[]{45,9,1,3,4,20};
            name = name + "Hatchetman";
            symbol = 'ĭ';
            alt_symbol = 'Ĭ';
            class_identifier = 3;
        }
        else {
            stats = new int[]{50, 5, 1, 8, 3, 10};
            name = name + "Swordman";
            symbol = 'ĩ';
            alt_symbol = 'Ĩ';
            class_identifier = 1;
        }
        identifier = 1;
        curr_health = stats[0];
    }
}
class Archer extends Unit{
    Archer(int type, int x, int y){
        modifyCoordinates(x,y);
        name = "Archer type ";
        if(type == 1){
            stats = new int[]{30, 6, 5, 8, 2, 15};
            name = name + "Longbow";
            symbol = 'ā';
            alt_symbol = 'Ā';
            class_identifier = 1;
        }
        else if (type == 2) {
            stats = new int[]{25,3,3,4,4,19};
            name = name + "Shortbow";
            symbol = 'ă';
            alt_symbol = 'Ă';
            class_identifier = 2;
        }
        else if (type == 3) {
            stats = new int[]{40,7,6,3,2,23};
            name = name + "Crossbow";
            symbol = 'ą';
            alt_symbol = 'Ą';
            class_identifier = 3;
        }
        else{
            stats = new int[]{30, 6, 5, 8, 2, 15};
            name = name + "Longbow";
            symbol = 'ā';
            alt_symbol = 'Ā';
            class_identifier = 1;
        }
        identifier = 2;
        curr_health = stats[0];
    }
}
class Mounted extends Unit{
    Mounted(int type, int x, int y){
        modifyCoordinates(x,y);
        name = "Mounted type ";
        if(type == 1){
            stats = new int[]{30, 5, 1, 3, 6, 20};
            name = name + "Knight";
            symbol = 'ŕ';
            alt_symbol = 'Ŕ';
            class_identifier = 1;
        }
        else if (type == 2) {
            stats = new int[]{50,2,1,7,5,23};
            name = name + "Armoured";
            symbol = 'ŗ';
            alt_symbol = 'Ŗ';
            class_identifier = 2;
        }
        else if (type == 3) {
            stats = new int[]{25,3,3,2,5,25};
            name = name + "Archer";
            symbol = 'ř';
            alt_symbol = 'Ř';
            class_identifier = 3;
        }
        else {
            stats = new int[]{30, 5, 1, 3, 6, 20};
            name = name + "Knight";
            symbol = 'ŕ';
            alt_symbol = 'Ŕ';
            class_identifier = 1;
        }
        identifier = 3;
        curr_health = stats[0];
    }
}
class Point{
    int x;
    int y;
    Point(int _x, int _y){
        x = _x;
        y = _y;
    }
}
class Field{
    private int width;
    private int height;
    private char[][] field;
    private char[][] field_printable;
    final private char[] gnd_symbols = {'▒','░','▓','◆'};
    final private double TREES_P = 0.05;
    final private double HILL_P = 0.1;
    final private double SWAMP_P = 0.04;

    private boolean matrixContains(int[][] matrix, int[] query){
        for (int[] pair: matrix){
            if(Arrays.equals(pair,query)){
                return true;
            }
        }
        return false;
    }
    private void copyField(){
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                field_printable[i][j] = field[i][j];
            }
        }
    }
    private void addObstacles(){
        int trees_q = (int) (width*height*TREES_P);
        int hill_q = (int) (width*height*HILL_P);
        int swamp_q = (int) (width*height*SWAMP_P);
        Random generator = new Random();
        int[][] trees = new int[trees_q][2];
        int[][] hills = new int[hill_q][2];
        int[][] swamps = new int[swamp_q][2];
        for (int i = 0; i < trees_q; i++){
            int x = generator.nextInt(width);
            int y = generator.nextInt(height);
            trees[i] = new int[]{x,y};
        }
        for (int i = 0; i < hill_q; i++){
            int x = generator.nextInt(width);
            int y = generator.nextInt(height);
            hills[i] = new int[]{x,y};
        }
        for (int i = 0; i < swamp_q; i++){
            int x = generator.nextInt(width);
            int y = generator.nextInt(height);
            swamps[i] = new int[]{x,y};
        }
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                int[] coords = {j,i};
                if(matrixContains(trees, coords)){
                    field[i][j] = gnd_symbols[3];
                }
                if(matrixContains(hills, coords)){
                    field[i][j] = gnd_symbols[2];
                }
                if(matrixContains(swamps, coords)){
                    field[i][j] = gnd_symbols[1];
                }
            }
        }
    }
    private void fillField(){
        for (int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                field[i][j] = gnd_symbols[0];
            }
        }
        addObstacles();
    }
    Field(int _width, int _height){
        width = _width;
        height = _height;
        field = new char[height][width];
        field_printable = new char[height][width];
        fillField();
        copyField();
    }
    @Override
    public String toString(){
        String output = new String();
        for (char[] arr: field_printable){
            for(char symb: arr){
                output = output + symb;
            }
            output = output + "\n";
        }
        return output;
    }
    public String toStringField(){
        String output = new String();
        for (char[] arr: field){
            for(char symb: arr){
                output = output + symb;
            }
            output = output + "\n";
        }
        return output;
    }
    public boolean move(Point a, Point b){
        if (field_printable[a.y][a.x] == field[a.y][a.x]){
            return false;
        }
        field_printable[b.y][b.x] = field_printable[a.y][a.x];
        field_printable[a.y][a.x] = field[a.y][a.x];
        return true;
    }
    public void put(Point a, char symbol){
        field_printable[a.y][a.x] = symbol;
    }
    public char atPoint(Point a, boolean printable){
        if (printable){
            return field_printable[a.y][a.x];
        }
        return field[a.y][a.x];
    }
    public char at(int x, int y, boolean printable){
        if (printable){
            return field_printable[y][x];
        }
        return field[y][x];
    }
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
    public void remove(Point a){
        field_printable[a.y][a.x] = field[a.y][a.x];
    }
}
class Shop{
    private int account;
    private ArrayList<Unit> Catalogue;
    private void initialiseCatalogue(){
        Catalogue = new ArrayList<>();
        for (int i = 1; i < 4; i++){
            Infantry u1 = new Infantry(i, 0, 0);
            Archer u2 = new Archer(i, 0,0);
            Mounted u3 = new Mounted(i, 0, 0);
            Catalogue.add(u1);
            Catalogue.add(u2);
            Catalogue.add(u3);
        }
    }
    Shop(int _account){
        account = _account;
        initialiseCatalogue();
    }
    private void printCatalogue(){
        for(int i = 0; i < Catalogue.size(); i++){
            System.out.println((i+1) + ".");
            System.out.println(Catalogue.get(i));
        }
    }
    public ArrayList<Unit> commenceShopping(boolean streamlined){
        ArrayList<Unit> Deck = new ArrayList<>();
        if (streamlined){
            for(int i = 0; i < 2; i++){
                Deck.add(new Infantry(1,i,0));
            }
            Deck.add(new Mounted(1,0,0));
            return Deck;
        }
        System.out.println("Welcome to the shop!\nUnits available:");
        printCatalogue();
        System.out.println("To quit type 'quit'");
        String input = "";
        Scanner stream = new Scanner(System.in);
        while(true){
            System.out.println("Gold left: " + account);
            System.out.println("Enter a number of a unit that you want to buy: ");
            input = stream.next();
            int choice = 0;
            if (input.equals("quit")){
                if (Deck.isEmpty()){
                    System.out.println("Buy at least one unit!");
                    continue;
                }
                break;
            }
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e){
                System.out.println("Wrong input!");
                continue;
            }
            Unit chosen = Catalogue.get(choice-1);
            Unit addable = switch (chosen.getIdentifier()) {
                case (1) -> new Infantry(chosen.getClass_identifier(), 0, 0);
                case (2) -> new Archer(chosen.getClass_identifier(), 0, 0);
                case (3) -> new Mounted(chosen.getClass_identifier(), 0, 0);
                default -> null;
            };
            if (account - chosen.getStatByName("Cost") >= 0){
                account = account - chosen.getStatByName("Cost");
                Deck.add(addable);
            } else {
                System.out.println("You're way too poor for that!");
            }
            if (account < 10){
                break;
            }

        }
        return Deck;
    }
    public ArrayList<Unit> invaderShopping(boolean streamlined){
        ArrayList<Unit> Deck = new ArrayList<>();
        if(streamlined){
            Deck.add(new Archer(3,0,0));
            Deck.add(new Infantry(2,0,0));
        } else {
            Random generator = new Random();
            for (int i = 0; i < 3; i++){
                int num = generator.nextInt(9);
                Unit chosen = Catalogue.get(num);
                Unit addable = switch (chosen.getIdentifier()) {
                    case (1) -> new Infantry(chosen.getClass_identifier(), 0, 0);
                    case (2) -> new Archer(chosen.getClass_identifier(), 0, 0);
                    case (3) -> new Mounted(chosen.getClass_identifier(), 0, 0);
                    default -> null;
                };
                Deck.add(addable);
            }
        }
        for (Unit u: Deck){
            u.swapSymbol();
        }
        return Deck;
    }
}
class NotEnoughFieldSpace extends Throwable{}
class GameHandler{
    private ArrayList<Unit> Player_Deck;
    private ArrayList<Unit> Invader_Deck;
    private Shop shop;
    private Field playfield;
    final private char[] gnd_symbols = {'▒','░','▓','◆'};
    final private double[][] fines = {{1,1.5,2,1.2},{1,1.8,2.2,1},{1,2.2,1.2,1.5}};
    private int gndSymbolToIndex(char symbol){
        for (int i = 0; i < gnd_symbols.length; i++){
            if(gnd_symbols[i] == symbol){
                return i;
            }
        }
        return -1;
    }
    private void assignCoords() throws NotEnoughFieldSpace {
        ArrayList<Integer> positions = new ArrayList<>();
        for(int i = 0; i < playfield.getWidth(); i++){
            if(i%2 == 0){
                positions.add(i);
            }
        }
        if (positions.size() < Player_Deck.size() || positions.size() < Invader_Deck.size()) {
            throw new NotEnoughFieldSpace();
        }
        for (int i = 0; i < Player_Deck.size(); i++){
            Player_Deck.get(i).modifyCoordinates(positions.get(i),0);
        }
        for (int i = 0; i < Invader_Deck.size(); i++){
            Invader_Deck.get(i).modifyCoordinates(positions.get(positions.size() - i - 1),playfield.getHeight()-1);
        }
    }
    private void putCharacters(){
        for (Unit u: Player_Deck){
            Point a = new Point(u.getX(),u.getY());
            char symbol = u.getSymbol();
            playfield.put(a, symbol);
        }
        for (Unit u: Invader_Deck){
            Point a = new Point(u.getX(),u.getY());
            char symbol = u.getSymbol();
            playfield.put(a, symbol);
        }
    }
    GameHandler(boolean streamlined, int _width, int _height){
        Player_Deck = new ArrayList<>();
        Invader_Deck = new ArrayList<>();
        shop = new Shop(70);
        Player_Deck = shop.commenceShopping(streamlined);
        Invader_Deck = shop.invaderShopping(streamlined);
        if (streamlined){
            playfield = new Field(10,10);
        }
        playfield = new Field(_width,_height);
        try {
        assignCoords();
        } catch (NotEnoughFieldSpace e){
            System.out.println("Field is not big enough!");
            System.exit(-113);
        }
        putCharacters();
    }
    @Override
    public String toString() {
        String output = playfield.toString();
        for (Unit u: Player_Deck){
            output = output + "\n" + u.shortToString() + ", standing on: " + playfield.at(u.getX(),u.getY(),false);
        }
        return output;
    }
    private int evaluateDistanceEUC(Point a, Point b){
        Point difference = new Point(Math.abs(a.x - b.x), Math.abs(a.y - b.y));
        return (int)Math.sqrt(Math.pow(difference.x,2) + Math.pow(difference.y,2));
    }
    private int evaluateMovement(Unit u, Point a, Point b){
        ArrayList<Character> passedTiles = new ArrayList<>();
        int differenceX = b.x - a.x;
        //System.out.println(differenceX);
        if (differenceX > 0){
            for(int i = a.x + 1; i < b.x + 1; i++){
                passedTiles.add(playfield.at(i,a.y,false));
            }
        } else {
            for (int i = a.x - 1; i > b.x - 1; i--){
                passedTiles.add(playfield.at(i,a.y,false));
            }
        }
        int differenceY = b.y - a.y;
        if (differenceY > 0){
            for(int i = a.y + 1; i < b.y + 1; i++){
                passedTiles.add(playfield.at(b.x,i,false));
            }
        } else {
            for (int i = a.y - 1; i > b.y - 1; i--){
                passedTiles.add(playfield.at(b.x,i,false));
            }
        }
        int identifier = u.getIdentifier();
        int totalMovementCost = 0;
        for (char tile: passedTiles){
            System.out.print(tile);
            totalMovementCost = totalMovementCost + (int)fines[identifier-1][gndSymbolToIndex(tile)];
        }
        System.out.println(totalMovementCost);
        return totalMovementCost;
    }
    private boolean attack (Unit attacker, Unit defender){
        int damage = attacker.getStatByName("Attack");
        double reduction_coef = (double)defender.getStatByName("Defence") / 8;
        damage = (int) ((double)damage - (double)damage*0.33*reduction_coef);
        if(defender.recieveDamage_Death(damage)){
            return true;
        }
        return false;
    }
    private int findUnitByCoordinates(ArrayList<Unit> units, Point a){
        for(int i = 0; i < units.size(); i++){
            if(units.get(i).getX() == a.x && units.get(i).getY() == a.y){
                return i;
            }
        }
        return -1;
    }
    public void playerTurn(){
        Scanner stream = new Scanner(System.in);
        System.out.println("Your turn!\nSyntax: -attack x y; -move x y; -skip");
        String input = "";
        for (Unit u: Player_Deck){
            System.out.println("Now in control of: " + u.getName());
            int actions = 2;
            while (actions > 0){
                input = stream.nextLine();
                if (input.equals("-skip")){
                    actions = actions - 1;
                    System.out.println(this);
                    if (endCondition()){
                        System.exit(0);
                    }
                    continue;
                }
                String[] serialized = input.split(" ");
                if (!(serialized[0].equals("-attack") || serialized[0].equals("-move") || serialized[0].equals("-skip"))){
                    System.out.println(u.getName() + " says: " + "Commander, what are you mumbling? I'd rather wait for you to come to your senses.");
                    actions = actions - 1;
                    System.out.println(this);
                    if (endCondition()){
                        System.exit(0);
                    }
                    continue;
                }
                Point b;
                try{
                    b = new Point(Integer.parseInt(serialized[1]), Integer.parseInt(serialized[2]));
                } catch (NumberFormatException e) {
                    System.out.println(u.getName() + " says: " + "Commander, what are you mumbling? I'd rather wait for you to come to your senses.");
                    actions = actions - 1;
                    System.out.println(this);
                    if (endCondition()){
                        System.exit(0);
                    }
                    continue;
                }
                if (serialized[0].equals("-attack")){
                    int distance = evaluateDistanceEUC(new Point(u.getX(),u.getY()), b);
                    if (distance > u.getStatByName("Range")){
                        System.out.println(u.getName() + " says: " + " I'll never be able to hit that...");
                        actions = actions - 1;
                        System.out.println(this);
                        if (endCondition()){
                            System.exit(0);
                        }
                        continue;
                 }
                    else {
                        int indexOfDefender = findUnitByCoordinates(Invader_Deck, b);
                        if (indexOfDefender == -1){
                            System.out.println(u.getName() + " says: " + "Commander, what are you mumbling? I'd rather wait for you to come to your senses.");
                            actions = actions - 1;
                            System.out.println(this);
                            if (endCondition()){
                                System.exit(0);
                            }
                            continue;
                        }
                        if (attack(u, Invader_Deck.get(indexOfDefender))){
                            System.out.println(u.getName() + " says: " + "Good hit! Target eliminated!");
                            playfield.remove(b);
                            Invader_Deck.remove(indexOfDefender);
                            actions = actions - 1;
                            System.out.println(this);
                            if (endCondition()){
                                System.exit(0);
                            }
                            continue;
                        } else {
                            System.out.println(u.getName() + " says: " + "Good hit!");
                            actions = actions - 1;
                            System.out.println(this);
                            if (endCondition()){
                                System.exit(0);
                            }
                            continue;
                        }
                    }
                }
                if (serialized[0].equals("-move")){
                    int reqMovement = evaluateMovement(u, new Point(u.getX(),u.getY()), b);
                    if (reqMovement > u.getStatByName("Movement")){
                        System.out.println(u.getName() + " says: " + "Commander, I'm way too slow for that...");
                        actions = actions - 1;
                        System.out.println(this);
                        if (endCondition()){
                            System.exit(0);
                        }
                    }
                    else {
                        if (findUnitByCoordinates(Player_Deck, b) != -1 || findUnitByCoordinates(Invader_Deck, b) != -1){
                            System.out.println(u.getName() + " says: " + "Commander, there is someone already there!");
                            actions = actions - 1;
                            System.out.println(this);
                            if (endCondition()){
                                System.exit(0);
                            }
                            continue;
                        }
                        playfield.move(new Point(u.getX(),u.getY()), b);
                        u.modifyCoordinates(b.x,b.y);
                        System.out.println(u.getName() + " says: " + "Got it! Moving.");
                        actions = actions - 1;
                        System.out.println(this);
                        if (endCondition()){
                            System.exit(0);
                        }
                    }
                }

            }
        }
    }
    private Point scanForPlayer(Unit u){
        for(int i = 0; i < playfield.getHeight(); i++){
            for(int j = 0; j < playfield.getWidth(); j++){
                boolean distanceCheck = evaluateDistanceEUC(new Point(u.getX(),u.getY()), new Point(j,i)) <= u.getStatByName("Range");
                boolean playerCheck = findUnitByCoordinates(Player_Deck, new Point(j,i)) != -1;
                if (distanceCheck && playerCheck){
                    return new Point(j,i);
                }
            }
        }

        return new Point(-1,-1);
    }
    public void invaderTurn(){
        String log = "";
        for(Unit u: Invader_Deck){
            Point playerUnitLocation = scanForPlayer(u);
            if (playerUnitLocation.x != -1){
                int indexOfDefender = findUnitByCoordinates(Player_Deck, playerUnitLocation);
                log = log + "\n" + "Enemy's " + u.getName() + " attacks your " + Player_Deck.get(indexOfDefender).getName();
                if(attack(u, Player_Deck.get(indexOfDefender))){
                    log = log + "\n" + Player_Deck.get(indexOfDefender).getName() + " dies!";
                    playfield.remove(playerUnitLocation);
                    Player_Deck.remove(indexOfDefender);
                    continue;
                } else {
                    log = log + "\n" + Player_Deck.get(indexOfDefender).getName() + " gets hit!";
                    continue;
                }
            }
            if (u.getY() != 0) {
                playfield.move(new Point(u.getX(),u.getY()), new Point(u.getX(),u.getY() - 1));
                u.modifyCoordinates(u.getX(), u.getY() - 1);
                log = log + "\n" + "Enemy's " + u.getName() + " moves forward!";
            } else if (u.getStatByName("Movement") < playfield.getHeight()){
                playfield.move(new Point(u.getX(),u.getY()), new Point(u.getX(), u.getY() + u.getStatByName("Movement")));
                u.modifyCoordinates(u.getX(), u.getY() + u.getStatByName("Movement"));
                log = log + "\n" + "Enemy's " + u.getName() + " moves backwards!";
            } else {
                playfield.move(new Point(u.getX(),u.getY()), new Point(u.getX(), playfield.getHeight() - 1));
                u.modifyCoordinates(u.getX(), playfield.getHeight()-1);
                log = log + "\n" + "Enemy's " + u.getName() + " moves backwards!";

            }
        }
        System.out.println(log);
    }
    public boolean endCondition(){
        if (Player_Deck.isEmpty()){
            System.out.println("Invader wins!");
            return true;
        }
        if (Invader_Deck.isEmpty()){
            System.out.println("Player wins!");
            return true;
        }
        return false;
    }
}

public class BaumansGate {
    public static void main(String[] args) {
        System.out.println("Welcome to Bauman's Gate!");
        System.out.println("Current gamemode: " + args[0]);
        int width = 0;
        int height = 0;
        boolean streamlined = false;
        Scanner stream = new Scanner(System.in);
        if (args[0].equals("streamlined")){
            width = 10;
            height = 10;
            streamlined = true;
        }
        else if (args[0].equals("default")){
            System.out.println("Enter game's parameters: \n Syntax: width height");
            String input = stream.nextLine();
            String[] serialized = input.split(" ");
            try {
                width = Integer.parseInt(serialized[0]);
                height = Integer.parseInt(serialized[1]);
            } catch (NumberFormatException e){
                System.out.println("Wrong syntax!");
                System.exit(-13);
            }
        }
        else {
            System.out.println("Unknown gamemode. Program halted");
            System.exit(-1);
        }
        GameHandler instance1 = new GameHandler(streamlined,width,height);
        while(true){
            System.out.println(instance1);
            try {
                instance1.playerTurn();
            } catch (ArrayIndexOutOfBoundsException e){
                System.out.println("Out of bounds! Turn lost. (ur stupid)");
            }
            instance1.invaderTurn();
            if (instance1.endCondition()){
                System.exit(0);
            }
        }
    }
}