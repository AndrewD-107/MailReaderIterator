package production;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class MessageEntity {
    private String fromAddres;
    private String subject;
    private String plainText;
    private Date sendDate;
    private Date receivedDate;
    private ArrayList<File> attachments;

    public MessageEntity() {
        attachments = new ArrayList<File>();
    }

    public boolean hasAttachments() {
        return !attachments.isEmpty();
    }

    public void setAttachments(ArrayList<File> attachments) {
        this.attachments = attachments;
    }

    public ArrayList<File> getAttachments() {
        return attachments;
    }

    public String getFromAddres() {
        return fromAddres;
    }

    public void setFromAddres(String fromAddres) {
        this.fromAddres = fromAddres;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPlainText() {
        return plainText;
    }

    public void setPlainText(String plainText) {
        this.plainText = plainText;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }
}
