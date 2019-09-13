package io.jzheaux.springone2019.tenant;

/**
 * @author Josh Cummings
 */
public class Tenant {
	private String alias;

	private String issuerUri;

	private String jwkSetUri;

	public Tenant() {}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getIssuerUri() {
		return issuerUri;
	}

	public void setIssuerUri(String issuerUri) {
		this.issuerUri = issuerUri;
	}

	public String getJwkSetUri() {
		return jwkSetUri;
	}

	public void setJwkSetUri(String jwkSetUri) {
		this.jwkSetUri = jwkSetUri;
	}
}
