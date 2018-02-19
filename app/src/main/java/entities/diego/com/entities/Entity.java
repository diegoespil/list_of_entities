package entities.diego.com.entities;



import java.io.Serializable;

/**
 * Created by diego on 13/12/17.
 */

public class Entity implements Serializable,Comparable<Entity>{

    private String timestamp;
    private String name;
    private float rating;

    public Entity(){}

    public Entity(String time,String n, float r){
        timestamp = time;
        name = n;
        rating = r;
    }

    public String getTimestamp(){
        return timestamp;
    }

    public String getName(){
        return name;
    }

    public float getRating(){
        return rating;
    }

    public void setTimestamp(String time){
        timestamp = time;
    }

    public void setName(String n){name = n;}

    public void setRating(float r){
        rating = r;
    }


    public int compareTo(Entity e1) {
        return getName().compareTo(e1.getName());
    }
}