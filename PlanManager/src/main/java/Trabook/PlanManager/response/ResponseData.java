package Trabook.PlanManager.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ResponseData<T> {
    private int statusCode;
    private String message;
    private T data;

    public ResponseData(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public static<T> ResponseData<T> res(final int statusCode, final  String message) {
        return res(statusCode,message,null);
    }
    public static<T> ResponseData<T> res(final int statusCode,final  String message,final  T t){
        return ResponseData.<T>builder()
                .data(t)
                .statusCode(statusCode)
                .message(message).build();
    }
}


