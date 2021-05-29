package sslengine;

import Threads.ThreadPool;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.KeyStore;

public abstract class SSLPeer {

    // Will contain this peer's application data in plaintext
    protected ByteBuffer myAppData;

    // Will contain this peer's encrypted data
    protected ByteBuffer myNetData;

    // Will contain the other peer's (decrypted) application data
    protected ByteBuffer peerAppData;

    // Will contain the other peer's encrypted data
    protected ByteBuffer peerNetData;

    protected int port;

    // protected abstract void read(SocketChannel socketChannel, SSLEngine engine) throws Exception;
    protected abstract String read(SocketChannel socketChannel, SSLEngine engine) throws Exception;

    protected abstract void write(SocketChannel socketChannel, SSLEngine engine, String message) throws Exception;

    protected boolean doHandshake(SocketChannel socketChannel, SSLEngine engine) {
        try {
            //System.out.println("Starting handshake protocol...");

            SSLEngineResult result;
            SSLEngineResult.HandshakeStatus handshakeStatus;

            int appBufferSize = engine.getSession().getApplicationBufferSize();
            ByteBuffer myAppData = ByteBuffer.allocate(appBufferSize);
            ByteBuffer peerAppData = ByteBuffer.allocate(appBufferSize);

            this.myNetData.clear();
            this.peerNetData.clear();

            handshakeStatus = engine.getHandshakeStatus();
            while (handshakeStatus != SSLEngineResult.HandshakeStatus.FINISHED && handshakeStatus != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
                switch (handshakeStatus) {
                    case NEED_UNWRAP -> {
                        if (socketChannel.read(this.peerNetData) < 0) {
                            if (engine.isInboundDone() && engine.isOutboundDone())
                                return false;
                            try {
                                engine.closeInbound();
                            } catch (SSLException e) {
                                System.err.println("This engine was forced to close inbound, without having received the proper SSL/TLS close notification message from the peer, due to end of stream.");
                            }
                            engine.closeInbound();
                            handshakeStatus = engine.getHandshakeStatus();
                            break;
                        }
                        this.peerNetData.flip();
                        try {
                            result = engine.unwrap(this.peerNetData, this.peerAppData);
                            this.peerNetData.compact();
                            handshakeStatus = result.getHandshakeStatus();
                        } catch (SSLException e) {
                            System.err.println("A problem was found while processing the data that caused the SSLEngine to abort. Will try to properly close connection...");
                            engine.closeOutbound();
                            handshakeStatus = engine.getHandshakeStatus();
                            break;
                        }
                        switch (result.getStatus()) {
                            case OK -> { break; }
                            case BUFFER_OVERFLOW -> {
                                // peerAppData's capacity is smaller than the data derived from peerNetData's unwrap
                                this.peerAppData = enlargeApplicationBuffer(engine, this.peerAppData);
                                break;
                            }
                            case BUFFER_UNDERFLOW -> {
                                this.peerNetData = handleBufferUnderflow(engine, this.peerNetData);
                                break;
                            }
                            case CLOSED -> {
                                if (engine.isOutboundDone())
                                    return false;
                                else {
                                    engine.closeOutbound();
                                    handshakeStatus = engine.getHandshakeStatus();
                                    break;
                                }
                            }
                            default -> throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
                        }
                        break;

                    }
                    case NEED_WRAP -> {
                        this.myNetData.clear();
                        try {
                            result = engine.wrap(this.myAppData, this.myNetData);
                            handshakeStatus = result.getHandshakeStatus();
                        } catch (SSLException e) {
                            System.err.println("A problem was found while processing the data that caused the SLLEngine to abort. Will try to properly close connection...");
                            engine.closeOutbound();
                            handshakeStatus = engine.getHandshakeStatus();
                            break;
                        }
                        switch (result.getStatus()) {
                            case OK -> {
                                this.myNetData.flip();
                                while (this.myNetData.hasRemaining()) {
                                    socketChannel.write(this.myNetData);
                                }
                                break;
                            }
                            case BUFFER_OVERFLOW -> {
                                // there is not enough space in myNetData buffer to write all the data that would be generated by the method wrap.
                                this.myNetData = enlargePacketBuffer(engine, this.myNetData);
                                break;
                            }
                            case BUFFER_UNDERFLOW -> throw new SSLException("Buffer underflow occurred after a wrap.");
                            case CLOSED -> {
                                try {
                                    this.myNetData.flip();
                                    while (this.myNetData.hasRemaining()) {
                                        socketChannel.write(this.myNetData);
                                    }
                                    this.peerNetData.clear();
                                } catch (Exception e) {
                                    //System.err.println("Failed to send server's CLOSE message due to socket channel's failure.");
                                    handshakeStatus = engine.getHandshakeStatus();
                                }
                                break;
                            }
                            default -> throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
                        }
                    }
                    case NEED_TASK -> {
                        Runnable task;
                        while((task = engine.getDelegatedTask()) != null)
                            ThreadPool.getInstance().execute(task);
                        handshakeStatus = engine.getHandshakeStatus();
                        break;
                    }
                    case FINISHED, NOT_HANDSHAKING -> {
                        break;
                    }
                    default -> throw new IllegalStateException("Invalid SSL status: " + handshakeStatus);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    protected ByteBuffer enlargePacketBuffer(SSLEngine engine, ByteBuffer buffer) {
        return enlargeBuffer(buffer, engine.getSession().getPacketBufferSize());
    }

    protected ByteBuffer enlargeApplicationBuffer(SSLEngine engine, ByteBuffer buffer) {
        return enlargeBuffer(buffer, engine.getSession().getApplicationBufferSize());
    }

    protected ByteBuffer enlargeBuffer(ByteBuffer buffer, int sessionProposedCapacity) {
        if (sessionProposedCapacity > buffer.capacity())
            buffer = ByteBuffer.allocate(sessionProposedCapacity);
        else
            buffer = ByteBuffer.allocate(buffer.capacity() * 2);
        return buffer;
    }

    protected ByteBuffer handleBufferUnderflow(SSLEngine engine, ByteBuffer buffer) {
        if (engine.getSession().getPacketBufferSize() < buffer.limit())
            return buffer;
        else {
            ByteBuffer replaceBuffer = enlargePacketBuffer(engine, buffer);
            buffer.flip();
            replaceBuffer.put(buffer);
            return replaceBuffer;
        }
    }

    protected void closeConnection(SocketChannel socketChannel, SSLEngine engine) {
        try {
            engine.closeOutbound();
            doHandshake(socketChannel, engine);
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void handleEndOfStream(SocketChannel socketChannel, SSLEngine engine) {
        try {
            engine.closeInbound();
        } catch (Exception e) {
            System.err.println("This engine was forced to close inbound, without having received the proper SSL/TLS close notification message from the peer, due to end of stream.");
        }
        closeConnection(socketChannel, engine);
    }

    protected KeyManager[] createKeyManagers(String filepath, String keystorePassword, String keyPassword) throws Exception {

        KeyStore keyStore = KeyStore.getInstance("JKS");

        try (InputStream keyStoreIS = new FileInputStream(filepath)) {
            keyStore.load(keyStoreIS, keystorePassword.toCharArray());
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyPassword.toCharArray());
        return kmf.getKeyManagers();
    }

    protected TrustManager[] createTrustMangers(String filepath, String keystorePassword) throws Exception {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (InputStream trustStoreIS = new FileInputStream(filepath)) {
            trustStore.load(trustStoreIS, keystorePassword.toCharArray());
        }
        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustFactory.init(trustStore);
        return trustFactory.getTrustManagers();
    }

    public int getPort() {
        return this.port;
    }
}
