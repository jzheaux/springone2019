package io.jzheaux.springone2019.message.tenant;

public interface TenantAware {
	String getTenant();
	void setTenant(String tenant);
}
