package com.diego.myapplication;



import java.io.Serializable;

/**
 * Created by diego on 13/12/17.
 */

public class Entity implements Serializable,Comparable<Entity>{

    private String name;
    private float rating;

    public Entity(){}

    public Entity(String n, float r){
        name = n;
        rating = r;
    }

    public String getName(){
        return name;
    }

    public float getRating(){
        return rating;
    }

    public void setName(String n){name = n;}

    public void setRating(float r){
        rating = r;
    }


    public int compareTo(Entity e1) {
        return getName().compareTo(e1.getName());
    }
}
