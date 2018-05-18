package production;

public abstract class AIterator implements IIterator {
    protected int count;
    protected int position;

    public abstract void open(String host, String user, String password);
    public abstract void close();
    public abstract void moveToException();
    public abstract void delete();

    public abstract MessageEntity getMessage();

    public boolean hasNext() {
        return count > 0 && position < count;
    }

    public void toNext() {
        position++;
    }
}
