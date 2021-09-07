import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;
import java.util.logging.Logger;

public class ServerPart {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException,  ClassNotFoundException, InterruptedException {
        Logger logger = Logger.getLogger("serverPart");
        //создание «слушающего» сокета
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(1879);
        } catch (SocketException e) {
            logger.info("Can't make server socket. Server is turning off");
            System.exit(0);
        }
        Vector<Ticket> TicketCollection = null;
        CollectionParser collectionParser;
        while(true) {
            try {
                File file = null;
                // создаем сокет
                Socket server = serverSocket.accept();
                // создаем потоки
                DataOutputStream outputStream = new DataOutputStream(server.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(server.getInputStream());
                logger.info("Сокет активирован");
                // считываем коллекцию из файла
                collectionParser = new CollectionParser(file, outputStream);
                    try{
                        TicketCollection = collectionParser.readCollection(file);
                    } catch (FileNotFoundException e){
                    TicketCollection = new Vector<>();
                    TicketCollection.add(new Ticket(true));
                    System.out.println("Создали недо-коллекцию");}

                if (server.isConnected()) {
                    logger.info("server is connected");
                    CommandsRealize executor = new CommandsRealize(TicketCollection, false);
                    executor.execute(inputStream, outputStream);
                    logger.info("session ended. Waiting for new session ... ");
                }
            } catch (SocketException e) {
                System.out.println("something went wrong");
                Thread.sleep(100);
            }
        }
    }
}
