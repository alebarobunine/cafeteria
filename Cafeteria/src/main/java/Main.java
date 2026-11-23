import java.util.*;

/**
 * Clase principal del programa.
 *
 */
public class Main {

    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);


        // El camarero será el mismo durante esta ejecución del programa.
        Camarero camarero = new Camarero("Carlos", "C01");

        ArrayList<Producto> catalogo = new ArrayList<>();

        catalogo.add(new Bebida("Café", 1.80, "Bebida", 10, "mediano"));
        catalogo.add(new Comida("Bocadillo", 3.50, "Comida", 10, true));
        catalogo.add(new Bebida("Zumo", 2.20, "Bebida", 10, "grande"));
        catalogo.add(new Producto("Galleta", 1.00, "Dulce", 10));
        catalogo.add(new Comida("Croissant", 1.70, "Comida", 10, false));


        System.out.println(catalogo.get(1).mostrarInformacion());

        // Esta clase se encarga del archivo donde guardamos el historial.
        GestorTickets gestorTickets = new GestorTickets();

        System.out.println("=== NUEVO TICKET ===");
        Cliente cliente = crearCliente(teclado);
        Ticket ticketActual = new Ticket(cliente, camarero);



        int opcion;

        do {
            mostrarMenu(ticketActual);
            opcion = leerEntero(teclado, "Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    mostrarCatalogo(catalogo);
                    break;
                case 2:
                    agregarProductosCatalogo(teclado, catalogo);
                    break;
                case 3:
                    agregarProductoAlTicket(teclado, catalogo, ticketActual);
                    break;
                case 4:
                    eliminarProductoDelTicket(teclado, ticketActual);
                    break;
                case 5:
                    ticketActual.mostrarTicket();
                    break;

                case 6:
                    aplicarDescuento(teclado, ticketActual);
                    break;

                case 7:
                    System.out.printf("Total actual del ticket: %.2f €%n",
                            ticketActual.calcularTotal());
                    break;

                case 8:
                    /*
                     * Finalizamos el ticket actual y lo guardamos en disco.
                     * Después se crea un nuevo cliente y un nuevo Ticket.
                     */
                    if (gestorTickets.guardarTicket(ticketActual)) {
                        System.out.println("Ticket nº "
                                + ticketActual.getNumeroTicket()
                                + " guardado correctamente.");
                        gestorTickets.exportarJson(ticketActual);
                        gestorTickets.agregarTicketPendiente(ticketActual);

                        System.out.println("\n=== NUEVO TICKET ===");
                        cliente = crearCliente(teclado);
                        ticketActual = new Ticket(cliente, camarero);

                        System.out.println("Nuevo ticket nº "
                                + ticketActual.getNumeroTicket()
                                + " creado.");

                    } else {
                        System.out.println("No se puede guardar un ticket vacío.");
                    }
                    break;

                case 9:
                    gestorTickets.mostrarTicketsGuardados();
                    break;
                case 10:
                    mostrarCatalogoPorCategorias(catalogo);
                    break;
                case 11:
                    gestorTickets.mostrarTicketsPendientes();
                    break;
                case 12:


                    try {

                        Ticket atendido =
                                gestorTickets.atenderSiguienteTicket();

                        System.out.println("Atendiendo ticket nº "
                                + atendido.getNumeroTicket());

                        atendido.mostrarTicket();

                        System.out.println("Tickets pendientes: "
                                + gestorTickets.getCantidadPendientes());

                    } catch (NoSuchElementException e) {

                        System.out.println("No hay tickets pendientes.");
                    }

                case 0:
                    if (ticketActual.getContadorProductos() > 0) {
                        System.out.println("ATENCIÓN: el ticket actual tiene productos "
                                + "pero no se ha finalizado ni guardado.");
                    }
                    System.out.println("Gracias por utilizar Cafetería Java.");
                    break;

                default:
                    System.out.println("Opción incorrecta. Inténtelo de nuevo.");
            }

            System.out.println();

        } while (opcion != 0);

        teclado.close();
    }

    /**
     * Muestra las opciones principales e indica qué ticket está abierto.
     */
    private static void mostrarMenu(Ticket ticketActual) {
        System.out.println("================================");
        System.out.println("         CAFETERÍA JAVA");
        System.out.println("       Ticket actual: nº "
                + ticketActual.getNumeroTicket());
        System.out.println("================================");
        System.out.println("1. Ver productos disponibles");
        System.out.println("2. Agregar productos al catalogo");
        System.out.println("3. Añadir producto al ticket actual");
        System.out.println("4. Eliminar producto en el ticket actual");
        System.out.println("5. Mostrar ticket actual");
        System.out.println("6. Aplicar descuento a un producto");
        System.out.println("7. Consultar total actual");
        System.out.println("8. Finalizar y guardar ticket");
        System.out.println("9. Ver tickets guardados");
        System.out.println("10. Mostrar catálogo por categorías");
        System.out.println("11. Mostrar tickets por atender");
        System.out.println("12. Atender siguiente ticket");
        System.out.println("0. Salir");
        System.out.println("================================");
    }

    /**
     * Crea un cliente usando los datos introducidos por teclado.
     */
    private static Cliente crearCliente(Scanner teclado) {
        String nombre;
        String telefono;

        do {
            System.out.print("Nombre del cliente: ");
            nombre = teclado.nextLine().trim();

            if (nombre.isEmpty()) {
                System.out.println("El nombre no puede estar vacío.");
            }
        } while (nombre.isEmpty());

        System.out.print("Teléfono del cliente: ");
        telefono = teclado.nextLine().trim();

        return new Cliente(nombre, telefono);
    }

    /**
     * Recorre el array del catálogo y muestra todos los productos.
     */
    private static void mostrarCatalogo(ArrayList<Producto> catalogo) {
        System.out.println("\n--- PRODUCTOS DISPONIBLES ---");

        for (int i = 0; i < catalogo.size(); i++) {
            System.out.println((i + 1) + ". " + catalogo.get(i).mostrarInformacion());
        }
    }
    private static void mostrarCatalogoPorCategorias(ArrayList<Producto> catalogo) {
        HashMap<String, ArrayList<Producto>> categorias = new HashMap<>();
        categorias.put("Bebida", new ArrayList<>());
        categorias.put("Comida", new ArrayList<>());
        categorias.put("Dulce", new ArrayList<>());
        categorias.put("Otros", new ArrayList<>());

        // Agrupar productos
        for (Producto producto : catalogo) {

            String categoria = producto.getCategoria().toLowerCase();

            if (categoria.equals("bebida")) {
                categorias.get("Bebida").add(producto);
            } else if (categoria.equals("comida")) {
                categorias.get("Comida").add(producto);
            } else if (categoria.equals("dulce")) {
                categorias.get("Dulce").add(producto);
            } else {
                categorias.get("Otros").add(producto);

            }
        }

        // Mostrar categorías y productos
        for (String categoria : categorias.keySet()) {

            System.out.println("\n=== " + categoria.toUpperCase() + " ===");

            for (Producto producto : categorias.get(categoria)) {
                System.out.println(producto.mostrarInformacion());
            }
        }


    }
    private static void agregarProductosCatalogo(Scanner teclado, ArrayList<Producto> catalogo){
        String nombre;
        String categoria;
        double precio;
        String tamanio;
        String temperatura;

        do {
            System.out.print("Nombre del producto: ");
            nombre = teclado.nextLine().trim();

            if (nombre.isEmpty()) {
                System.out.println("El nombre no puede estar vacío.");
            }
        } while (nombre.isEmpty());
        while (true) {
            try {
                System.out.print("Precio del producto: ");
                precio = teclado.nextDouble();

                if (precio < 0) {
                    throw new IllegalArgumentException(
                            "El precio no puede ser negativo");
                }

                teclado.nextLine(); // limpiar buffer
                break;

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        do {

            System.out.print("Categoria del producto: ");
            categoria = teclado.nextLine().trim();


            if (categoria.isEmpty()) {
                System.out.println("La categoria no puede estar vacia.");
            }
            else if (categoria.equalsIgnoreCase("Bebida")) {
                System.out.print("Tamaño de la bebida: ");
                tamanio = teclado.nextLine().trim();
                catalogo.add(new Bebida (nombre, precio, categoria, 10, tamanio));
            }
            else if (categoria.equalsIgnoreCase("Comida")) {
                while (true) {
                    try {
                        System.out.print("¿La comida es caliente? (y/n): ");
                        temperatura = teclado.nextLine().trim();

                        if (!temperatura.equalsIgnoreCase("y") &&
                                !temperatura.equalsIgnoreCase("n")) {
                            throw new Exception("Opción no válida");
                        }

                        break;

                    } catch (Exception e) {
                        System.out.println("Error: debes introducir y o n.");
                    }
                }

                if (temperatura.equalsIgnoreCase("y")) {
                    catalogo.add(new Comida (nombre, precio, categoria, 10, true));
                }else {
                    catalogo.add(new Comida (nombre, precio, categoria, 10, false));
                }
            }
            else{
                catalogo.add(new Producto (nombre, precio, categoria, 10));
            }
        } while (categoria.isEmpty());
        System.out.println("Producto añadido correctamente");

    }

    /**
     * Permite elegir un producto del catálogo y añadirlo al ticket actual.
     */
    private static void agregarProductoAlTicket(Scanner teclado,
                                                ArrayList<Producto> catalogo,
                                                 Ticket ticket) {
        mostrarCatalogo(catalogo);

        int numeroProducto = leerEntero(teclado,
                "Seleccione el número del producto que desea añadir: ");

        if (numeroProducto < 1 || numeroProducto > catalogo.size()) {
            System.out.println("El producto seleccionado no existe.");
            return;
        }

        Producto productoElegido = catalogo.get(numeroProducto - 1).copiar();

        int cantidad = leerEntero(teclado, "Introduce cantidad: ");

        boolean agregado = ticket.agregarProducto(productoElegido, cantidad);

        if (agregado) {
            System.out.println("Producto añadido correctamente: "
                    + productoElegido.getNombre());
        }
    }


    private static void eliminarProductoDelTicket(
            Scanner teclado,
            Ticket ticket) {

        ArrayList<Producto> productos = ticket.getProductos();

        if (productos.isEmpty()) {
            System.out.println("No hay productos en el ticket.");
            return;
        }

        System.out.println("\n--- PRODUCTOS DEL TICKET ---");

        for (int i = 0; i < productos.size(); i++) {
            System.out.println((i + 1) + ". "
                    + productos.get(i).mostrarInformacion());
        }

        int numeroProducto = leerEntero(
                teclado,
                "Seleccione el producto que desea eliminar: ");

        if (numeroProducto < 1 || numeroProducto > productos.size()) {
            System.out.println("El producto seleccionado no existe.");
            return;
        }

        Producto producto = productos.get(numeroProducto - 1);

        ticket.eliminarProducto(producto);

        System.out.println("Producto eliminado correctamente: "
                + producto.getNombre());
    }

    /**
     * Aplica un descuento a un producto para demostrar la interfaz
     * Descontable. Igual que en la versión anterior, el descuento se calcula
     * y se muestra, pero no modifica el precio original del catálogo.
     */
    private static void aplicarDescuento(Scanner teclado, Ticket ticket) {

        if (ticket.getContadorProductos() == 0) {
            System.out.println("No hay productos en el ticket.");
            return;
        }

        System.out.println("\n--- PRODUCTOS DEL TICKET ---");

        ArrayList<Producto> productos = ticket.getProductos();

        for (int i = 0; i < ticket.getContadorProductos(); i++) {
            System.out.println((i + 1) + ". " + productos.get(i).mostrarInformacion());
        }

        int numeroProducto = leerEntero(teclado,
                "Seleccione el producto al que desea aplicar el descuento: ");

        if (numeroProducto < 1 || numeroProducto > ticket.getContadorProductos()) {
            System.out.println("El producto seleccionado no existe en el ticket.");
            return;
        }

        double porcentaje = leerDouble(teclado,
                "Introduzca el porcentaje de descuento (0-100): ");

        if (porcentaje < 0 || porcentaje > 100) {
            System.out.println("El porcentaje debe estar entre 0 y 100.");
            return;
        }

        Producto producto = productos.get(numeroProducto - 1);
        Descontable descontable = producto;
        double precioConDescuento = descontable.aplicarDescuento(porcentaje);
        producto.setPrecio(precioConDescuento);

        System.out.println("\n--- DESCUENTO ---");
        System.out.println("Producto: " + producto.getNombre());
        System.out.printf("Precio original: %.2f €%n", producto.getPrecio());
        System.out.printf("Descuento aplicado: %.2f%%%n", porcentaje);
        System.out.printf("Precio con descuento: %.2f €%n", precioConDescuento);
    }

    private static int leerEntero(Scanner teclado, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = teclado.nextLine();

            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Debe introducir un número entero.");
            }
        }
    }

    private static double leerDouble(Scanner teclado, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = teclado.nextLine().replace(',', '.');

            try {
                return Double.parseDouble(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Debe introducir un número válido.");
            }
        }
    }
}
