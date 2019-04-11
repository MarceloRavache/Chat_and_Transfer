import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ChatTCPServer {
    public static void main (String args[]) {
        try{
            int serverPort = 7896; // the server port

                ServerSocket listenSocket = new ServerSocket(serverPort);
            while (true) {
                Socket serverSocket = listenSocket.accept();
                Connection c = new Connection(serverSocket);
            }
        } catch(IOException e) {System.out.println("Listen socket:"+e.getMessage());}
    }
}
class Connection extends Thread {

    public Connection (Socket socket) {
        try {

            DataInputStream in = new DataInputStream(socket.getInputStream());
            String escolha = in.readUTF();
            System.out.println(escolha);
            socket.close();

            if (escolha.equals("chat")) {
                int serverPort = 7910; // the server port

                ServerSocket listenSocket = new ServerSocket(serverPort);
                Socket serverSocket = listenSocket.accept();

                MyRead entrada = new MyRead(serverSocket);
                MyWrite saida = new MyWrite(serverSocket);

                Thread thread1 = new Thread(entrada);
                Thread thread2 = new Thread(saida);

                thread1.start();
                thread2.start();

                thread1.join();
                thread2.join();
            } else if (escolha.equals("transferir")) {
                int serverPort = 7900; // the server port

                ServerSocket listenSocket = new ServerSocket(serverPort);
                Socket serverSocket = listenSocket.accept();

                Receber arquivo = new Receber(serverSocket, "teste.txt");
                Thread thread = new Thread(arquivo);
                thread.start();
                thread.join();
            } else {
                System.out.println("Argumento não existe");
            }

        }catch(IOException e) {System.out.println("Connection:"+e.getMessage());}
        catch(InterruptedException e) {System.out.println("Connection:"+e.getMessage());}
        finally {if(socket!=null) try {socket.close();}catch (IOException e){System.out.println("close:"+e.getMessage());}}
    }
}

class MyRead implements  Runnable {
    DataInputStream in ;
    Socket serverSocket;
    public MyRead(Socket aServerSocket){
        try{
            this.serverSocket = aServerSocket;
            this.in = new DataInputStream(serverSocket.getInputStream());

        }catch (IOException e){System.out.println("Connection: "+e.getMessage());}
    }

    public void run(){
        String data;
        try {			                 // an echo server
            //While adicionado depois
            while(true){
                data = in.readUTF();
                System.out.println(data);
            }

        }catch (EOFException e){
            System.out.println("EOF:"+e.getMessage());
        } catch(IOException e) {System.out.println("readline:"+e.getMessage());
        } finally{ try {serverSocket.close();}catch (IOException e){/*close failed*/}}

    }
}

class MyWrite implements  Runnable {
    DataOutputStream out;
    Socket serverSocket;
    public MyWrite(Socket aServerSocket){
        try{
            this.serverSocket = aServerSocket;
            this.out = new DataOutputStream(serverSocket.getOutputStream());

        }catch (IOException e){System.out.println("Connection: "+e.getMessage());}
    }

    public void run(){
        Scanner scn = new Scanner(System.in);
        String msg;
        try {			                 // an echo server
            //While adicionado depois
            while(true) {
                msg = scn.nextLine();

                out.writeUTF(msg);
            }

        }catch (EOFException e){System.out.println("EOF:"+e.getMessage());
        } catch(IOException e) {System.out.println("readline:"+e.getMessage());
        } finally{ try {serverSocket.close();}catch (IOException e){/*close failed*/}}

    }
}

class Receber implements Runnable{
    Socket serverSocket;
    String nomeArquivo;
    InputStream in;
    public Receber(Socket socket, String nomeArquivo){
        try{
            this.serverSocket = socket;
            this.nomeArquivo = nomeArquivo;
            this.in = socket.getInputStream();
        }catch (IOException e){System.out.println("Connection: "+e.getMessage());}
    }

    public void run(){

        try {
            InputStreamReader inReceber = new InputStreamReader(in);
            BufferedReader leitor = new BufferedReader(inReceber);

            System.out.println(nomeArquivo);

            File arquivo = new File("/home/overnull/Documentos/" + nomeArquivo);
            FileOutputStream out = new FileOutputStream(arquivo);

            int tamanho = 4096; //4KB
            byte[] buffer = new byte[tamanho];

            int jaLidos = -1;
            while ((jaLidos = in.read(buffer, 0, tamanho)) != -1) {
                System.out.println(jaLidos);
                out.write(buffer, 0, jaLidos);
            }
            out.flush();
            serverSocket.close();

        }catch (EOFException e){System.out.println("EOF:"+e.getMessage());
        } catch(IOException e) {System.out.println("readline:"+e.getMessage());
        } finally{ try {serverSocket.close();}catch (IOException e){/*close failed*/}}

    }
}
