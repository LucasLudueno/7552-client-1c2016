package taller2.match_client;

/* Connection Exception is thrown when send Request failed */
public class ConnectionException extends Exception {
    public ConnectionException () {

    }

    public ConnectionException (String message) {
        super (message);
    }

    public ConnectionException (Throwable cause) {
        super (cause);
    }

    public ConnectionException (String message, Throwable cause) {
        super (message, cause);
    }
}