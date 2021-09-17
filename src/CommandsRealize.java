
import com.oracle.xmlns.internal.webservices.jaxws_databinding.ExistingAnnotationsType;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Scanner;
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


    public void execute(ObjectInputStream inputStream, DataOutputStream outputStream) throws ClassNotFoundException, ParserConfigurationException {
        // принимаем сообщение
        BufferedReader br = new BufferedReader(
                new InputStreamReader(System.in));


boolean as = true;

        boolean endOfStream = false;
        while (!endOfStream) {
            try {
                Thread. sleep(1000);
                if (!br.ready()) {

                        Message message = (Message) inputStream.readObject();
                        logger.info("message received");
                        if (message.isEnd) {
                            logger.info("Ctrl+D");
                            break;
                        }
                        if (message.type == Commander.CommandType.exit && !message.metaFromScript){
                            //br.ready();
                            endOfStream = true;
                        }
                    System.out.println("1");
                        if (!validate(message.ticket) || !(message.ticket instanceof Ticket)) throw new IOException();
                        //CommanderServ command2 = new CommanderServ(outputStream, new Scanner(System.in), message.ticket, TicketCollection, fromScript );
                        Commander command = new Commander(outputStream, message.argument, message.ticket, TicketCollection, fromScript);
                        command.TypeChanger(message.type);
                    System.out.println("2");
                        Thread.sleep(100);
                        command.run();
                    System.out.println("3");
                        //Thread.sleep(3000);
                        //            String input;
                        //         if (!br.ready()) {
                        //           System.out.println("not Input");
                        //     } else {
                        //       System.out.println("AOAOOA");
                        // }
                        //br.close();
                        Thread.sleep(1000);
                        //outputStream.flush();
                } else{
                    String commander = br.readLine();
                    if (commander.equals("save")){
                        System.out.println("SaveInput");
                              Ticket t = new Ticket();
                             Commander command = new Commander(outputStream, "save", t , TicketCollection, fromScript);
                             command.save();
                        System.out.println(" SAVED IT 2");
                        //   command.exit();
                    } else System.out.println("Incorrect Command");
                    //commander="";
                }
            } catch (Exception e) {
                //e.printStackTrace();
                logger.info("can't receive message");
                break;
            }
        }
    }

    public void executeserv() throws ClassNotFoundException, ParserConfigurationException, IOException {
        // принимаем сообщение
        try {
            boolean endOfStream2 = false;
            while (!endOfStream2) {
                Scanner scanner = new Scanner(System.in);
                Thread.sleep(3000);
                String m = null;
                if (scanner.hasNext()){
                     m = scanner.nextLine();
                } else System.out.println("You type: NOTHING");

                System.out.println("ВАШ ВВОД " + m);
                scanner.close();
                System.out.println("CLOSED");
                //endOfStream2 = true;
                //CommanderServ command2 = new CommanderServ(m, TicketCollection);
                //command2.run();
                //command2.run();
                // }catch (IOException e){      Scanner scanner = new Scanner(System.in);
                //                String m = scanner.nextLine();
                //                if (m=="save"){
                //                    System.out.println("ВАШ ВВОД LOX");
                //                    endOfStream = true;
                //                } else {
                //                    System.out.println("++ что-то другое" +m);
                //                }

                //     System.out.println("JOKER");
                //}
            }
        } catch (Exception e) {
            System.out.println("Except");
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