import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * PACKAGE_NAME
 * Nombre_project: ExamenProc_Sheng_Ye_Xiang
 * Server
 * Created by: sheng
 * Date : 26/02/2022
 * Descripcion:
 * Se debe desarrollar un servicio que simule una caché distribuida de tipo clave-valor.
 * El servidor, una vez aceptada la petición de registro por parte de un cliente, debe mantener un control
 * sobre qué clientes forman parte del clúster y qué cliente lo han abandonado.
 * Una vez que el cliente se ha registrado en el servicio de caché distribuida las operaciones que se permiten
 * son la siguientes:
 * - Almacenar un par clave-valor
 * - Eliminar un par clave-valor
 * - Consultar el valor asociado a una clave
 * - Modificar el valor asociado a una clave
 * Únicamente los clientes registrados podrán realizar las operaciones anteriores.
 * El alumno debe definir tanto el protocolo a seguir entre cliente y servidor como el tipo de comunicación entre ellos
 * , además de cualquier otro detalle que afecte directamente al funcionamiento propuesto del servicio.
 *
 * Ayudas: Alguna ayuda con videos de youtube
 *
 *
 **/
public class Server {

    //Atributos
    static HashMap<String, Timestamp> cliente = new HashMap<>();
    static HashMap<String, String> messas = new HashMap<>();

    static DataInputStream dataInputStream = null;
    static DataInputStream input = null;

    static DataOutputStream dataOutputStream = null;
    ServerSocket serverSocket;
    static Socket socket;
    Timestamp tiempo;


    //Primer Hilo donde controlamos el menu del servidor
    public static class HiloServidor extends Thread {
        private final DataInputStream dataInputStream;
        private final DataOutputStream dataOutputStream;
        private final DataInputStream input;

        private HashMap<String, String> access = new HashMap<>();


        public HiloServidor(DataInputStream dataInputStream, DataOutputStream datas, DataInputStream input, HashMap access) {
            this.dataInputStream = dataInputStream;
            this.dataOutputStream = datas;
            this.input = input;
            this.access = access;

        }

        public void run() {
            //Mostramos los la informacion de cliente que ha entrado
            for (String clave : cliente.keySet()) {
                String valor = String.valueOf(cliente.get(clave));
                System.out.println(clave + " - " + valor );
            }

            int opcion = 0;
            String mensjae= "";
            String mensjae1= "";

            try {
                while (true) {
                        opcion = dataInputStream.readInt();
                        mensjae = dataInputStream.readUTF();
                    System.out.println("**************************************************");
                        switch (opcion) {
                            case 1:
                                mensjae1 = input.readUTF();
                                //Almacenamos
                                access.put(mensjae, mensjae1);
                                dataOutputStream.writeUTF("Almacenamos los valores: " + mensjae  + " " + mensjae1);
                                System.out.println("Valores añadidos por cliente : " + mensjae + " " + mensjae1);

                                break;
                            case 2:
                                mensjae1 = input.readUTF();

                                //Borramos
                                access.remove(mensjae);
                                dataOutputStream.writeUTF("Eliminamos los valores añadidos: "+ mensjae  + " " + mensjae1);
                                System.out.println("Valores eliminados por el cliente: " + mensjae + " " + mensjae1);

                                break;
                            case 3:
                                //Consultamos
                                dataOutputStream.writeUTF("Consultamos el valor: " + mensjae +" "+ access.get(mensjae));
                                System.out.println("Valores consultadas por el cliente: " + mensjae  + " " + access.get(mensjae));

                                break;
                            case 4:
                                mensjae1 = input.readUTF();

                                //Modificar
                                access.replace(mensjae, mensjae1);
                                dataOutputStream.writeUTF("Modificar los valores: " + mensjae+" "+ mensjae1);
                                System.out.println("Valores modificadas por el cliente: " + mensjae + " " + mensjae1);

                                break;

                            case 5:
                                System.out.println("Salir");
                                dataInputStream.close();
                                dataOutputStream.close();
                                break;
                        }
                    }

            } catch (IOException e) {
                //Si el cliente se desconecta entonces entra en otro hilo y dara un aviso de que se ha desconectado
                System.out.println("Un cliente se ha desconectado: ");
                new HiloCliente(cliente).start();
            }
        }
    }


    //Segundo hilo en donde controlamos si esta el cliente
    public static class HiloCliente extends Thread{


        HashMap<String, Timestamp> clientes1 = new HashMap<String, Timestamp>();

        public HiloCliente(HashMap<String, Timestamp> clientes1) {
            this.clientes1 = clientes1;
        }

        //Metodo donde controlamos el tiempo de conexion del usuario
        public void run() {
            while (true){
                    for (String client : clientes1.keySet()){
                            if (System.currentTimeMillis() - clientes1.get(client).getTime() > 15000){
                                System.out.println("Se elimina el siguiente cliente con el IP :  " + client );
                                clientes1.remove(client);
                            }
                    }
            }

        }
    }

    public void start() throws IOException {
        //Asignamos el puerto del servidor
        serverSocket = new ServerSocket(44001);
        System.out.println("----------Servidor conectado-------------");
        try {

        while (true) {
            //Aceptamos si el cliente se conecta
                socket = serverSocket.accept();
                System.out.println("----Conexion con el cliente----");
                dataInputStream = new DataInputStream(socket.getInputStream());
                input = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                String addressClient = socket.getInetAddress().getHostAddress();

                tiempo = new Timestamp(System.currentTimeMillis());

                //Añadimos el cliente
//                cliente.put(addressClient, tiempo);
                if (!cliente.containsKey(addressClient)) {
                    cliente.put(addressClient, tiempo);
                } else {
                    cliente.replace(addressClient, tiempo);

                }

                //Iniciamos el hilo
                new HiloServidor(dataInputStream, dataOutputStream, input, messas).start();

        }
        } catch (Exception e) {
            System.out.println("close client");
        }

    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.start();
    }
}
