# ip-geolocation
IP to country convert helper, data by https://db-ip.com/db/

```java
String xForwardFor = request.getHeader('X-Forward-For');
String ip = getIpFromXForwardFor(ForwardFor);
String country = IpCountryHelper.getCountry(ip);
