package entity;

public class Player {
    private String id;
    private String name;
    private int age;
    private String sportsType;

    public Player(String id, String name, int age, String sportsType) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.sportsType = sportsType;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getSportsType() {
        return sportsType;
    }

    @Override
    public String toString() {
        return id + "," + name + "," + age + "," + sportsType;
    }

    public static Player fromLine(String line) {
        try {
            String[] parts = line.split(",");
            if (parts.length == 4) {
                return new Player(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3]);
            }
        } catch (Exception e) {
            System.out.println("Error parsing player line.");
        }
        return null;
    }
}