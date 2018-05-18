package production;

import javax.mail.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ImapIterator extends AIterator {
    private final static String PROTOCOL = "imaps";
    private final static String PLAIN_TEXT_TYPE = "text/plain";
    private final static String MULTIPART_TYPE = "multipart/*";
    private final static String APPLICATION_TYPE = "application/*";

    private Store store;
    private Folder inboxFolder;
    private Folder exceptionFolder;

    private Message[] messages;

    private String inboxFolderLabel;
    private String exceptionFolderLabel;

    private ArrayList<File> attachments;

    public ImapIterator() {
        inboxFolderLabel = "INBOX";
        exceptionFolderLabel = "EXCEPTIONS";
        attachments = new ArrayList<File>();

        Session session = Session.getInstance(System.getProperties());
        try {
            store = session.getStore(PROTOCOL);
        } catch (NoSuchProviderException e) {
            System.out.println(e.getMessage());
        }
    }

    public void open(String host, String user, String password) {
        try {
            store.connect(host, user, password);
            openInboxFolder(store);
            openExceptonFolder(store);
            getMessagesFromInbox();
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }
    }

    public void close() {
        try {
            closeExceptinoFolder();
            closeInboxFolder();
            closeStore();
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }
    }

    public void moveToException() {
        try {
            Message[] messages = new Message[1];
            messages[0] = this.messages[position];
            exceptionFolder.appendMessages(messages);
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete() {
        try {
            Message[] messages = new Message[1];
            messages[0] = this.messages[position];
            inboxFolder.setFlags(messages, new Flags(Flags.Flag.DELETED), true);
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }
    }

    public MessageEntity getMessage() {
        return formateMessage(messages[position]);
    }

    public void setInboxFolderLabel(String inboxFolderLabel) {
        this.inboxFolderLabel = inboxFolderLabel;
    }

    public void setExceptionFolderLabel(String exceptionFolderLabel) {
        this.exceptionFolderLabel = exceptionFolderLabel;
    }

    private void openInboxFolder(Store store) throws MessagingException {
        inboxFolder = store.getFolder(inboxFolderLabel);
        inboxFolder.open(Folder.READ_WRITE);
    }

    private void openExceptonFolder(Store store) throws MessagingException {
        exceptionFolder = store.getFolder(exceptionFolderLabel);
        exceptionFolder.open(Folder.READ_WRITE);
    }

    private void getMessagesFromInbox() throws MessagingException {
        messages = inboxFolder.getMessages();
        count = messages.length;
    }

    private void closeStore() throws MessagingException {
        if (store != null) {
            store.close();
        }
    }

    private void closeExceptinoFolder() throws MessagingException {
        if (exceptionFolder != null) {
            exceptionFolder.close();
        }
    }

    private void closeInboxFolder() throws MessagingException {
        if (inboxFolder != null) {
            inboxFolder.close();
        }
    }

    private MessageEntity formateMessage(Message message) {
        MessageEntity messageEntity = new MessageEntity();
        try {
            messageEntity.setFromAddres(message.getFrom()[0].toString());
            messageEntity.setSubject(message.getSubject());
            messageEntity.setSendDate(message.getSentDate());
            messageEntity.setReceivedDate(message.getReceivedDate());
            messageEntity.setPlainText(getParsedMessageText(message));
            messageEntity.setAttachments(attachments);
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return messageEntity;
    }

    private String getParsedMessageText(Message message) throws MessagingException, IOException {
        return message.isMimeType(PLAIN_TEXT_TYPE) ? message.getContent().toString() :
                parseMultipart((Multipart) message.getContent());
    }

    private String parseMultipart(Multipart multipart) throws MessagingException, IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (bodyPart.isMimeType(PLAIN_TEXT_TYPE)) {
                stringBuilder.append(bodyPart.getContent().toString());
            } else if (bodyPart.isMimeType(MULTIPART_TYPE)) {
                stringBuilder.append(parseMultipart((Multipart) bodyPart.getContent()));
            } else if (bodyPart.isMimeType(APPLICATION_TYPE)) {
                handleAttachment(bodyPart);
            }
        }
        return stringBuilder.toString();
    }

    private void handleAttachment(BodyPart part) throws MessagingException, IOException {
        String filename = part.getFileName();
        if (filename.matches(".+\\.csv$")) {
            InputStream inputStream = part.getInputStream();
            File file = new File("/var/tmp/"+filename);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int byteRead;
            if ((byteRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, byteRead);
            }
            outputStream.close();
            attachments.add(file);
        }
    }
}
