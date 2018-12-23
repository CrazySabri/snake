package club.itchina.snake.tools.config.data;

import java.util.Objects;

/**
 * @author: sabri
 * @date: 2018/12/18 22:40
 * @description: 耐得住寂寞，守得住繁华--寂寞高手
 */
public class RedisNode {
    private String host;
    private int port;

    public RedisNode() {
    }

    public RedisNode(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static RedisNode from(String config) {
        String[] array = config.split(":");
        // TODO: 此处区分的意义？
        if(array.length == 2){
            return new RedisNode(array[0], Integer.parseInt(array[1]));
        }else{
            return new RedisNode(array[0], Integer.parseInt(array[1]));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RedisNode redisNode = (RedisNode) o;
        return port == redisNode.port &&
                Objects.equals(host, redisNode.host);
    }

    @Override
    public int hashCode() {
        // TODO: 此处hash算法原理
        int result = host.hashCode();
        result = 31*result + port;
        return result;
    }

    @Override
    public String toString() {
        return "RedisNode{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
