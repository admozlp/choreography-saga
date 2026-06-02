package choreographysaga.payment.util;

public record Constant(

) {
    public static final String ERROR = "error";
    public static final String SUCCESS = "success";
    public static final String REDIRECT_URL = "redirectUrl";
    public static final String MESSAGE = "message";
    public static final String ERROR_URL = "http://localhost:3530/payments/error";
    public static final String SUCCESS_URL = "http://localhost:3530/payments/success";

}
