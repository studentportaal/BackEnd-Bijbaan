package exception;

/**
 * @author Max Meijer
 * Created on 26/03/2019
 */
public class Util {

    private Util() {}

    public static Throwable getRootCause(Throwable t) {
        Throwable cause = null;
        Throwable result = t;

        while(null != (cause = result.getCause())  && (result != cause) ) {
            result = cause;
        }
        return result;
    }
}
