package com.cs.wit.socketio.client;

import com.corundumstudio.socketio.SocketIOClient;
import com.cs.wit.basic.MainUtils;
import com.google.common.collect.ArrayListMultimap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractNettyClient implements NettyClient {

    /**
     * 已连接客户端数
     */
    protected ArrayListMultimap<String, SocketIOClient> clientsMap = ArrayListMultimap.create();

    @Override
    public int size() {
        return clientsMap.size();
    }

    @Override
    public List<SocketIOClient> getAllClients() {
        return clientsMap.asMap()
                .values()
                .stream()
                .flatMap( clientList -> clientList.stream())
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Collection<SocketIOClient>> asMap() {
        return clientsMap.asMap();
    }

    @Override
    public List<SocketIOClient> getClients(String key) {
        return clientsMap.get(key);
    }

    @Override
    public void putClient(String key, SocketIOClient client) {
        clientsMap.put(key, client);
    }

    @Override
    public int removeClient(String key, String id) {
        List<SocketIOClient> keyClients = this.getClients(key);

        final List<SocketIOClient> socketIOClientList = keyClients.stream()
                .filter(client -> {
                    String sessionId = client.getSessionId().toString();
                    return sessionId.equals(id)
                            || MainUtils.getContextID(sessionId).equals(id)
                            || !client.isChannelOpen();
                })
                .collect(Collectors.toList());
        if(0 < socketIOClientList.size()) {
            keyClients.removeAll(socketIOClientList);
        }

        if (keyClients.size() == 0) {
            // 当没有客户端链接，可以清除腾出内存，也防止内存溢出
            clientsMap.removeAll(key);
        }
        return keyClients.size();
    }

    @Override
    public void removeAll(final String key) {
        clientsMap.removeAll(key);
    }
}
