package jms.listener;

import java.util.Date;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

/**
 * 默认的监听器，将接收到的消息打印到控制台
 * @author hujiancai
 * @data 2016年7月27日
 * @Description 
 * @version v 1.0
 */
public class DefaultListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		System.out.print(new Date() + ", you recieve a message,");

		if (message instanceof TextMessage) {
			TextMessage msg = (TextMessage) message;
			try {
				System.out.println("TextMessage : " + msg.getText());
			} catch (JMSException e) {
				e.printStackTrace();
			}
		} else if (message instanceof ObjectMessage) {

			ObjectMessage msg = (ObjectMessage) message;
			System.out.println("ObjectMessage : " + msg.toString());
		} else if (message instanceof MapMessage) {

			MapMessage msg = (MapMessage) message;
			System.out.println("MapMessage : " + msg.toString());
		} else if (message instanceof BytesMessage) {

			BytesMessage msg = (BytesMessage) message;
			System.out.println("BytesMessage : " + msg.toString());
		} else if (message instanceof StreamMessage) {

			StreamMessage msg = (StreamMessage) message;
			System.out.println("StreamMessage : " + msg.toString());
		}

	}

}
