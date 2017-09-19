package com.brm.services.iot.model;

public class Services
{
    private Error error;

    private Entry_list[] entry_list;

    public Error getError ()
    {
        return error;
    }

    public void setError (Error error)
    {
        this.error = error;
    }

    public Entry_list[] getEntry_list ()
    {
        return entry_list;
    }

    public void setEntry_list (Entry_list[] entry_list)
    {
        this.entry_list = entry_list;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [error = "+error+", entry_list = "+entry_list+"]";
    }
}