import java.io.Serializable;
// класс команды, который передается от клиента на сервер
public class Messege implements Serializable {
    Commander.CommandType type;
    String argument;
    Ticket ticket = new Ticket();
    boolean metaFromScript;
    boolean isEnd = false;
    public Message(boolean isEnd) {
        this.isEnd = isEnd;
    }
    public Messege(Commander.CommandType type, String argument, boolean metaFromScript) {
        this.argument = argument;
        this.type = type;
        this.metaFromScript = metaFromScript;
    }
    public Messege(Ticket ticket, Commander.CommandType type, String argument, boolean metaFromScript) {
        this.argument = argument;
        this.type = type;
        this.ticket = ticket;
        this.metaFromScript = metaFromScript;
    }
}
