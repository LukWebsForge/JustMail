package de.lukweb.justmail.smtp;

public enum Response {

    SYSTEM_STATUS(211, "%s"),
    HELP_MESSAGE(214, "%s"),
    SERVICE_READY(220, "%s Service ready"),
    SERVICE_CLOSING(221, "Have a nice day"),
    AUTH_SUCCESSFUL(235, "2.7.0 Authentication successful"),
    ACTION_OKAY(250, "Ok"),
    FORWARD_USER(251, "User not local; will forward to %s"),
    CONTINE_AUTH(334, " "),
    START_DATA(354, "Start mail input; end with . "),
    SERVICE_NOT_AVAILABLE(421, "%s Service not available, closing transmission channel"),
    MAILBOX_BUSY(450, "Requested mail action not taken: mailbox unavailable"),
    ACTION_ABORTED(451, "Requested action aborted: local error in processing"),
    INSUFFICIENT_STORAGE(452, "Requested action not taken: insufficient system storage"),
    TLS_NOT_AVAILABLE(454, "TLS not available due to temporary reason"),
    COMMAND_UNRECOGNIZED(500, "Syntax error, command unrecognized"),
    ARGUMENT_ERROR(501, "Synatx error in parameters or arguments"),
    COMMAND_NOT_IMPLEMENTED(502, "SmtpCommand not implemented"),
    BAD_SEQUENCE(503, "Bad sequence of commands"),
    ARGUMENT_NOT_IMPLEMENTED(504, "SmtpCommand parameter not implemented"),
    AUTH_REQUIRED(530, "Authentication required"),
    MUST_USE_TLS(530, "Must issue a STARTTLS command first"),
    INVALID_CREDENTIALS(535, "Authentication credentials invalid"),
    MAILBOX_NOT_FOUND(550, "Mailbox counldn't be found"),
    USER_NOT_LOCAL(551, "User not local; please try %s"),
    NO_STORAGE_LEFT(552, "Requested mail action aborted: exceeded storage allocation"),
    MAILBOX_NAME_NOT_ALLOWED(553, "Requested action not taken: mailbox name not allowed"),
    TRANSACTION_FAILED(554, "Transaction failed");

    private int code;
    private String message;

    Response(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String customMessage(String message) {
        return code + " " + message + "\r\n";
    }

    public String create(String... data) {
        String cache = message;
        for (String s : data) cache = cache.replaceFirst("%s", s);
        return customMessage(cache);
    }

    public int getCode() {
        return code;
    }

    //////////////////////////////////////////////////

    public static Response getByCode(int code) {
        for (Response response : values()) if (response.getCode() == code) return response;
        return null;
    }

}
