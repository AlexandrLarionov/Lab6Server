
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Vector;
import java.util.logging.Logger;

// принимает от клиента объект Message, преобразовывает его в команду и выполняет её
public class CommandsRealize {
    private Vector<Ticket> TicketCollection;
    private final boolean fromScript;
    private final Logger logger = Logger.getLogger("server.executor");
    public CommandsRealize(Vector<Ticket> TicketCollection, boolean fromScript) {
        this.TicketCollection = TicketCollection;
        this.fromScript = fromScript;
    }

    public CommandsRealize(boolean fromScript) {
        this.fromScript = fromScript;
    }

    public void execute(ObjectInputStream inputStream, DataOutputStream outputStream) throws ClassNotFoundException, ParserConfigurationException {
        // принимаем сообщение
        boolean endOfStream = false;
        while (!endOfStream) {
            try {
                Message message = (Message) inputStream.readObject();
                logger.info("message received");
                if (message.isEnd) {
                    logger.info("Ctrl+D ??");
                    break;
                }
                if (message.type == Commander.CommandType.exit && !message.metaFromScript)
                    endOfStream = true;                                                   // заканчиваем принимать сообщения после команды exit не из скрипта
                if (!validate(message.ticket) || !(message.ticket instanceof Ticket)) throw new IOException();
                Commander command = new Commander(outputStream, message.argument, message.ticket, TicketCollection, fromScript);
                command.TypeChanger(message.type);
                command.run();
            } catch (IOException e) {
                e.printStackTrace();
                logger.info("can't receive message");
                break;
            }
        }

    }
    public boolean validate(Ticket ticket) {
        try {
            Ticket ticket1 = new Ticket(ticket.getId(), ticket.getName(), ticket.getCoords(), ticket.getCreationDate(),
                    ticket.getPrice(), ticket.getEvent(), ticket.getType());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}