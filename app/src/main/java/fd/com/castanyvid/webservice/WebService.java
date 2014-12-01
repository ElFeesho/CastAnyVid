package fd.com.castanyvid.webservice;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by sawczc01 on 01/12/2014.
 */
public interface WebService {
    public interface Listener
    {
        public void complete() throws FileNotFoundException;
        public void error();
    }
    void startUp(Listener listener);

    void shutdown(Listener listener);

    String host(InputStream data);
}
