# ip-geolocation
IP to country convert helper, data by https://db-ip.com/db/



```xml
<dependency>
    <groupId>com.wealoha</groupId>
    <artifactId>ip-geolocation</artifactId>
    <version>1.0.0</version>
</dependency>
```


```java
String xForwardFor = request.getHeader('X-Forward-For');
String ip = getIpFromXForwardedFor(ForwardFor);
String country = IpCountryHelper.getCountry(ip);
```

## Copyright
IP to country database by db-ip.com. 
Copyright © 2015 db-ip.com, all rights reserved.
