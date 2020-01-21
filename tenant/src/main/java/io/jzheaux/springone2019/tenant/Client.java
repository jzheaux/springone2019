package io.jzheaux.springone2019.tenant;

public class Client {
	String tenantAlias;

	String issuerUri;

	String clientId;

	String clientSecret;

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

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
}
