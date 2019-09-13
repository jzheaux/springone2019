package io.jzheaux.springone2019.tenant;

public class Client {
	String tenantAlias;

	String issuerUri;

	String clientId;

	public String getTenantAlias() {
		return tenantAlias;
	}

	public void setTenantAlias(String tenantAlias) {
		this.tenantAlias = tenantAlias;
	}

	public String getIssuerUri() {
		return issuerUri;
	}

	public void setIssuerUri(String issuerUri) {
		this.issuerUri = issuerUri;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
