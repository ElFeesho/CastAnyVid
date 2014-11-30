package fd.com.castanyvid;

/**
 * Created by chris on 28/11/14.
 */
public class Duration {
    public final long milliseconds;

    public Duration(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    @Override
    public String toString() {
        long seconds = milliseconds / 1000;
        long hours = seconds / (60 * 60);
        seconds -= hours * (60 * 60);
        long minutes = seconds / 60;
        seconds -= minutes * 60;

        return String.format("%s%02d:%02d", hours > 0 ? (hours + ":") : (""), minutes, seconds);
    }
}
