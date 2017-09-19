package com.brm.services.iot.model;

public class Entry_list
{
    private String count;

    private String[] total_records;

    public String getCount ()
    {
        return count;
    }

    public void setCount (String count)
    {
        this.count = count;
    }

    public String[] getTotal_records ()
    {
        return total_records;
    }

    public void setTotal_records (String[] total_records)
    {
        this.total_records = total_records;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [count = "+count+", total_records = "+total_records+"]";
    }
}