package sslengine;

import javax.net.ssl.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.SecureRandom;

public class SSLClient extends SSLPeer {
    private String remoteAddress;
    private int port;
    private SSLEngine engine;
    private SocketChannel socketChannel;
    private String message;

    public SSLClient(String protocol, String remoteAddress, int port) throws Exception {
        this.remoteAddress = remoteAddress;
        this.port = port;

        SSLContext context = SSLContext.getInstance(protocol);
        context.init(createKeyManagers("../../keys/client.keys", "123456", "123456"), createTrustMangers("../../keys/truststore", "123456"), new SecureRandom());
        engine = context.createSSLEngine(remoteAddress, port);
        engine.setUseClientMode(true);

        SSLSession session = engine.getSession();

        this.myAppData = ByteBuffer.allocate(1024);
        this.myNetData = ByteBuffer.allocate(session.getPacketBufferSize());
        this.peerAppData = ByteBuffer.allocate(1024);
        this.peerNetData = ByteBuffer.allocate(session.getPacketBufferSize());
    }

    public boolean connect() throws Exception {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking (false);
        socketChannel.connect(new InetSocketAddress(remoteAddress, port));
        while(!socketChannel.finishConnect()) {
             // ...
        }

        engine.beginHandshake();
        return doHandshake(socketChannel, engine);
    }

    public String read() throws Exception {
        return read(socketChannel, engine);
    }

    @Override
    protected String read(SocketChannel socketChannel, SSLEngine engine) throws Exception {
        this.peerNetData.clear();
        int waitToReadMillis = 50;
        while(true) {
            int bytesRead = socketChannel.read(this.peerNetData);
            if (bytesRead > 0) {
                this.peerNetData.flip();
                while (this.peerNetData.hasRemaining()) {
                    this.peerAppData.clear();
                    SSLEngineResult result = engine.unwrap(this.peerNetData, this.peerAppData);
                    switch (result.getStatus()) {
                        case OK -> {
                            this.peerAppData.flip();
                            this.message = new String(this.peerAppData.array());
                            return this.message;
                        }
                        case BUFFER_OVERFLOW -> this.peerAppData = enlargeApplicationBuffer(engine, this.peerAppData);
                        case BUFFER_UNDERFLOW -> this.peerNetData = handleBufferUnderflow(engine, this.peerNetData);
                        case CLOSED -> {
                            closeConnection(socketChannel, engine);
                            return null;
                        }
                        default -> throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
                    }
                }
            } else if (bytesRead < 0) {
                handleEndOfStream(socketChannel, engine);
                return null;
            }
            Thread.sleep(waitToReadMillis);
        }
    }

    public void write(String message) throws Exception {
        write(socketChannel, engine, message);
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
                        socketChannel.write(this.myNetData);
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

    public void shutdown() throws Exception {
        closeConnection(socketChannel, engine);
        executor.shutdown();
    }
}
