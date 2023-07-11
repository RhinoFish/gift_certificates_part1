package com.epam.esm.dto;

public class TagDTO {
    private int id;
    private String name;

    public TagDTO(){

    }
    public TagDTO(String name){
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
