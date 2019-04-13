package models.api;

/**
 * @author Max Meijer
 * Created on 26/03/2019
 */
public class ApiError<T> {

    private T message;

    public ApiError() {}

    public ApiError(T message) {
        this.message = message;
    }

    public T getMessage() {
        return message;
    }

    public void setMessage(T message) {
        this.message = message;
    }
}
