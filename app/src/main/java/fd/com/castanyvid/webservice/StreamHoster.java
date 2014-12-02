package fd.com.castanyvid.webservice;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class StreamHoster {
    private final String streamToHost;

    public static class StreamProvider
    {
        private final String filePath;

        public StreamProvider(String filePath)
        {
            this.filePath = filePath;
        }

        public InputStream streamForByteRangeStart(long byteRange) throws IOException {
            FileInputStream fin = new FileInputStream(filePath);
            fin.skip(byteRange);
            return fin;
        }

        public long rangeLeftFrom(long byteRange)
        {
            return new File(filePath).length() - byteRange;
        }
    }

    private Runnable hostRunnable = new Runnable()
    {
        private boolean alive = true;
        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(8081);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            while(alive) {
                try {
                    // Read the request and figure out what we're about to do
                    Socket client = serverSocket.accept();
                    InputStream clientInput = client.getInputStream();

                    byte[] data = new byte[4096];
                    boolean readHeader = false;
                    String headerBuffer = "";
                    while(!readHeader)
                    {
                        int bytesRead = clientInput.read(data, 0, 4096);
                        headerBuffer+=new String(data, 0, bytesRead);
                        readHeader = headerBuffer.endsWith("\r\n\r\n");
                    }

                    Log.d("STREAMHOSTER", "Got header: " + headerBuffer);

                    long byteRangeStart = getByteRangeStart(headerBuffer);

                    StreamProvider streamProvider = new StreamProvider(streamToHost);
                    long contentLength = streamProvider.rangeLeftFrom(byteRangeStart);
                    InputStream consumerStream = streamProvider.streamForByteRangeStart(byteRangeStart);

                    long bytesWritten = 0;
                    int bytesJustSent = 0;
                    OutputStream clientOutputStream = client.getOutputStream();
                    clientOutputStream.write(("HTTP/1.1 200 OK\r\nContent-Length: "+contentLength+"\r\n\r\n").getBytes());
                    do
                    {
                        bytesWritten+=bytesJustSent;
                        int streamBytesRead = consumerStream.read(data, 0, 4096);
                        if(streamBytesRead < 0)
                        {
                            alive = false;
                            break;
                        }
                        clientOutputStream.write(data, 0, streamBytesRead);
                        bytesJustSent = streamBytesRead;
                    } while(bytesWritten != contentLength && bytesJustSent != -1);

                    clientInput.close();
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    alive = false;
                }
            }

            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private long getByteRangeStart(String headerBuffer) {
        String truncHeader = headerBuffer.substring(headerBuffer.indexOf("Range: bytes=")+13);
        String byteRange = truncHeader.substring(0, truncHeader.indexOf("\r\n"));
        String[] rangeComponents = byteRange.split("-");
        return Long.parseLong(rangeComponents[0]);
    }

    public StreamHoster(String stream)
    {
        streamToHost = stream;
    }

    public void startHosting() {
        new Thread(hostRunnable).start();
    }
}
