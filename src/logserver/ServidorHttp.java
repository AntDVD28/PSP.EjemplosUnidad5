
package logserver;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Servidor HTTP Concurrente
 * @author David Jiménez Riscardo
 * @version 1.0
 */
public class ServidorHttp {

    /**
     * Método principal
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //Iniciamos el registrador de eventos
        Logger logger = registrarEventos();
        
        //Gestión del puerto
        int puerto = 80;      
        if(args.length!=1 || !isNumeroPositivo(args[0])){
            System.out.println("Parámetros erróneos. Iniciando servidor en el puerto 80.\n");
            logger.log(Level.WARNING, "Parámetros erróneos. Iniciando servidor en el puerto 80.\n");
        }else {
            puerto = Integer.parseInt(args[0]);
        }
        
        System.out.println("SERVIDOR HTTP");
        System.out.println("=============");
             
              
        try {
            //Instanciamos al servidor
            HttpServer server = HttpServer.create(new InetSocketAddress(puerto), 0);
            System.out.println("["+Utilidades.getFechaHoraActualFormateada()+"] Servidor HTTP iniciado en el puerto "+puerto);
            logger.log(Level.INFO, "Servidor HTTP iniciado en el puerto "+puerto+"\n");
            
            //Agregamos los contextos al servidor
            server.createContext("/saludar", (HttpHandler) new HandlerSaludar(logger));
            server.createContext("/primo", (HttpHandler) new HandlerEsPrimo(logger));
            
            //Incorporamos la gestión multihilo
            server.setExecutor(Executors.newCachedThreadPool());
            
            //Iniciar server
            server.start();
        } catch (BindException ex){    
            System.out.println("Puerto no disponible. Revise si el puerto está siendo ya utilizado por otro programa o no tiene permisos.");
            logger.log(Level.WARNING, "Puerto no disponible: "+puerto+"\n");
        } catch (IOException ex) {
            System.out.println("Error de E/S");
            logger.log(Level.SEVERE, "Error de E/S\n");
        }
    }
    
     /**
     * Método para comprobar si una cadena recibida es un número positivo
     * @param cadena Cadena recibida
     * @return Valor booleano, true si la cadena es un número positivo, false en caso contrario
     */
    public static boolean isNumeroPositivo(String cadena) {

        boolean resultado = false;

        try {
            if(Integer.valueOf(cadena)>0)
                resultado = true;
        } catch (NumberFormatException excepcion) {
                resultado = false;
        }
        return resultado;
    }
    
    public static Logger registrarEventos(){
        
        //Creamos o buscamos el logger a utilizar
        Logger logger = Logger.getLogger("MyLog");
        //Establecemos los niveles de seguridad de los eventos a registrar
        //Indicar los niveles del mayor al menor, en caso contrario no recogerá alguno
        logger.setLevel(Level.SEVERE);
        logger.setLevel(Level.WARNING);
        logger.setLevel(Level.INFO);

        //Indicamos que no queremos ver los mensajes por pantalla
        logger.setUseParentHandlers(false);
        
        try {          
            //Vínculamos el logger a un fichero
            //Indicamos el valor "true" para que los registros se añadan
            FileHandler fh = new FileHandler("mylog.txt", true);         
            //Establecemos el formato del archivo
            SimpleFormatter sf = new SimpleFormatter();
            fh.setFormatter(sf);
            //Añadimos el manejador del archivo a nuestro log
            logger.addHandler(fh);
             
        } catch (IOException ex) {
            Logger.getLogger(ServidorHttp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ServidorHttp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return logger;
    }
    
}

