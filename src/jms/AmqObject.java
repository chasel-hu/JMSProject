package jms;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

/**
 * 对于activeMq生产者和消费者的抽象，提取了共同的成员变量和方法
 * 
 * @author hujiancai
 * @data 2016年7月27日
 * @Description
 * @version v 1.0
 */
public abstract class AmqObject implements Serializable {

	private static final long serialVersionUID = -5883829147797581449L;

	public static final int TOPIC_TYPE = 1; // 表示创建的mq队列为topic，发送方式为一对多
	public static final int QUEUE_TYPE = 2; // 表示创建的mq队列为queue，发送方式为一对一

	protected ConnectionFactory connectionFactory; // 连接工厂
	protected Connection connection = null; // 用于建立连接
	protected Session session; // 消息所在的会话
	protected Destination destination; // 根据待发送的队列的创建的对象

	protected String mqName; // 用于表示待建立的mq队列名称
	protected int type; // 要初始化的队列的类型

	/**
	 * 构造器，进行一系列的默认赋值，但是需要制定mq队列的名称和发送消息的类型 默认使用的user和password都为null
	 * 默认的brokerUrl是本地的mq服务器地址 默认的预读消息个数为1
	 * 
	 * @param mqName
	 *            待建立的mq队列的名称
	 * @param type
	 *            要初始化的队列的类型，1表示topic，2表示queue
	 */
	protected AmqObject(String mqName, int type) {
		this.mqName = mqName;
		this.type = type;
	}

	/**
	 * 关闭connection的连接
	 */
	public void close() {
		if (session != null) {
			try {
				session.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取当前的connection对象
	 * 
	 * @return 当前的connection对象
	 */
	public Connection getConnection() {
		return this.connection;
	}

	/**
	 * 取得当前的session对象
	 * 
	 * @return 当前的session对象
	 */
	public Session getSession() {
		return this.session;
	}

	public String getMqName() {
		return mqName;
	}

	public void setMqName(String mqName) {
		this.mqName = mqName;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}