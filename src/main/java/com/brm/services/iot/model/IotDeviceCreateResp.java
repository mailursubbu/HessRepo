package com.brm.services.iot.model;

public class IotDeviceCreateResp
{
    private String message;

    private RespData data;

    private String success;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public RespData getData ()
    {
        return data;
    }

    public void setData (RespData data)
    {
        this.data = data;
    }

    public String getSuccess ()
    {
        return success;
    }

    public void setSuccess (String success)
    {
        this.success = success;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+", data = "+data+", success = "+success+"]";
    }
}
