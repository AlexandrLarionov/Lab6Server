import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
public class ServerPart {
    //static ExecutorService executeIt = Executors.newFixedThreadPool(2);
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException,  ClassNotFoundException, InterruptedException {
        Logger logger = Logger.getLogger("serverPart");
        //создание «слушающего» сокета
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(1279);
        } catch (SocketException e) {
            logger.info("Can't make server socket. Server is turning off");
            System.exit(0);
        }

        int counter = 0;
        boolean sec = true;




        try {
                while (true) {
                    //  File file = null;
                    // создаем сокет

                    // считываем коллекцию из файла
                    Socket server = serverSocket.accept();

                    // создаем потоки
                    ServerClientThread sct = new ServerClientThread(server, counter);

                    //ServerCommand com = new ServerCommand(TicketCollection);
                    // boolean needNormalMode = true;
                    sct.start();

                    //executeIt.execute(new MonoThreadClientHandler(server));

                    // while (needNormalMode) {
                    //     needNormalMode =
                    //com.read(new Scanner(System.in));
                    //}
                    //  boolean sak = false;
                    //    while (sak){
                    //    com.saveServ();
                    //    }
                    //   ServerClientThread sct2 = new ServerClientThread(server,counter);
                    // sct2.start();

                }
            } catch (SocketException e) {
                System.out.println("something went wrong");
                Thread.sleep(100);
            }
            //executeIt.shutdown();
        }
}

class ServerClientThread extends Thread {
    Socket server;
    File file = null;
    Logger logger;
    Vector<Ticket> TicketCollection;
    CollectionParser collectionParser;
    int clientNo;
    int squre;
    ServerClientThread(Socket inSocket,int counter){
        server = inSocket;
        clientNo=counter;
    }
    public void run(){
        try{

    //        while(!clientMessage.equals("bye")){
      //          clientMessage=inStream.readUTF();
        //        System.out.println("From Client-" +clientNo+ ": Number is :"+clientMessage);
          //      squre = Integer.parseInt(clientMessage) * Integer.parseInt(clientMessage);
            //    serverMessage="From Server to Client-" + clientNo + " Square of " + clientMessage + " is " +squre;
              //  outStream.writeUTF(serverMessage);
                //outStream.flush();
                  // }

            DataOutputStream outputStream = new DataOutputStream(server.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(server.getInputStream());
            System.out.println("Сокет активирован");
            collectionParser = new CollectionParser();//, outputStream);

            try {
                TicketCollection = collectionParser.readCollection();
            } catch (Exception e) {
                TicketCollection = new Vector<>();
                TicketCollection.add(new Ticket(true));
                System.out.println("Создали недо-коллекцию");
            }
            if (server.isConnected()) {
                System.out.println("server is connected");
                //Scanner console = new Scanner(System.in);
                //   String command = console.nextLine();
                CommandsRealize executor = new CommandsRealize(TicketCollection, true);
                //executor.executeserv();
                executor.execute(inputStream, outputStream);
                System.out.println("CLient finished");
            } else System.out.println("THERE");
        }catch(NullPointerException ex){
            System.out.println("Null at saving");
        } catch (Exception p){
            System.out.println("Problems with server saving");
        }
    }



}

class MonoThreadClientHandler implements Runnable {

    Socket server;
    Vector<Ticket> TicketCollection;
    CollectionParser collectionParser;
    int clientNo;

    public MonoThreadClientHandler(Socket server) {
        this.server = server;
    }

    @Override
    public void run() {

        try {
            // инициируем каналы общения в сокете, для сервера

            // канал записи в сокет следует инициализировать сначала канал чтения для избежания блокировки выполнения программы на ожидании заголовка в сокете
            DataOutputStream outputStream = new DataOutputStream(server.getOutputStream());

// канал чтения из сокета
            ObjectInputStream inputStream = new ObjectInputStream(server.getInputStream());
            //DataInputStream in = new DataInputStream(clientDialog.getInputStream());
            System.out.println("DataInputStream created");

            System.out.println("DataOutputStream  created");
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // основная рабочая часть //
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // начинаем диалог с подключенным клиентом в цикле, пока сокет не
            // закрыт клиентом
            while (!server.isClosed()) {

                System.out.println("Server reading from channel");

                // серверная нить ждёт в канале чтения (inputstream) получения
                // данных клиента после получения данных считывает их

                collectionParser = new CollectionParser();//, outputStream);

                try {
                    TicketCollection = collectionParser.readCollection();
                } catch (Exception e) {
                    TicketCollection = new Vector<>();
                    TicketCollection.add(new Ticket(true));
                    System.out.println("Создали недо-коллекцию");
                }
                if (server.isConnected()) {
                    System.out.println("server is connected");
                    //Scanner console = new Scanner(System.in);
                    //   String command = console.nextLine();
                    CommandsRealize executor = new CommandsRealize(TicketCollection, true);
                    // executor2.executeserv();
                    executor.execute(inputStream, outputStream);

                    System.out.println("CLient finished");
                } else System.out.println("THERE");


                // и выводит в консоль

                // если условие окончания работы не верно - продолжаем работу -
                // отправляем эхо обратно клиенту


                // освобождаем буфер сетевых сообщений
                outputStream.flush();

                // возвращаемся в началло для считывания нового сообщения
            }

            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // основная рабочая часть //
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // если условие выхода - верно выключаем соединения

            System.out.println("Closing connections & channels.");

            // закрываем сначала каналы сокета !
            inputStream.close();
            outputStream.close();

            // потом закрываем сокет общения с клиентом в нити моносервера
            server.close();

            System.out.println("Closing connections & channels - DONE.");
        } catch (IOException e) {
            System.out.println("except1");

        }  catch (Exception e) {
            System.out.println("except2");
        }
    }
}




class ServerCommand extends Thread{
    Vector<Ticket> TicketCollection;
    CollectionParser collectionParser;
    Scanner scanner;
    ServerCommand (Vector<Ticket> TicketCollection){
        this.TicketCollection = TicketCollection;
    }

    public void read(Scanner scanner) throws IOException {
        boolean exitStatus = false;
        Ticket ticket = new Ticket();

        boolean wasEnter = false;
        while (!exitStatus) {
            try{
                String[] text = null;
                if (!wasEnter) System.out.println("Enter command");
                wasEnter = false;
                if (scanner.hasNext()) {
                    String textline = scanner.nextLine();
                    if (textline.trim().isEmpty()) {
                        wasEnter = true;
                        continue;
                    }
                    text = textline.replaceAll("^\\s+", "").split(" ", 2);
                } else {
                    System.exit(0);
                }
                String word = text[0];
                String argument;
                try {
                    argument = text[1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    argument = null;
                }
                boolean normalCommand = true;
                switch (word) {
                    case ("save"):
                        System.out.println("YOU DONE");
                        break;
                    default:
                        System.out.println("Invalid command.");
                        normalCommand = false;
                        break;
                }
                Thread.sleep(4000); } catch (Exception e){
                System.out.println("Unexcpected exception");
            }
        }
    }


    public void saveServ() throws  IOException {
        boolean x = false;
        boolean wasEnter = false;
        try {
            while(!x){
                Scanner scanner = new Scanner(System.in);

                String[] text = null;
                if (!wasEnter) System.out.println("Enter command");
                wasEnter = false;
                if (scanner.hasNext()) {
                    String textline = scanner.nextLine();
                    if (textline.trim().isEmpty()) {
                        wasEnter = true;
                        continue;
                    }
                    text = textline.replaceAll("^\\s+", "").split(" ", 2);
                } else {
                    System.exit(0);
                }
                String word = text[0];
                String argument;
                try {
                    argument = text[1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    argument = null;
                }
                boolean normalCommand = true;
                switch (word) {
                    case ("save"):
                        System.out.println("YOU DONE");


                        //              if (scanner.hasNext()) {
                        //                textline = scanner.nextLine();
                        //          } else System.out.println("JOKE");;
                        //        if (textline.equals("save")) {
                        collectionParser = new CollectionParser();//, outputStream);
                        try {
                            TicketCollection = collectionParser.readCollection();
                        } catch (Exception e) {
                            TicketCollection = new Vector<>();
                            TicketCollection.add(new Ticket(true));
                            System.out.println("Создали недо-коллекцию");
                        }
                        Logger logger = Logger.getLogger("serverPartCommand");
                        logger.info("saving to disk");
                        File Input = null;
                        Scanner scan = null;
                        try {
                            Input = new File(System.getenv("Input"));      // проверка на наличие переменной окружения
                            scan = new Scanner(Input);
                        } catch (NullPointerException e) {
                            System.out.println("Cant find env variable");
                            System.exit(0);
                        } catch (FileNotFoundException e) {   // неправильный путь к файлу или нет доступа на чтение
                            System.out.println("File not found");
                            System.exit(0);
                        }
                        try {
                            FileWriter writter = new FileWriter(Input);
                            writter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + "<Ticket>\n");
                            for (Ticket ticket : TicketCollection) {
                                String id = ticket.getId().toString();
                                String name = ticket.getName();
                                String coordinates = ticket.getCoords().getX() + " " + ticket.getCoords().getY();
                                String idevent = ticket.event.getIdTicket().toString();
                                String eventname = ticket.event.getNameTicket();
                                String eventage = ticket.event.getMinAge().toString();
                                String eventcount = ticket.event.getTicketsCount().toString();
                                String ticketdate = ticket.getCreationDate().toString();
                                String typeField = ticket.type.toString();
                                String price = Double.toString(ticket.getPrice());
                                writter.write("<ticket " + "id='" + id + "'" + " name='" + name + "' " + "coordinates='" + coordinates + "' " + "eventid='" + idevent + "' "
                                        + "eventname='" + eventname + "' " + " eventage='" + eventage + "' " + " eventcount='" + eventcount + "' "
                                        + " creation_date='" + ticketdate + "' " + " type='" + typeField + "' " + " price='" + price + "' " + " />\n");
                            }
                            writter.write("</Ticket>");
                            writter.close();
                            System.out.println("The command was executed");
                            logger.info("Collection was saved to disk");
                        } catch (NullPointerException e) {
                            logger.info("Collection was not saved to disk");
                        }
                        break;
                    default:
                        System.out.println("Invalid command.");
                        normalCommand = false;
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Unexpected Exception ");
        }
    }
}
