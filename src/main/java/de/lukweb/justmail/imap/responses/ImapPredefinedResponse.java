package de.lukweb.justmail.imap.responses;

public enum ImapPredefinedResponse {

    BAD_INVALID_ARGUMENTS(ImapResponse.BAD, "arguments invalid"),
    BAD_COMMNAD_UNKNOW(ImapResponse.BAD, "command unknown");

    private ImapResponse response;
    private String responseText;

    ImapPredefinedResponse(ImapResponse response, String responseText) {
        this.response = response;
        this.responseText = responseText;
    }

    public String create(String tag) {
        return response.create(tag, responseText);
    }

}
