package com.za.verify;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.thrift.TException;

import com.za.db.utils.DBUtils;

public class VerifyAppDomainHandler implements VerifyAppDomainService.Iface {
	private static int MAX_SESSION = 2000000;
	private static int CLEAN_SIZE = 100000;
	private static ConcurrentHashMap<String, AppDetail> APPs = new ConcurrentHashMap<>();
	public static final ConcurrentHashMap<String, SessionCheck> SESSIONSCHECK = new ConcurrentHashMap<>(500, 0.75F, 4);

	public VerifyAppDomainHandler() {
		APPs = DBUtils.getProductIds();
		// Config Default
		System.out.println("Total App:" + APPs.size() + "\n" + APPs.toString());
	}

	@Override
	public boolean verifyAppDomain(String appid, String domain, String sessionId) throws TException {
		if (domain == null || appid == null || APPs == null || APPs.get(appid) == null)
			return false;
		return domain.equals(APPs.get(appid).getDomain())
				&& !isSpam(APPs.get(appid).getRpm(), sessionId, SESSIONSCHECK);

	}

	@Override
	public void updateAppDomain(String appId, String appUrl, int rpm) throws TException {
		if (appId != null && appUrl != null && !appUrl.equals("null")) {
			APPs.put(appId, new AppDetail(appUrl, rpm));
		} else {
			APPs.remove(appId);
		}
	}

	private void cleanSessions() {
		for (Entry<String, SessionCheck> entry : SESSIONSCHECK.entrySet()) {
			if (entry.getValue().timeOut()) {
				SESSIONSCHECK.remove(entry);
			}
		}
	}

	// check spam
	private boolean isSpam(int rpm, String sessionId, Map<String, SessionCheck> sessionChecks) {
		if (sessionId == null || sessionId.equals("null"))
			return true;
		if (sessionChecks.containsKey(sessionId)) {
			return sessionChecks.get(sessionId).check();

		} else {
			if (sessionChecks.size() >= CLEAN_SIZE)
				cleanSessions();
			if (sessionChecks.size() < MAX_SESSION)
				sessionChecks.put(sessionId, new SessionCheck(rpm));

			return false;
		}
	}
}

class SessionCheck {
	AtomicInteger count = new AtomicInteger(0);
	AtomicLong sstime = new AtomicLong(System.currentTimeMillis());
	int rpm;

	public SessionCheck(int rpm) {

		this.rpm = rpm;
	}

	boolean check() {
		if ((System.currentTimeMillis() - sstime.get()) > 1 * 60 * 1000) {
			sstime.set(System.currentTimeMillis());
			count.set(0);
			return false;
		} else {
			return (count.incrementAndGet() > rpm);
		}
	}

	boolean timeOut() {
		return (System.currentTimeMillis() - sstime.get()) > 1 * 60 * 1000;
	}
}
