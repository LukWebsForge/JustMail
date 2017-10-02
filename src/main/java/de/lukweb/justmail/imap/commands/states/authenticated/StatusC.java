package de.lukweb.justmail.imap.commands.states.authenticated;

import de.lukweb.justmail.imap.ImapSession;
import de.lukweb.justmail.imap.commands.objects.ImapCommand;
import de.lukweb.justmail.imap.responses.ImapResponse;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.objects.Mail;
import de.lukweb.justmail.sql.objects.Mailbox;
import de.lukweb.justmail.sql.storages.Mailboxes;
import de.lukweb.justmail.sql.storages.Mails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StatusC extends ImapCommand {

    public StatusC() {
        super("status");
    }

    @Override
    public void execute(String[] arguments, String tag, ImapSession session) {

        if (!session.checkForAuthentication(tag)) return;

        if (arguments.length < 2) {
            session.send(ImapResponse.BAD.create(tag, "Invalid arguments"));
            return;
        }

        // todo add split thing
        Mailbox mailbox = Storages.get(Mailboxes.class).get(session.getUser(), arguments[0]);
        if (mailbox == null) {
            session.send(ImapResponse.NO.create(tag, "Mailbox does not exist"));
            return;
        }


        String statusData = Arrays.toString(arguments).split("\\(")[1].split("\\)")[0];
        String statusResult = "";
        if(statusData.contains(" ")){
            String[] statusRequests = statusData.split(", ");
            for(String status : statusRequests){
                if(statusRequests[0].equals(status)){
                    statusResult += getStatus(status, mailbox);
                }else{
                   statusResult += " " + getStatus(status, mailbox);
                }
            }
        } else {
            statusResult = getStatus(statusData, mailbox);
        }

        session.send("* STATUS " + arguments[0]+ " ("+ statusResult + ")");

        session.send(ImapResponse.OK.create(tag, "STATUS completed"));


    }

    private String getStatus(String status, Mailbox mailbox) {
        String result = "";

        Mails mails = Storages.get(Mails.class);
        List<Mail> userMail = mails.getAll(mailbox);
        List<Mail> unreadMails = userMail.stream().filter(mail -> !mail.isRead())
                .sorted((o1, o2) -> o1.getId() > o2.getId() ? 1 : -1).collect(Collectors.toList());

        switch (status) {
            case "MESSAGES":
                result = "MESSAGES " + userMail.size();
                break;
            case "RECENT":
                result = "RECENT " + unreadMails.stream().count();
                break;
            case "UIDNEXT":
                result = "UIDNEXT " + mails.getMaxUID()+1;
                break;
            case "UIDVALIDITY":
                result = "UIDVALIDITY 3857529045";
                break;
            case "UNSEEN":
                result = "UNSEEN " + unreadMails.stream().count();
                break;
        }
        return result;
    }
}
