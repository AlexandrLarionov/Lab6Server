
import java.io.*;
        import java.net.*;
        import java.nio.ByteBuffer;
        import java.nio.channels.SocketChannel;
        import java.nio.charset.StandardCharsets;
        import java.time.LocalDateTime;
        import java.util.Scanner;

// считывает команды из консоли и принимает ответы с сервера, выводит их в консоль
public class CommandsReader {
    InetSocketAddress address;
    SocketChannel channel;
    ByteArrayOutputStream byteArrayOutputStream;
    ObjectOutputStream objectOutputStream;
    boolean afterConnecting = false;
    public CommandsReader(InetSocketAddress address) {
        this.address = address;
    }
    void send(byte[] message) throws IOException {
        int r = channel.write(ByteBuffer.wrap(message));
        if (r != message.length) {
            throw new IOException();
        }
    }
    // функция, которая отправляет message и получает ответ
    String getResponse(Message message) {
        while (true) {
            try {
                byteArrayOutputStream.reset();
                objectOutputStream.writeObject(message);
                objectOutputStream.flush();
                send(byteArrayOutputStream.toByteArray());

                ByteBuffer shortBuffer = ByteBuffer.allocate(2);
                int r = channel.read(shortBuffer);
                if (r == -1) {
                    throw new IOException();
                }
                shortBuffer.flip();
                short len = shortBuffer.getShort();
                ByteBuffer buffer = ByteBuffer.allocate(len);
                r = channel.read(buffer);
                if (r == -1) {
                    throw new IOException();
                }
                buffer.flip();
                return StandardCharsets.UTF_8.decode(buffer).toString();
            } catch (IOException e) {
            }
        }
    }
    String tryRead() {
        try {
            ByteBuffer shortBuffer = ByteBuffer.allocate(2);
            int r = channel.read(shortBuffer);
            if (r == -1) {
                throw new IOException();
            }
            shortBuffer.flip();
            short len = shortBuffer.getShort();
            ByteBuffer buffer = ByteBuffer.allocate(len);
            r = channel.read(buffer);
            if (r == -1) {
                throw new IOException();
            }
            buffer.flip();
            return StandardCharsets.UTF_8.decode(buffer).toString();
        } catch (IOException e) { return null;}
    }
    // основная функция взаимодействия
    public boolean read(Scanner scanner, boolean fromScript) throws IOException {
        boolean exitStatus = false;
        Ticket ticket = new Ticket();
        boolean wasEnter = false;
        while (!exitStatus) {
            afterConnecting = false;
            String[] text = null;
            Commander.CommandType type = null;
            if (!fromScript && !wasEnter) System.out.println("Enter command");
            wasEnter = false;
            if (scanner.hasNext()) {
                String textline = scanner.nextLine();
                if (textline.trim().isEmpty()) {wasEnter = true; continue;}
                text = textline.replaceAll("^\\s+", "").split(" ", 2);
            } else {
                objectOutputStream.writeObject(new Message(true));
                objectOutputStream.flush();
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
                    exitStatus = true;
                    type = Commander.CommandType.save;
                    break;
                default:
                    System.out.println("Invalid command. Try 'help' to see the list of commands");
                    normalCommand = false;
                    break;
            }
            try {            // если нормальная команда отправляем на сервер
                if (normalCommand) {
                    Message message = new Message(ticket, type, argument, fromScript);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("I cant send message");
            }
            byteArrayOutputStream.reset();
        }
        return false;
    }
}
