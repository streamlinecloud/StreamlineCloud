package net.streamlinecloud.main.core.backend;

import lombok.Getter;
import net.streamlinecloud.api.extension.event.player.PlayerChoseProxyEvent;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.server.RunningServer;
import net.streamlinecloud.main.extension.ExtensionManager;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class LoadBalancer {

    @Getter
    transient private List<RunningServer> servers;
    transient private Selector selector;

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

    public void registerServer(RunningServer server) {
        servers.add(server);
    }

    private Optional<RunningServer> nextServer() {
        return servers.stream()
                .min(Comparator.comparingInt(ps -> ps.getOnlinePlayers().size()));
    }

    public void start() throws IOException {
        servers = new ArrayList<>();

        selector = Selector.open();

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        StreamlineCloud.log(name + " listening on port " + port);

        new Thread(() -> {
            while (true) {
                int readyChannels;
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

            try {
                RunningServer target = nextServer().orElse(null);

                if (target == null) {
                    clientChannel.close();
                    return;
                }

                ExtensionManager.eventManager.callEvent(new PlayerChoseProxyEvent(target));

                SocketChannel backendChannel = SocketChannel.open();
                backendChannel.configureBlocking(false);
                backendChannel.connect(new InetSocketAddress(target.getAddress(), target.getPort()));

                ConnectionPair pair = new ConnectionPair(clientChannel, backendChannel);

                clientChannel.register(selector, SelectionKey.OP_READ, pair);
                backendChannel.register(selector, SelectionKey.OP_CONNECT, pair);
            } catch (NoSuchElementException e) {
                StreamlineCloud.log(getName() + " does not have any registered servers (" + e.getMessage() + ")");
            }
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
                try {
                    dstChannel.write(buffer);
                } catch (NotYetConnectedException e) {
                    return;
                }
            }
        } catch (IOException e) {
            pair.close();
        }
    }

    private static class ConnectionPair {
        private final SocketChannel clientChannel;
        private final SocketChannel backendChannel;

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
