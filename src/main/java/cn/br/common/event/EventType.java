package cn.br.common.event;

/**
 * Event type <br/>
 * 事件类型
 *
 * @author ZJL
 * @since 2.0.0
 */
public enum EventType {
	/**
	 * 服务启动中
	 */
    SERVER_STARTING,
    
    /**
     * 服务启动完成
     */
    SERVER_STARTED,
    
    /**
     * 服务关闭中
     */
    SERVER_STOPPING,
    
    /**
     * 服务已关闭
     */
    SERVER_STOPPED,
    
    /**
     * 创建会话
     */
    SESSION_CREATED,
    
    /**
     * 会话销毁
     */
    SESSION_DESTROY,
    
    /**
     * 资源发生变化
     */
    SOURCE_CHANGED,
    
    /**
     * 环境发生变化
     */
    ENVIRONMENT_CHANGED
}