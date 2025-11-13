package DumbAndDumber.Danchive.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final boolean success;
    private final String message;
    private final T data;

    public ApiResponse(boolean success, String message, T data){
        this.success = success; this.message = message; this.data = data;
    }
    public static <T> ApiResponse<T> success(String msg, T data){ return new ApiResponse<>(true, msg, data); }
    public static <T> ApiResponse<T> success(String msg){ return new ApiResponse<>(true, msg, null); }
    public static <T> ApiResponse<T> fail(String msg){ return new ApiResponse<>(false, msg, null); }

    public boolean isSuccess(){ return success; }
    public String getMessage(){ return message; }
    public T getData(){ return data; }
}