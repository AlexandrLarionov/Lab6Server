import javax.xml.parsers.ParserConfigurationException;
        import java.io.*;
        import java.util.*;
        import java.util.logging.Logger;
        import java.util.stream.Collectors;
        import java.util.stream.Stream;


public class CommanderServ {
    String argument = "";
    Vector<Ticket> TicketCollection;
    boolean exitStatus = false;
    Scanner scanner;
    private CommandType type = CommandType.help;
    Ticket ticket;
    Logger logger = Logger.getLogger("server.command");
    public CommanderServ(String argument,  Vector<Ticket> TicketCollection) {
        this.argument = argument;
        this.TicketCollection = TicketCollection;
    }
    public void ArgumentChanger(String argument) {
        this.argument = argument;
    }
  //  public void TypeChanger(CommandType type) {
    //    this.type = type;
   // }
    public void run() throws IOException {
        logger.info("running command");
        try {
            switch (type) {
                case help: this.help();break;
                case saveServ: this.saveServ();break;
            }
        }   catch (NullPointerException ignored) {}
    }


    public void help() throws IOException {
        logger.info("'help' command was detected");
        System.out.println("help : вывести справку по доступным командам\n" +
                "saveServ : сохранить коллекцию в файл\n");
    }

    public void saveServ() throws  IOException {
        logger.info("saving to disk");
        File Input = null;
        Scanner scan = null;
        try {
            Input = new File(System.getenv("Input"));      // проверка на наличие переменной окружения
            scan = new Scanner(Input);
        } catch (NullPointerException e) {
            System.out.println("Cant find env variable");
            System.exit(0);
        }catch (FileNotFoundException e) {   // неправильный путь к файлу или нет доступа на чтение
            System.out.println("File not found");
            System.exit(0);
        }
        try {
            FileWriter writter = new FileWriter(Input);
            writter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + "<Ticket>\n");
            for (Ticket ticket : TicketCollection){
                String id =  ticket.getId().toString();
                String name = ticket.getName();
                String coordinates = ticket.getCoords().getX() + " " + ticket.getCoords().getY();
                String idevent = ticket.event.getIdTicket().toString();
                String eventname = ticket.event.getNameTicket();
                String eventage = ticket.event.getMinAge().toString();
                String eventcount = ticket.event.getTicketsCount().toString();
                String ticketdate = ticket.getCreationDate().toString();
                String typeField = ticket.type.toString();
                String price = Double.toString(ticket.getPrice());
                writter.write("<ticket " + "id='"+id+"'"+" name='"+name+"' "+"coordinates='"+coordinates+"' "+"eventid='"+idevent+"' "
                        + "eventname='"+eventname+"' "+" eventage='"+eventage+"' "+" eventcount='"+eventcount+"' "
                        +" creation_date='"+ticketdate+"' "+" type='"+typeField+"' "+" price='"+price+"' "+" />\n");
            }
            writter.write("</Ticket>");
            writter.close();
            System.out.println("The command was executed");
            logger.info("Collection was saved to disk");
        } catch (NullPointerException e) {
            logger.info("Collection was not saved to disk");
        }
    }


    /**
     * Method used when 'show' command is called
     * @param ticket ticket which description need to be shown
     * @return String description
     */
    public static String extendedDescription(Ticket ticket) {
        return Stream.of(ticket.getId(), ticket.getCoords().getX(), ticket.getCoords().getY(), ticket.getName(),
                ticket.getPrice(), ticket.getCreationDate(), ticket.getEvent().getTicketsCount(), ticket.getEvent().getIdTicket(),
                ticket.getEvent().getMinAge(), ticket.getEvent().getNameTicket()).map(Object::toString).collect(Collectors.joining(", "));
    }

    public enum CommandType {
        saveServ, help
    }
}
