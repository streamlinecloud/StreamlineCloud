package net.streamlinecloud.main.core.backend;

import lombok.Getter;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.server.CloudServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadBalancer {

    transient private List<CloudServer> backendServers;
    transient private AtomicInteger currentIndex;

    transient private Selector selector;
    transient private ServerSocketChannel serverChannel;

    @Getter
    String name;
    @Getter
    String group;
    @Getter
    int port;

    public LoadBalancer(String name, String group, int port) {
        this.port = port;
        this.name = name;
        this.group = group;
    }

    public void registerServer(CloudServer server) {
        backendServers.add(server);
        StreamlineCloud.log(server.getName() + "-" + server.getShortUuid() + " connected to " + name);
    }

    private CloudServer nextServer() {
        if (backendServers.isEmpty()) return null;
        int index = currentIndex.getAndIncrement() % backendServers.size();
        return backendServers.get(index);
    }

    public void start() throws IOException {
        backendServers = new ArrayList<>();
        currentIndex = new AtomicInteger(0);

        selector = Selector.open();

        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        StreamlineCloud.log(name + " listening on port " + port);

        new Thread(() -> {
            while (true) {
                int readyChannels = 0;
                try {
                    readyChannels = selector.select();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (readyChannels == 0) continue;

                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (!key.isValid())
                        continue;

                    if (key.isAcceptable()) {
                        handleAccept(key);
                    }

                    else if (key.isConnectable()) {
                        finishConnect(key);
                    }
                    else if (key.isReadable()) {
                        handleRead(key);
                    }
                }
            }
        }).start();
    }

    private void handleAccept(SelectionKey key) {
        try {
            ServerSocketChannel serverSock = (ServerSocketChannel) key.channel();
            SocketChannel clientChannel = serverSock.accept();
            clientChannel.configureBlocking(false);

            CloudServer target = nextServer();
            if (target == null) {
                clientChannel.close();
                return;
            }

            SocketChannel backendChannel = SocketChannel.open();
            backendChannel.configureBlocking(false);
            backendChannel.connect(new InetSocketAddress(target.getAddress(), target.getPort()));

            ConnectionPair pair = new ConnectionPair(clientChannel, backendChannel);

            clientChannel.register(selector, SelectionKey.OP_READ, pair);
            backendChannel.register(selector, SelectionKey.OP_CONNECT, pair);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void finishConnect(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        try {
            if (channel.finishConnect()) {
                key.interestOps(SelectionKey.OP_READ);
            }
        } catch (IOException e) {
            ConnectionPair pair = (ConnectionPair) key.attachment();
            pair.close();
        }
    }

    private void handleRead(SelectionKey key) {
        ConnectionPair pair = (ConnectionPair) key.attachment();
        SocketChannel srcChannel = (SocketChannel) key.channel();
        SocketChannel dstChannel = pair.getPeer(srcChannel);

        ByteBuffer buffer = ByteBuffer.allocate(4096);
        try {
            int bytesRead = srcChannel.read(buffer);
            if (bytesRead == -1) {
                pair.close();
                return;
            } else if (bytesRead == 0) {
                return;
            }
            buffer.flip();

            if (!dstChannel.isConnected()) {
                if (dstChannel.isConnectionPending()) {
                    try {
                        dstChannel.finishConnect();
                    } catch (IOException e) {
                        pair.close();
                        return;
                    }
                } else {
                    pair.close();
                    return;
                }
            }

            while (buffer.hasRemaining()) {
                dstChannel.write(buffer);
            }
        } catch (IOException e) {
            pair.close();
        }
    }

    private class ConnectionPair {
        private SocketChannel clientChannel;
        private SocketChannel backendChannel;

        public ConnectionPair(SocketChannel clientChannel, SocketChannel backendChannel) {
            this.clientChannel = clientChannel;
            this.backendChannel = backendChannel;
        }

        public SocketChannel getPeer(SocketChannel channel) {
            return channel.equals(clientChannel) ? backendChannel : clientChannel;
        }

        public void close() {
            try {
                if (clientChannel != null && clientChannel.isOpen())
                    clientChannel.close();
            } catch (IOException ignored) {}
            try {
                if (backendChannel != null && backendChannel.isOpen())
                    backendChannel.close();
            } catch (IOException ignored) {}
        }
    }

}
