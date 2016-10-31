package crawl;

import java.util.Date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import us.codecraft.webmagic.Spider;

/**
 * 
 * @author liuyanxiang
 * @data 2016年7月9日
 * @Description 实现爬虫service接口定义的方法
 * @version v 1.0
 */

public class CrawlerServiceImpl{
	
	
	/**
	 * 根据数据项获得爬虫
	 * @author liuyanxiang
	 * @param dataitem 关键字实体
	 * @param result 保存爬取结果的集合
	 * @param keyword 该爬虫对应的关键字（一个数据项实体可以存在多个关键字）
	 * @return 爬虫对象
	 */
	private Spider getSpider(String url, List<Map<String,Object>> result, String expContent){
		
		Spider spider = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		try {
			date = sdf.parse("2016-01-01");
			spider = Spider.create(new NormalPageProcessor(url,expContent,date));

			spider.addPipeline(new NormalPipeline(result));
			
			String dir = getClass().getResource("/").getPath().toString().replace("classes", "plugins");
			String driver = dir + "firefox/firefox-bin";
			System.err.println("dir is " + dir);
			spider.setDownloader(new FireFoxDownloader("C:/Program Files (x86)/Mozilla Firefox/firefox"));
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		return spider;
		
	}

	public List<Map<String, Object>> immeStartCrawl(String url,String keyword) throws ParseException {

		List<Map<String,Object>> result = null;

		result = new ArrayList<Map<String, Object>>();
		System.err.println("------1-----");

		Spider spider = getSpider(url, result, keyword);
		System.err.println("------2-----");

		spider.addUrl(url);
		System.err.println("------3-----");

		spider.run();
		System.err.println("------4-----");

		for (Map<String, Object> m : result) {
			System.err.println(" -- " + m);
		}

		return result;
	}
}
                                                  
