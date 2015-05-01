package com.wealoha.ipgeolocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IP to country
 * 
 * @author javamonk
 * @createTime 2015年4月3日 下午12:36:53
 * @see data file source <a href="https://db-ip.com/db/">https://db-ip.com/db/</a>, Copyright 2015 db-ip.com
 *      <a href="http://en.wikipedia.org/wiki/Private_network">http://en.
 *      wikipedia.org/wiki/Private_network</a>
 * @see algorithm <a href=
 *      "http://stackoverflow.com/questions/4256438/calculate-whether-an-ip-is-in-a-specified-range-in-java"
 *      >http://stackoverflow.com/questions/4256438/calculate-whether-an-ip
 *      -is-in-a-specified-range-in-java<a>
 */
public abstract class IpCountryHelper {

    // ip转成long来比较
    // key: start
    // value: end
    private static TreeMap<Long, Long> ipRangeMap = new TreeMap<Long, Long>();

    private static TreeMap<Long, Long> privateIpRangeMap = new TreeMap<Long, Long>();

    // key: 和ipRangeMap一致
    private static Map<Long, String> countryMap = new HashMap<>();

    private static Logger logger = LoggerFactory.getLogger(IpCountryHelper.class);
    static {
        // "1.0.8.0","1.0.15.255","CN"
        // "(\d+\.\d+\.\d+\.\d+)","(\d+\.\d+\.\d+\.\d+)","(\w+)"
        Pattern p = Pattern
                .compile("\"(\\d+\\.\\d+\\.\\d+\\.\\d+)\",\"(\\d+\\.\\d+\\.\\d+\\.\\d+)\",\"(\\w+)\"");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                IpCountryHelper.class.getResourceAsStream("/dbip-country-2015-04.csv")))) {
            String line = null;
            do {
                line = reader.readLine();

                if (line != null) {
                    Matcher m = p.matcher(line);
                    if (m.find()) {
                        String ipStart = m.group(1);
                        String ipEnd = m.group(2);
                        String country = m.group(3);

                        Long start = RequestIpHelper.ipv4ToLong(ipStart);
                        Long end = RequestIpHelper.ipv4ToLong(ipEnd);
                        Long exist = ipRangeMap.put(start, end);
                        if (exist != null) {
                            logger.warn("ip冲突: start={}, end={}({}), exist(value)={}", //
                                    ipStart, ipEnd, end, exist);
                        }
                        countryMap.put(start, country);
                    } else if (!StringUtils.contains(line, ":")) {
                        // 非ipv6
                        logger.warn("不支持的地址: {}", line);
                    }
                }

            } while (line != null);
        } catch (IOException e) {
            throw new RuntimeException("读取ip数据文件失败", e);
        }

        // 本地网络
        String[] localIpRanges = { "10.0.0.0", "10.255.255.255", //
                "169.254.1.0", "169.254.254.255", //
                "172.16.0.0", "172.31.255.255", //
                "192.168.0.0", "192.168.255.255", //
                "240.0.0.0", "254.255.255.254", //
        };
        for (int i = 0; i < localIpRanges.length; i++) {
            privateIpRangeMap.put(RequestIpHelper.ipv4ToLong(localIpRanges[i]),
                    RequestIpHelper.ipv4ToLong(localIpRanges[++i]));
        }
    }

    private IpCountryHelper() {
    }

    /**
     * 获取ip对应的国家
     * 
     * @param ip
     * @return 如果找不到或者是内网地址，返回null
     */
    public static String getCountry(long ip) {
        if (isPrivateNetworkIp(ip)) {
            return null;
        }
        // 开始要比ip低
        Long start = ipRangeMap.floorKey(ip);
        Long end = ipRangeMap.get(start);
        if (ip <= end) {
            return countryMap.get(start);
        }
        logger.warn("value错误: start={}, end={}, ip={}", start, end, ip);
        return null;
    }

    /**
     * 是否内网
     * 
     * @param ip
     * @return
     */
    public static boolean isPrivateNetworkIp(long ip) {
        Long start = privateIpRangeMap.floorKey(ip);
        if (start == null) {
            return false;
        }
        Long end = privateIpRangeMap.get(start);
        return ip <= end;
    }
}
