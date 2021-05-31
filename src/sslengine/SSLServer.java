package sslengine;

import javax.net.ssl.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.security.SecureRandom;
import java.util.Iterator;

public class SSLServer extends SSLPeer {
    private boolean active = false;
    private SSLContext context;
    private Selector selector;
    private String message;
    private SelectionKey key;

    public SSLServer(String protocol, String IP, int port) throws Exception {
        this.port = port;
        this.context = SSLContext.getInstance(protocol);
        context.init(createKeyManagers("../../keys/server.keys", "123456", "123456"), createTrustMangers("../../keys/truststore", "123456"), new SecureRandom());

        SSLSession dummySession = context.createSSLEngine().getSession();
        this.myAppData = ByteBuffer.allocate(dummySession.getApplicationBufferSize());
        this.myNetData = ByteBuffer.allocate(dummySession.getPacketBufferSize());
        this.peerAppData = ByteBuffer.allocate(dummySession.getApplicationBufferSize());
        this.peerNetData = ByteBuffer.allocate(dummySession.getPacketBufferSize());

        dummySession.invalidate();

        selector = SelectorProvider.provider().openSelector();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(IP, port));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        active = true;
    }

    public String start() throws Exception {
        while(active) {
            this.selector.select();
            Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
            while(selectedKeys.hasNext()) {
                SelectionKey key = selectedKeys.next();
                selectedKeys.remove();

                if (!key.isValid())
                    continue;
                if (key.isAcceptable()) {
                    accept(key);
                } else if (key.isReadable()) {
                    this.key = key;
                    return read((SocketChannel) key.channel(), (SSLEngine) key.attachment());
                }
            }
        }
        return null;
    }

    public void stop() {
        active = false;
        executor.shutdown();
        selector.wakeup();
    }

    private void accept(SelectionKey key) throws Exception {
        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        socketChannel.configureBlocking(false);

        SSLEngine engine = context.createSSLEngine();
        engine.setUseClientMode(false);
        engine.setNeedClientAuth(true);
        engine.beginHandshake();

        if (doHandshake(socketChannel, engine))
            socketChannel.register(selector, SelectionKey.OP_READ, engine);
        else {
            socketChannel.close();
        }
    }

    public String read() {
        try {
            return read((SocketChannel) this.key.channel(), (SSLEngine) this.key.attachment());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String read(SocketChannel socketChannel, SSLEngine engine) throws Exception {
        this.peerNetData.clear();
        int bytesRead = socketChannel.read(this.peerNetData);
        if (bytesRead > 0) {
            this.peerNetData.flip();
            while(this.peerNetData.hasRemaining()) {
                this.peerAppData.clear();
                SSLEngineResult result = engine.unwrap(this.peerNetData, this.peerAppData);
                switch (result.getStatus()) {
                    case OK -> {
                        this.peerAppData.flip();
                        System.out.println("Received message: " + new String(this.peerAppData.array()));
                        this.message = new String(this.peerAppData.array());
                    }
                    case BUFFER_OVERFLOW -> {
                        this.peerAppData = enlargeApplicationBuffer(engine, this.peerAppData);
                    }
                    case BUFFER_UNDERFLOW -> {
                        this.peerNetData = handleBufferUnderflow(engine, this.peerNetData);
                    }
                    case CLOSED -> {
                        closeConnection(socketChannel, engine);
                        return null;
                    }
                    default -> throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
                }
            }

            return this.message;
        } else if (bytesRead < 0) {
            handleEndOfStream(socketChannel, engine);
        }

        return null;
    }

    public void write(String message) {
        try {
            SocketChannel socketChannel = (SocketChannel) this.key.channel();
            SSLEngine engine = (SSLEngine) this.key.attachment();
            write(socketChannel, engine, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        @Override
    protected void write(SocketChannel socketChannel, SSLEngine engine, String message) throws Exception {
        this.myAppData.clear();
        this.myAppData.put(message.getBytes());
        this.myAppData.flip();
        while(this.myAppData.hasRemaining()) {
            this.myNetData.clear();
            SSLEngineResult result = engine.wrap(this.myAppData, this.myNetData);
            switch (result.getStatus()) {
                case OK -> {
                    this.myNetData.flip();
                    while(this.myNetData.hasRemaining())
                        socketChannel.write(myNetData);
                }
                case BUFFER_OVERFLOW -> {
                    this.myNetData = enlargePacketBuffer(engine, this.myNetData);
                }
                case BUFFER_UNDERFLOW -> throw new SSLException("Buffer underflow occurred after a wrap.");
                case CLOSED -> {
                    closeConnection(socketChannel, engine);
                    return;
                }
                default -> throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
            }
        }
    }

    public String getMessage() {
        return this.message;
    }
}
