
// Java implementation for multithreaded chat client
// Save file as Client.java

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientTCP
{

    public static void main (String args[]) {
        // arguments supply message and hostname
        Socket s = null;
        int serverPort = 7896;

        try{

            s = new Socket("localhost", serverPort);
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.writeUTF(args[0]);
            s.close();
            if(args[0].equals("transferir")){
                Socket s1 = new Socket("localhost", 7900);
                Transferir trf = new Transferir(s1,args[2], args[1]);
                Thread t1 = new Thread(trf);
                t1.start();
            }else if(args[0].equals("chat")) {
                Socket s2 = new Socket("localhost", 7910);

                MyWrite w = new MyWrite(s2);
                MyRead r = new MyRead(s2);
                Thread t1 = new Thread(w);
                Thread t2 = new Thread(r);
                t1.start();
                t2.start();

                t1.join();
            }else{
                System.out.println(args[0]);
            }
        }catch (UnknownHostException e){System.out.println("Socket:"+e.getMessage());
        }catch (EOFException e){System.out.println("EOF:"+e.getMessage());
        }catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {if(s!=null) try {s.close();}catch (IOException e){System.out.println("close:"+e.getMessage());}}
    }


}
class MyRead implements  Runnable {
    DataInputStream in ;
    Socket serverSocket;
    public MyRead(Socket aServerSocket){
        try{
            this.serverSocket = aServerSocket;
            this.in = new DataInputStream(serverSocket.getInputStream());
            //this.start();
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

        }catch (EOFException e){System.out.println("EOF:"+e.getMessage());
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

class Transferir implements Runnable{
    Socket serverSocket;
    String nomeArquivo;
    OutputStream out;
    String diretorio;

    public Transferir(Socket socket, String nomeArquivo, String diretorio){
        try{
            this.serverSocket = socket;
            this.nomeArquivo = nomeArquivo;
            this.out = socket.getOutputStream();
            this.diretorio = diretorio;
        }catch (IOException e){System.out.println("Connection: "+e.getMessage());}
    }

    public void run(){

        try {

            File arquivo = new File(diretorio + nomeArquivo);
            FileInputStream in = new FileInputStream(arquivo);

            int tamanho = 4096; //4KB
            byte[] buffer = new byte[tamanho];

            int jaLidos = -1;

            while ((jaLidos = in.read(buffer, 0, tamanho)) != -1) {
                out.write(buffer, 0, jaLidos);
            }

        }catch (EOFException e){System.out.println("EOF:"+e.getMessage());
        } catch(IOException e) {System.out.println("readline:"+e.getMessage());
        } finally{ try {serverSocket.close();}catch (IOException e){/*close failed*/}}

    }
}
