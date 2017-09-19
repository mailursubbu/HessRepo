package com.brm.services.iot.model;

public class Error
{
    private String description;

    private String name;

    private String number;

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getNumber ()
    {
        return number;
    }

    public void setNumber (String number)
    {
        this.number = number;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [description = "+description+", name = "+name+", number = "+number+"]";
    }
}