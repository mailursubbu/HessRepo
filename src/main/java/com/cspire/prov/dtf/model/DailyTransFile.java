package com.cspire.prov.dtf.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ProvisioningMobiDtf")    
public class DailyTransFile{
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer Id;    
    private Long txnId;//mobiPurchaseId;
    private Integer provId;
    private Long bsiOmniaSo;    
    private String txnType;
    private Long txnTime;
    private String productId;
    private String uId;
    private String origin;
    private String comment;
    private Long processed;
    private String accStatus;
    private String fipsCode;
    
    public String getAccStatus() {
		return accStatus;
	}
	public void setAccStatus(String accStatus) {
		this.accStatus = accStatus;
	}
	public String getFipsCode() {
		return fipsCode;
	}
	public void setFipsCode(String fipsCode) {
		this.fipsCode = fipsCode;
	}
	public Integer getId() {
        return Id;
    }
    public void setId(Integer id) {
        Id = id;
    }
    public Long getTxnId() {
        return txnId;
    }
    public void setTxnId(Long txnId) {
        this.txnId = txnId;
    }
    public Integer getProvId() {
        return provId;
    }
    public void setProvId(Integer provId) {
        this.provId = provId;
    }
    public Long getBsiOmniaSo() {
        return bsiOmniaSo;
    }
    public void setBsiOmniaSo(Long bsiOmniaSo) {
        this.bsiOmniaSo = bsiOmniaSo;
    }
    public String getTxnType() {
        return txnType;
    }
    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }
    public Long getTxnTime() {
        return txnTime;
    }
    public void setTxnTime(Long txnTime) {
        this.txnTime = txnTime;
    }

    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
    public String getuId() {
        return uId;
    }
    public void setuId(String uId) {
        this.uId = uId;
    }
    public String getOrigin() {
        return origin;
    }
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public Long getProcessed() {
        return processed;
    }
    public void setProcessed(Long processed) {
        this.processed = processed;
    }
  
    public DailyTransFile() {
        super();
    }
    @Override
    public String toString() {
        return "DailyTransFile [Id=" + Id + ", txnId=" + txnId + ", provId=" + provId + ", bsiOmniaSo=" + bsiOmniaSo
                + ", txnType=" + txnType + ", txnTime=" + txnTime + ", productId=" + productId + ", uId=" + uId
                + ", origin=" + origin + ", comment=" + comment + ", processed=" + processed + "]";
    }        
        
}