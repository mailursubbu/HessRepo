package com.brm.services.iot.model;

public class IotAccCreateResp
{
    private String message;

    private Data data;

    private String success;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public Data getData ()
    {
        return data;
    }

    public void setData (Data data)
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
	public String toString() {
		return "IotAccCreateResp [message=" + message + ", data=" + data + ", success=" + success + "]";
	}
    
    
}
