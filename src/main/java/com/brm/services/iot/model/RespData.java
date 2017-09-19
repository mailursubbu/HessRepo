package com.brm.services.iot.model;

public class RespData
{
    private Services services;

    private MHS01_Hosts MHS01_Hosts;

    public Services getServices ()
    {
        return services;
    }

    public void setServices (Services services)
    {
        this.services = services;
    }

    public MHS01_Hosts getMHS01_Hosts ()
    {
        return MHS01_Hosts;
    }

    public void setMHS01_Hosts (MHS01_Hosts MHS01_Hosts)
    {
        this.MHS01_Hosts = MHS01_Hosts;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [services = "+services+", MHS01_Hosts = "+MHS01_Hosts+"]";
    }
}

