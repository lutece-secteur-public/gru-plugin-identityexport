package fr.paris.lutece.plugins.identityexport.business;

public class Attribute {
	public long certificateDate;
    public String lastUpdateClientCode;
    public String name;
    public String description;
    public boolean pivot;
    public String certifierCode;
    public Object certificateExpirationDate;
    public String type;
    public String value;
    public String certifierName;
    public String key;
    
    
	public long getCertificateDate() {
		return certificateDate;
	}
	public void setCertificateDate(long certificateDate) {
		this.certificateDate = certificateDate;
	}
	public String getLastUpdateClientCode() {
		return lastUpdateClientCode;
	}
	public void setLastUpdateClientCode(String lastUpdateClientCode) {
		this.lastUpdateClientCode = lastUpdateClientCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isPivot() {
		return pivot;
	}
	public void setPivot(boolean pivot) {
		this.pivot = pivot;
	}
	public String getCertifierCode() {
		return certifierCode;
	}
	public void setCertifierCode(String certifierCode) {
		this.certifierCode = certifierCode;
	}
	public Object getCertificateExpirationDate() {
		return certificateExpirationDate;
	}
	public void setCertificateExpirationDate(Object certificateExpirationDate) {
		this.certificateExpirationDate = certificateExpirationDate;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getCertifierName() {
		return certifierName;
	}
	public void setCertifierName(String certifierName) {
		this.certifierName = certifierName;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
    
    
}
