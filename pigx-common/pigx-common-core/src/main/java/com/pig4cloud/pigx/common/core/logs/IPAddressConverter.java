package com.pig4cloud.pigx.common.core.logs;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import org.apache.commons.lang.StringUtils;

public class IPAddressConverter extends ClassicConverter {
	String localIPAddressStr;

	public IPAddressConverter() {
		try {
			this.localIPAddressStr = getHostAddress();
		} catch (Exception var2) {
			this.localIPAddressStr = "127.0.0.1";
		}

	}

	public String convert(ILoggingEvent iLoggingEvent) {
		return this.localIPAddressStr;
	}

	public static String getHostAddress() {
		return isWindowsOS() ? getWindowsHostAddress() : getLinuxHostAddress();
	}

	public static boolean isWindowsOS() {
		boolean isWindowsOS = false;
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().indexOf("windows") > -1) {
			isWindowsOS = true;
		}

		return isWindowsOS;
	}

	private static String getWindowsHostAddress() {
		String localIp = "";

		try {
			localIp = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException var2) {
			localIp = "127.0.0.1";
		}

		return localIp;
	}

	private static String getLinuxHostAddress() {
		String localIp = "";

		try {
			Enumeration interfaces = NetworkInterface.getNetworkInterfaces();

			do {
				NetworkInterface networkInterface;
				do {
					do {
						do {
							if (!interfaces.hasMoreElements()) {
								return localIp;
							}

							networkInterface = (NetworkInterface)interfaces.nextElement();
						} while(networkInterface.isLoopback());
					} while(networkInterface.isVirtual());
				} while(!networkInterface.isUp());

				Enumeration addresses = networkInterface.getInetAddresses();

				while(addresses.hasMoreElements()) {
					InetAddress addr = (InetAddress)addresses.nextElement();
					if (addr != null && !addr.isLoopbackAddress() && addr instanceof Inet4Address) {
						localIp = addr.getHostAddress();
						break;
					}
				}
			} while(!StringUtils.isNotEmpty(localIp));
		} catch (SocketException var5) {
			localIp = "127.0.0.1";
		}

		return localIp;
	}
}

