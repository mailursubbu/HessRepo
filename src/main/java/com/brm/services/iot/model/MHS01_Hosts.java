package com.brm.services.iot.model;

public class MHS01_Hosts
{
    private String id;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

	@Override
	public String toString() {
		return "MHS01_Hosts [id=" + id + "]";
	}
}
