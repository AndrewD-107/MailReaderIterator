import production.ImapIterator;
import production.MessageEntity;

import java.io.File;

public class Application {
    private static final String user = "user";
    private static final String password = "password";
    private static final String host = "imap.gmail.com";

    public static void main(String[] args) {
        ImapIterator imapReader = new ImapIterator();
        imapReader.open(host, user, password);
        while (imapReader.hasNext()) {
            MessageEntity message = imapReader.getMessage();

            System.out.println(message.getFromAddres());
            System.out.println(message.getSubject());
            System.out.println(message.getSendDate());
            System.out.println(message.getReceivedDate());
            System.out.println(message.getPlainText());

//            imapReader.setExceptionFolderLabel("SPECIAL_FOLDER");
//            imapReader.moveToException(); // If you need move messesage to special folder

            imapReader.delete();
            imapReader.toNext();

            if (message.hasAttachments()) {
                for (File attachment : message.getAttachments()) {
                    attachment.renameTo(new File("/Users/andrew/upload/" + attachment.getName()));
                }
            }
        }

        imapReader.close();
    }
}