package org.project.model.response;

public class ObjectResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public ObjectResponse() {
    }

    public ObjectResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ObjectResponse<T> success(T data) {
        return new ObjectResponse<>(true, "OK", data);
    }

    public static <T> ObjectResponse<T> fail(String message) {
        return new ObjectResponse<>(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
