package com.wealoha.ipgeolocation;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 
 * @author javamonk
 * @createTime 2014年5月25日 下午3:24:29
 */
public class RequestIpHelper {

    protected RequestIpHelper() {
    }

    public static Long getRequestIP(HttpServletRequest request) {
        String header = request.getHeader("X-Forwarded-For");
        Long ip = parseIpFromXForwardFor(header);
        if (ip == null) {
            // 取不到就用这个头(nginx)
            ip = parseIpFromXForwardFor(request.getHeader("X-Real-IP"));
        }
        if (ip == null) {
            ip = parseIpFromXForwardFor(request.getRemoteAddr());
        }
        return ip;
    }

    public static Long parseIpFromXForwardFor(String header) {
        if (StringUtils.isNotEmpty(header)) {
            // "10.0.1.3, 10.0.1.223" 第一个是原始的
            String[] ips = StringUtils.split(header, ",");
            Long lastIp = null;
            for (int i = 0; i < ips.length; i++) {
                Long ip = ipv4ToLong(StringUtils.trim(ips[i]));
                if (ip != null) {
                    lastIp = ip;
                    if (IpCountryHelper.isPrivateNetworkIp(ip)) {
                        // 跳过内网地址
                        continue;
                    }
                    return ip;
                }
            }
            // 如果没有有效的地址，返回最后一个
            return lastIp;
        }
        return null;
    }

    public static Long ipv4ToLong(String ipStr) {
        String[] ip = StringUtils.split(ipStr, ".");
        if (ip != null && ip.length == 4) {
            long l = (NumberUtils.toInt(ip[0]) << 24) + //
                    (NumberUtils.toInt(ip[1]) << 16) + //
                    (NumberUtils.toInt(ip[2]) << 8) + //
                    NumberUtils.toInt(ip[3]);
            return l;
        }
        return null;
    }

    public static String longToIpv4(long ip) {
        return ((ip >>> 24) & 0x000000FF) + "." + // 使用>>或者>>>符号位的值都会一起右移，但是原来的符号位要抹掉
                ((ip & 0x00FFFFFF) >> 16) + "." + //
                ((ip & 0x0000FFFF) >> 8) + "." + //
                (ip & 0x000000FF);
    }
}
