import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.simpleframework.http.Response;
import org.simpleframework.http.Request;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.io.*;

import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;

import visualizer.GEdge;

public class WebInterface implements Container {
    MapSearcher mapSearcher;
    
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
              System.out.println("USAGE: java WebInterface <file>.co");
               return;
           }


        Tree tree;
        try {
           tree = new Tree(args[0]);
        }
        catch (IOException e) {
           System.out.println ("Couldn't load file.");
           return;
        }

    	MapSearcher mapSearcher = new MapSearcher(tree);
        WebInterface webInterface = new WebInterface(mapSearcher);
	}
    
    public WebInterface(MapSearcher mapSearcher_) {
        mapSearcher = mapSearcher_;
        
        Container container = this;
        Connection connection;
        SocketAddress address = new InetSocketAddress(8080);

        try {
            connection = new SocketConnection(container);
            connection.connect(address);            
        }
        catch (IOException e) {
            System.out.println ("Could not listen on port 8080!");
        }

    }

    public void handle(Request request, Response response) {
        PrintStream body;
        try {
            body = response.getPrintStream();
         }
         catch(Exception e) {    
             return;
         }

        long time = System.currentTimeMillis();

        response.set("Server", "CS40 Maps");
        response.setDate("Date", time);
        response.setDate("Last-Modified", time);


        try {
            int maxy = Integer.parseInt(request.getForm().get("maxy"));
            int miny = Integer.parseInt(request.getForm().get("miny"));
            int maxx = Integer.parseInt(request.getForm().get("maxx"));
            int minx = Integer.parseInt(request.getForm().get("minx"));
            
            LinkedList<GEdge> edges = mapSearcher.getEdges(maxy,miny,maxx,minx);
            
            response.set("Content-Type", "application/json");
            
            body.println("[");
            
            for (GEdge gEdge : edges) {
                body.print("[");
                body.print(gEdge.vlon);
                body.print(",");
                body.print(gEdge.vlat);
                body.print(",");
                body.print(gEdge.wlon);
                body.print(",");
                body.print(gEdge.wlat);
                body.println("],");
            }
            
            body.println("]");
        }
        catch (Exception e) {
            System.out.println ("Badly formed request, sending HTML.");
            
            response.set("Content-Type", "text/html");
            
            FileInputStream in;
            
            try {
                in = new FileInputStream("HTMLVisualizer.html");
            }
            catch (IOException ioe) {
                body.close();
                return;
            }
            
            Scanner scanner = new Scanner(in);
            
            try {
                while (scanner.hasNextLine())
                    body.println(scanner.nextLine());                
            }
            finally {
                scanner.close();
            }   
        }

        body.close();
    }
}