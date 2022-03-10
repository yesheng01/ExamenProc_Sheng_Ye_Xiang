import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * PACKAGE_NAME
 * Nombre_project: ExamenProc_Sheng_Ye_Xiang
 * Client
 * Created by: sheng
 * Date : 26/02/2022
 **/
public class Client {
    static Socket socket = null;
    static DataInputStream dataInputStream = null;
    static DataOutputStream OutputStream = null;

    static DataOutputStream dataOutputStream = null;
    static int opcion = 0;




    //Hilo para mostrar el menu
    public static class HiloMenuCliente extends Thread{
        private final DataInputStream dataInputStream;
        private final DataOutputStream dataOutputStream;
        private final DataOutputStream OutputStream;

        Scanner teclado = new Scanner(System.in);



        public HiloMenuCliente(DataInputStream dataInputStream, DataOutputStream dataOutputStream, DataOutputStream outputStream) {
            this.dataInputStream = dataInputStream;
            this.dataOutputStream = dataOutputStream;
            OutputStream = outputStream;
        }

        public void start() {
            String mensaje;
            while (true) {
                try {
                    System.out.println("***************************************************");
                    System.out.println("1- Almacenar un par clave-valor");
                    System.out.println("2- Eliminar un par clave-valor");
                    System.out.println("3- Consultar el valor asociado a una clave");
                    System.out.println("4- Modificar la clave y el valor");
                    System.out.println("5- Salir (Escibre el numero 5 para salir)");
                    System.out.println("Accion : ");
                    opcion = teclado.nextInt();
                    dataOutputStream.writeInt(opcion);
                    switch (opcion) {
                        case 1:
                            System.out.print("<nombre_de_clave>: ");
                            String nombre_de_clave = teclado.next();
                            System.out.print("<valor>: ");
                            String valor = teclado.next();
                            dataOutputStream.writeUTF(nombre_de_clave);
                            OutputStream.writeUTF(valor);
                            mensaje = dataInputStream.readUTF();
                            System.out.println(mensaje);
                            break;
                        case 2:
                            Scanner s = new Scanner(System.in);
                            System.out.print("<nombre_de_clave>: ");
                            String nombre_de_clave1 = s.next();
                            System.out.print("<valor>: ");
                            String valor1 = s.next();
                            dataOutputStream.writeUTF(nombre_de_clave1);
                            OutputStream.writeUTF(valor1);
                            mensaje = dataInputStream.readUTF();
                            System.out.println(mensaje);
                            break;
                        case 3:
                            Scanner s3 = new Scanner(System.in);
                            System.out.print("<nombre_de_clave>: ");
                            String nombre_de_clave2 = s3.next();
                            dataOutputStream.writeUTF(nombre_de_clave2);
                            mensaje = dataInputStream.readUTF();
                            System.out.println(mensaje);
                            break;

                        case 4:
                            Scanner s1 = new Scanner(System.in);
                            System.out.print("<nombre_de_clave>: ");
                            String nombre_de_clave3 = s1.next();
                            System.out.print("<valor>:  ");
                            String valor2 = s1.next();
                            dataOutputStream.writeUTF(nombre_de_clave3);
                            OutputStream.writeUTF(valor2);
                            mensaje = dataInputStream.readUTF();
                            System.out.println(mensaje);
                            break;

                        case 5:
                            System.out.println("Cerrando cliente del servidor");
                            while (opcion == 5) {
                                socket.close();
                                dataInputStream.close();
                                dataOutputStream.close();
                                System.exit(0);
                            }
                            break;
                    }
                }catch (IOException e) {
                    try {
                        System.out.println("cerrado");
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }
            }
        }
    }
    public void start() {
        while (true){
            try {
                //Establecemos el socket del cliente
                socket = new Socket("localhost", 44001);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                OutputStream = new DataOutputStream(socket.getOutputStream());


                new HiloMenuCliente(dataInputStream, dataOutputStream, OutputStream).start();

            } catch (IOException e) {
                System.out.println("cerrando");
            }
        }

    }


    public static void main(String[] args) {
        System.out.println("\nEl protocolo a seguir entre cliente y " +
                "servidor por lo que para hacer lo que se pide , indicando el numero (1 a 5) en ello te pedira " +
                "añadir los valores como se deben\n");
        Client client = new Client();
        client.start();
    }
}
