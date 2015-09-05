package com.za.verify;

public class AppDetail {
	private String domain;
	private int rpm;

	public AppDetail(String domain, int rpm) {
		setDomain(domain);
		setRpm(rpm);
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public int getRpm() {
		return rpm;
	}

	public void setRpm(int rpm) {
		if (rpm > 0) {
			this.rpm = rpm;

		} else {
			this.rpm = 10;
		}
	}

}
