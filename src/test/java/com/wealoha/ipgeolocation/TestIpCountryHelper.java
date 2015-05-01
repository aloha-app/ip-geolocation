package com.wealoha.ipgeolocation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 * @author javamonk
 * @createTime 2015年4月3日 下午1:35:00
 */
public class TestIpCountryHelper {

    @Test
    public void testGetCountry() {
        String[] ips = { "111.201.34.56", "206.253.166.171", "216.58.221.68", "219.118.222.227", //
                "0.0.0.0", "121.201.7.28", "202.175.21.120", "120.18.129.109", //
                "101.8.134.158" };
        String[] results = { "CN", "US", "US", "JP", //
                "US", "CN", "MO", "AU", //
                "TW" };
        for (int i = 0; i < ips.length; i++) {
            String ip = ips[i];
            String expected = results[i];
            Long ipLong = RequestIpHelper.ipv4ToLong(ip);
            String country = IpCountryHelper.getCountry(ipLong);
            assertEquals(expected, country);
        }
    }

    @Test
    public void testIsPrivateNetworkIp() {
        String[] ips = { "192.168.10.175", "192.168.0.1", "169.255.0.0", "121.201.7.28", //
                "111.201.34.56", "10.0.1.1", "11.10.1.1" };

        boolean[] results = { true, true, false, false, //
                false, true, false };

        for (int i = 0; i < ips.length; i++) {
            String ip = ips[i];

            assertEquals(results[i],
                    IpCountryHelper.isPrivateNetworkIp(RequestIpHelper.ipv4ToLong(ip)));
        }
    }
}
