import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Se encarga de guardar y consultar los tickets finalizados.
 *
 * Los tickets se almacenan en un archivo de texto llamado "tickets.txt".
 * De esta forma el historial permanece disponible aunque cerremos el programa
 * y volvamos a ejecutarlo más tarde.
 */
public class GestorTickets {

    private static final String NOMBRE_ARCHIVO = "tickets.txt";
    private Queue<Ticket> ticketsPendientes = new LinkedList<>();
    /**
     * Guarda un ticket al final del archivo.
     *
     * FileWriter con el parámetro true trabaja en modo "añadir", por lo que
     * no borra los tickets guardados anteriormente.
     *
     * @return true si se ha guardado correctamente
     */
    public boolean guardarTicket(Ticket ticket) {
        if (ticket == null || ticket.getContadorProductos() == 0) {
            return false;
        }

        try (BufferedWriter escritor = new BufferedWriter(
                new FileWriter(NOMBRE_ARCHIVO, true))) {

            escritor.write(ticket.generarTextoTicket());
            escritor.write("\n");
            return true;

        } catch (IOException e) {
            System.out.println("Error al guardar el ticket: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lee el archivo línea a línea y muestra todos los tickets almacenados.
     */
    public void mostrarTicketsGuardados() {
        File archivo = new File(NOMBRE_ARCHIVO);

        if (!archivo.exists() || archivo.length() == 0) {
            System.out.println("Todavía no hay tickets guardados.");
            return;
        }

        System.out.println("\n========== HISTORIAL DE TICKETS ==========");

        try (BufferedReader lector = new BufferedReader(
                new FileReader(archivo))) {

            String linea;
            while ((linea = lector.readLine()) != null) {
                System.out.println(linea);
            }

        } catch (IOException e) {
            System.out.println("Error al leer los tickets: " + e.getMessage());
        }
    }
    public void agregarTicketPendiente(Ticket ticket) {
        ticketsPendientes.offer(ticket);
    }
    public void mostrarTicketsPendientes() {

        if (ticketsPendientes.isEmpty()) {
            System.out.println("No hay tickets pendientes.");
            return;
        }

        System.out.println("\n--- TICKETS PENDIENTES ---");

        for (Ticket ticket : ticketsPendientes) {
            System.out.println("Ticket nº "
                    + ticket.getNumeroTicket());
        }

        System.out.println("Total pendientes: "
                + ticketsPendientes.size());
    }
    public Ticket atenderSiguienteTicket() {
        return ticketsPendientes.remove();
    }
    public int getCantidadPendientes() {
        return ticketsPendientes.size();
    }
    public void exportarJson(Ticket ticket) {

        try {

            HashMap<String, Object> datosTicket = new java.util.LinkedHashMap<>();

            datosTicket.put("cliente",
                    ticket.getCliente().getNombre());

            datosTicket.put("camarero",
                    ticket.getCamarero().getNombre());

            ArrayList<HashMap<String, Object>> productos =
                    new ArrayList<>();

            for (Producto producto : ticket.getProductos()) {

                HashMap<String, Object> datosProducto =
                        new java.util.LinkedHashMap<>();

                datosProducto.put("nombre",
                        producto.getNombre());

                datosProducto.put("precio",
                        java.math.BigDecimal.valueOf(producto.getPrecio())
                                .setScale(2, java.math.RoundingMode.HALF_UP));

                productos.add(datosProducto);
            }

            datosTicket.put("productos", productos);

            datosTicket.put("total",
                    java.math.BigDecimal.valueOf(ticket.calcularTotal())
                            .setScale(2, java.math.RoundingMode.HALF_UP));

            ObjectMapper mapper = new ObjectMapper();

            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            mapper.writeValue(
                    new File("ticket.json"),
                    datosTicket);

            System.out.println(
                    "Ticket exportado a ticket.json");

        } catch (IOException e) {

            System.out.println(
                    "Error al exportar JSON: "
                            + e.getMessage());
        }
    }
}
