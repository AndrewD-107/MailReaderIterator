package production;

public interface IIterator {
    void open(String host, String user, String password);
    void close();
    boolean hasNext();
    void toNext();
    MessageEntity getMessage();
    void moveToException();
    void delete();
}
