# This project is to separate shared codes like `HttpTemplate`, which is easier to maintain.

# APIs
### String getContent(String url);
Http `GET` content. Whether to use proxy or cache are set in properties file
### String getContent(String url, boolean useProxy, boolean isCacheEnabled);
Http `GET` content. Whether to use proxy or cache are set in method parameter explicitly
### String postContent(String url, String data);
Http `POST` content. Whether to use proxy are set in properties file
### String postContent(String url, boolean useProxy, Map<String, String> headerMap, String data);
Http `POST` content. Whether to use proxy are set in method parameter explicitly
### String ajaxCall(String url, boolean useProxy, String data);
Ajax call
### boolean downloadFile(String url, String destPath, boolean useProxy);
Download file

# The variables definition in properties file. They should be put into properties file.
| Variable        						| Desc          | Sample  |
|:------------- 						|:-------------|:-----|
crawler.proxy.enabled					| Enable proxy or not|true
crawler.proxyList.location				| The proxy file|proxylist.txt
crawler.cache.enabled					| Enable http caching or not, if enabled it will load the page of the same url from cache rather than crawling again.|false
crawler.cache.location					| Caching location|/cf/cache/
crawler.retry.count						| Retry times|3
crawler.retry.elapse.ms					| How long to wait between the retries|1000
crawler.connection.timeout				| Http connection timeout|10000
crawler.read.timeout					| Http read timeout|10000
crawler.phantomjs.enable				| Enable phantomjs or not `(optional)`|false
crawler.phantomjs.exec.path				| Phantomjs executable path `(optional)`|/cf/phantomjs/bin/phantomjs
crawler.phantomjs.debug.output.folder	| Phantomjs crawled html file local saved path for debug purpose`(optional)`|/tmp/phantomjs.output
crawler.phantomjs.process.num			| How many phantomjs processes will be created`(optional)`|1
