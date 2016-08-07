package jms.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AmqUtil {

	public static String brokerUrl;
	public static String userName;
	public static String password;
	
	static{
		Properties prop = new Properties();
		try {  
            prop.load(new FileInputStream("src/amq_config.properties"));
            brokerUrl = prop.getProperty("brokerUrl");
            userName = prop.getProperty("userName");
            password = prop.getProperty("password");
        } catch(IOException e) {  
            e.printStackTrace();  
        }  
	}
}
