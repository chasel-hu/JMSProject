package crawl;

import java.text.ParseException;

import org.junit.Test;

public class NdcpTest {

	@Test
	public void testCrawl(){
		String url = "http://www.gdstats.gov.cn/tjzl/tjkx/";
		String keyword = "广东规模以上工业综合能源消费量{[$]}万吨标准煤";
		CrawlerServiceImpl craw = new CrawlerServiceImpl();
		try {
			craw.immeStartCrawl(url, keyword);
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
}
