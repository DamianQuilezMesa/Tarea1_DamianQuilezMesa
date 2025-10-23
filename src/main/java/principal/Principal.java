package principal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import entidades.Coordinacion;
import entidades.Credenciales;
import entidades.Espectaculo;
import entidades.Perfiles;
import entidades.Sesion;

public class Principal {
	// Aquí declaro como estático cosas que quiero acceder desde cualquier parte
	static Scanner sc = new Scanner(System.in);
	static Sesion sesion = new Sesion();
	static DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd/MM/yyyy");
	static File ficheroEspectaculos = new File("espectaculos.dat");
	static File ficheroCredenciales = new File("ficheros/credenciales.txt");
	static File ficheroPaises = new File("src/main/resources/paises.xml");

	// Main con mensaje inicial y final y el metodo que inicia las
	// funcionalidades
	public static void main(String[] args) {

		System.out.println("Bienvenido al Circo!!");
		// Mientras que la sesion tenga algun perfil asociado, se vuelve
		// a ejecutar el selector de menu
		while (sesion.getPerfil() != (null)) {
			selectorMenu(sesion);
		}

		System.out.println("Fin del programa");
	}

	// Método del LogIn, aquí logueamos en cada uno de los 3 perfiles
	// tanto Admin como Artista/Coordinacion
	// El método devuelve la sesión
	public static Sesion logIn(Sesion sesion) {
		String nomusuario;
		String contrasenia;
		boolean credencialesCorrectas = false;

		do {
			// Introducimos el nombre de usuario y le damos opcion a cancelar el
			// login introduciendo
			// la palabra salir en minúscula
			System.out.println(
					"Introduce tu nombre de usuario (o escribe 'salir' para cancelar):");
			nomusuario = sc.nextLine().trim();
			if ("salir".equalsIgnoreCase(nomusuario)) {
				System.out.println("Login cancelado.");
				break;
			}

			// Introducimos la contraseña y le damos opcion a cancelar el login
			// introduciendo
			// la palabra salir en minúscula
			System.out.println(
					"Introduce tu contraseña (o escribe 'salir' para cancelar):");
			contrasenia = sc.nextLine();
			if ("salir".equalsIgnoreCase(contrasenia)) {
				System.out.println("Login cancelado.");
				break;
			}

			// Asignamos el resultado de las comprobaciones a la variable
			boolean esAdmin = verificarLoginAdmin(nomusuario, contrasenia,
					sesion);
			boolean esUsuario = verificarLoginPerfiles(nomusuario, contrasenia,
					sesion);
			// Si alguno de los dos boleanos anteriores es true, credenciales
			// correctas se asigna true también
			credencialesCorrectas = esAdmin || esUsuario;

			// Si las credenciales son incorrectas lanzamos este mensaje
			if (!credencialesCorrectas) {
				System.out.println(
						"Credenciales incorrectas. Vuelve a intentarlo.");
			}
		} while (!credencialesCorrectas);
		return sesion;
	}

	// Selector de menu, le paso como parámetro la sesión(que se inicializa a
	// null en Credenciales y a Invitado como Perfil)
	public static void selectorMenu(Sesion sesion) {
		// En funcion del perfil selecciona su correspondiente menu
		switch (sesion.getPerfil()) {
		case INVITADO:
			menuInvitado(sesion);
			break;
		case ADMIN:
			menuAdmin(sesion);
			break;
		case ARTISTA:
			menuArtista(sesion);
			break;
		case COORDINACION:
			menuCoordinacion(sesion);
			break;
		default: // Controlamos que la sesión sea inválida y avisamos
			System.out.println("Sesion inválida");
			break;
		}
	}

	// Método que devuelve un booleano, si el login como admin es correcto
	// (true)
	// si no lo es (false)
	public static boolean verificarLoginAdmin(String nomusuario,
			String contrasenia, Sesion sesion) {
		boolean credencialesCorrectas = false;

		// Cargamos el application.properties para extraer el usuario
		// y contraseña de admin
		Properties propiedades = new Properties();
		// Podria pedir manualmente la ruta si no carga correctamente
		try {
			propiedades.load(new FileInputStream(
					"src/main/resources/application.properties"));
		} catch (FileNotFoundException e) {
			System.out.println("No se encuentra el fichero");
			credencialesCorrectas = false;
		} catch (IOException e) {
			System.out.println("El formatin ye incorrectu");
			credencialesCorrectas = false;
		}

		/*
		 * Almacenamos en un string el string asociado a usuarioAdmin y a
		 * passwordAdmin que en este caso es admin para ambos
		 */
		String usuarioAdmin = propiedades.getProperty("usuarioAdmin");
		String contraseñaAdmin = propiedades.getProperty("passwordAdmin");

		/*
		 * Comprobamos que las credenciales introducidas por teclado coincidan
		 * con las de admin, si es asi le establecemos el tipo de perfil a ADMIN
		 * y devolvemos true, si no false
		 */
		if (nomusuario.equals(usuarioAdmin)
				&& contrasenia.equals(contraseñaAdmin)) {
			System.out.println("Bienvenido Administrador!");
			sesion.setPerfil(Perfiles.ADMIN);
			credencialesCorrectas = true;
		} else {
			credencialesCorrectas = false;
		}
		return credencialesCorrectas;
	}

	// Hacemos un proceso similar al método anterior pero con los tipos de
	// usuario que debemos contrastar en el fichero credenciales.txt
	// Paso como parámetros la contraseña del login y el usuario además de la
	// sesion
	public static boolean verificarLoginPerfiles(String usuario,
			String contrasena, Sesion sesion) {
		boolean credencialesCorrectas = false;
		Credenciales credenciales = new Credenciales();
		File archivoCredenciales = ficheroCredenciales;
		;

		// Comprobamos que exista el archivo, en su defecto avisamos
		if (!archivoCredenciales.exists()) {
			System.out.println("El archivo credenciales.txt no existe.");
			credencialesCorrectas = false;
		}

		// Leemos el archivo
		try (BufferedReader br = new BufferedReader(
				new FileReader(archivoCredenciales))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] partes = linea.split("\\|");
				if (partes.length < 7)
					continue;
				// Extraemos los campos que nos interesan
				Long id = Long.parseLong(partes[0].trim());
				String usuarioArchivo = partes[1].trim().toLowerCase();
				String contrasenaArchivo = partes[2].trim();
				String perfil = partes[6].trim().toLowerCase();

				// Comprobamos que usuario y contraseña coinciden con los campos
				// del archivo
				if (usuarioArchivo.equals(usuario.toLowerCase().trim())
						&& contrasenaArchivo.equals(contrasena.trim())) {

					// Según el tipo de perfil que sea el usuario/contraseña se
					// lo asignamos
					switch (perfil) {
					case "artista":
						System.out.println("Usuario autenticado como ARTISTA");
						credenciales = new Credenciales(id, usuarioArchivo,
								contrasenaArchivo, Perfiles.ARTISTA);
						sesion.setCredenciales(credenciales);
						sesion.setPerfil(Perfiles.ARTISTA);
						credencialesCorrectas = true;
						break;
					case "coordinacion":
						System.out.println(
								"Usuario autenticado como COORDINACIÓN");
						credenciales = new Credenciales(id, usuarioArchivo,
								contrasenaArchivo, Perfiles.COORDINACION);
						sesion.setCredenciales(credenciales);
						sesion.setPerfil(Perfiles.COORDINACION);
						credencialesCorrectas = true;
						break;
					default:
						// Si el perfil no es correcto, avisamos
						System.out.println("Perfil no reconocido: " + perfil);
						credencialesCorrectas = false;
					}

				}

			}
			// Avisamos si hay problemas en la lectura del archivo
		} catch (IOException e) {
			System.out.println("Error al leer el archivo de credenciales.");
			e.printStackTrace();

		}

		return credencialesCorrectas;
	}

	// Menú al que se accede si la sesion tiene el perfil de Admin correctamente
	// autenticado
	public static void menuAdmin(Sesion sesion) {
		String opcionMenu = "";

		System.out.println("Hola " + Perfiles.ADMIN + ", bienvenido.");

		// Mostramos el menú
		do {
			System.out.println("-----SELECCIONA UNA OPCION-----");
			System.out.println("1 - Gestionar espectáculos");
			System.out.println("2 - Gestionar personas");
			System.out.println("3 - Cerrar sesión");
			// Introducimos la opcion
			opcionMenu = sc.nextLine();

			// Seleccionamos entre las diferentes opciones y ejecutamos el
			// método
			switch (opcionMenu) {
			case "1":
				gestionarEspectaculos();
				break;
			case "2":
				gestionarPersonas();
				break;
			case "3":
				logOut();
				break;

			default:
				System.out.println("Opción no valida, vuelve a intentarlo");
				break;
			}
			// Mientras que el usuario no quiera salir, se repite el menú
		} while (!opcionMenu.equals("3"));

	}

	// Menú al que se accede si la sesion tiene el perfil de Artista
	// correctamente autenticado
	public static void menuArtista(Sesion sesion) {
		String opcionMenu = "";
		// Mostramos el menú
		do {
			System.out.println("Hola " + sesion.credenciales.getNombre()
					+ ", selecciona una opción");
			System.out.println("1-Ver espectáculos");
			System.out.println("3-Cerrar sesión");
			// Selecionamos la opción a realizar
			opcionMenu = sc.nextLine();

			// Seleccionamos la opción a llevar a cabo
			switch (opcionMenu) {
			case "1":
				mostrarEspectaculos();
				break;
			case "2":
				logOut();
				break;
			default:
				System.out.println("Opción no valida, vuelve a intentarlo");
				break;
			}
		} while (!opcionMenu.equals("2"));

	}

	// Menú al que se accede una vez autenticado como Coordinador
	public static void menuCoordinacion(Sesion sesion) {
		String opcionMenu = "";
		// Mostramos el menú
		do {
			System.out.println("Hola " + sesion.credenciales.getNombre()
					+ ", selecciona una opción");
			System.out.println("1-Gestionar espectáculos");
			System.out.println("2-Cerrar sesión");
			// Selecionamos la opción a realizar
			opcionMenu = sc.nextLine();

			// Seleccionamos la opción a llevar a cabo
			switch (opcionMenu) {
			case "1":
				gestionarEspectaculos();
				break;
			case "2":
				logOut();
				break;
			default:
				System.out.println("Opción no valida, vuelve a intentarlo");
				break;
			}
		} while (!opcionMenu.equals("2"));

	}

	// Este es el primer menú que se lanza al ser el correspondiente a la sesión
	// por defecto
	public static void menuInvitado(Sesion sesion) {
		String opcionMenu = "";

		// Mostramos el menú de opciones
		do {
			System.out.println(
					"Hola " + Perfiles.INVITADO + ", selecciona una opción");
			System.out.println("1-Ver espectáculos");
			System.out.println("2-Log in");
			System.out.println("3-Salir del programa");
			// Seleccionamos la opcion
			opcionMenu = sc.nextLine();

			// Elegimos la tarea a realizar
			switch (opcionMenu) {
			case "1":
				mostrarEspectaculos();
				break;
			case "2":
				logIn(sesion);
				break;
			case "3":
				// Solo salimos del menú una vez se elija la opción 3 ya que
				// esta establece la sesión a null
				System.out.println("Saliendo del programa");
				sesion.setPerfil(null);
				break;

			default:
				System.out.println("Opción no valida, vuelve a intentarlo");
				break;
			}
			// Mientras que se sea invitado sigue en el menú, la única
			// casuística en la que se sale es cuando establecemos a null el
			// perfil siendo en todo el programa posible unicamente en este menú
			// al elegir la opcion 3
		} while (sesion.getPerfil() == (Perfiles.INVITADO));

	}

	// Desde este submenú disponible para ADMIN y COORDINACIÓN, gestionamos los
	// espectáculos
	public static void gestionarEspectaculos() {
		String opcionMenu = "";

		// Mostramos el menú
		do {
			System.out.println(
					"Hola " + sesion.getPerfil() + ", selecciona una opción");
			System.out.println("1 - Ver Espectaculos");
			System.out.println("2 - Crear un nuevo Espectaculo");
			System.out.println("3 - Salir de la Gestion de Espectaculos");
			// Seleccionamos la opción
			opcionMenu = sc.nextLine();

			// Seleccioanmos la tarea a realizar
			switch (opcionMenu) {
			case "1":
				mostrarEspectaculos();
				break;
			case "2":
				crearEspectaculo();
				break;
			case "3":
				System.out.println("Saliendo de la gestion de espectaculos");
				break;

			default:
				System.out.println("Opción no valida, vuelve a intentarlo");
				break;
			}
		} while (!opcionMenu.contentEquals("3"));
	}

	// Creamos un espectáculo
	public static void crearEspectaculo() {

		try {
			// Establecemos con este método que el id sea autogenerado
			Long id = generarNuevoIdEspectaculo();
			// Pedimos un nombre avisando que debe ser de máximo 25 caracteres
			System.out.println("Introduce el nombre (máx. 25 caracteres):");
			String nombre = sc.nextLine().trim();
			// Si es mayor a 25 caracteres lo volvemos a pedir hasta que sea
			// valido
			while (nombre.length() > 25) {
				System.out.println(
						"El nombre no puede superar los 25 caracteres.");
				nombre = sc.nextLine().trim();
			}
			while (existeNombreEspectaculo(nombre)) {
				System.out.println("Ya existe un espectáculo con ese nombre.");
				nombre = sc.nextLine().trim();
			}

			System.out.println("Introduce la fecha de inicio (dd/mm/yyyy):");
			LocalDate fechaini = LocalDate.parse(sc.nextLine(), formatter);

			System.out.println(
					"Introduce la fecha de fin (dd/mm/yyyy), recuerda que cada espectaculo tiene 1 año de validez");
			LocalDate fechafin = LocalDate.parse(sc.nextLine(), formatter);

			while (!fechafin.isAfter(fechaini)) {
				System.out.println(
						"La fecha de fin debe ser posterior a la de inicio.");
				nombre = sc.nextLine().trim();
			}
			// Con esto restringimos que el periodo de validez sea de 1 año
			while (fechaini.plusYears(1).isBefore(fechafin)) {
				System.out.println("El periodo no puede superar 1 año.");
				nombre = sc.nextLine().trim();
			}

			Long idCoord = 0L;
			if (sesion.getPerfil() == Perfiles.COORDINACION) {
				// Generamos automáticamente el id del coordinador aunque no lo
				// almacenamos en ninguna parte aún
				idCoord = generarNuevoIdCoordinador();
				// Al ser admin no generamos el id automáticamente, le damos la
				// opción de elegir entre los diferentes coordinadores y luego
				// le asignamos dicho id
			} else if (sesion.getPerfil() == Perfiles.ADMIN) {
				Long candidato = seleccionarCoordinador().getIdCoord();
				if (!esCoordinadorValido(candidato)) {
					System.out.println(
							"El id introducido no corresponde a un coordinador válido.");
					return;
				}
				idCoord = candidato;
			} else {
				System.out
						.println("No tienes permisos para crear espectáculos.");
				return;
			}

			Espectaculo nuevoEspectaculo = new Espectaculo(id, nombre, fechaini,
					fechafin, idCoord);
			guardarEspectaculo(nuevoEspectaculo);

			System.out.println("Espectáculo creado: " + nuevoEspectaculo);

		} catch (Exception e) {
			System.out.println(
					"Error al introducir los datos: " + e.getMessage());
		}
	}

	// Método que comprueba si existe un espectáculo con el nombre indicado
	// Devuelve true si encuentra un espectáculo con ese nombre en el fichero
	// Devuelve false si no lo encuentra, si el fichero no existe o está vacío
	public static boolean existeNombreEspectaculo(String nombreBuscado) {

		// Si el fichero de espectáculos no existe o está vacío devolvemos false
		// directamente
		if (!ficheroEspectaculos.exists() || ficheroEspectaculos.length() == 0)
			return false;

		try (ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(ficheroEspectaculos))) {

			// Leemos la lista de espectáculos almacenada en el fichero binario
			List<Espectaculo> espectaculos = (List<Espectaculo>) ois
					.readObject();

			// Recorremos la lista de espectáculos
			for (Espectaculo e : espectaculos) {
				// Comparamos el nombre de cada espectáculo con el buscado
				// (ignorando mayúsculas/minúsculas)
				if (e.getNombre().equalsIgnoreCase(nombreBuscado)) {
					// Si encontramos coincidencia devolvemos true
					return true;
				}
			}
		} catch (Exception e) {
			// Si ocurre cualquier error en la lectura mostramos el mensaje y
			// devolvemos false
			System.out.println("Error leyendo espectáculos: " + e.getMessage());
		}

		// Si no se encuentra ningún espectáculo con ese nombre devolvemos false
		return false;
	}

	// Método que guarda un nuevo espectáculo en el fichero binario de
	// espectáculos
	// Si el fichero ya contiene espectáculos, primero los lee y los añade a la
	// lista
	// Después incorpora el nuevo espectáculo y sobrescribe el fichero con la
	// lista completa
	public static void guardarEspectaculo(Espectaculo nuevo) {
		List<Espectaculo> espectaculos = new ArrayList<>();

		// Si el fichero existe y no está vacío, cargamos los espectáculos ya
		// guardados
		if (ficheroEspectaculos.exists() && ficheroEspectaculos.length() > 0) {
			try (ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(ficheroEspectaculos))) {
				espectaculos = (List<Espectaculo>) ois.readObject();
			} catch (Exception e) {
				System.out.println(
						"No se pudieron leer los espectáculos existentes: "
								+ e.getMessage());
			}
		}

		// Añadimos el nuevo espectáculo a la lista
		espectaculos.add(nuevo);

		// Guardamos la lista completa (los antiguos + el nuevo) en el fichero
		// binario
		try (ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(ficheroEspectaculos))) {
			oos.writeObject(espectaculos);
			System.out.println(
					"Espectáculo guardado correctamente en espectaculos.dat");
		} catch (IOException e) {
			System.out.println(
					"Error al guardar el espectáculo: " + e.getMessage());
		}
	}

	// Método que carga las nacionalidades desde el fichero XML de países
	// Devuelve un mapa con clave = código del país (ID) y valor = nombre del
	// país
	// Además, muestra por pantalla cada país cargado
	public static Map<String, String> cargarNacionalidades() {
		Map<String, String> paises = new LinkedHashMap<>(); // Usamos
															// LinkedHashMap
															// para mantener el
															// orden de
															// inserción

		try {
			// Preparamos el parser de XML
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(ficheroPaises);
			doc.getDocumentElement().normalize();

			// Recorremos todos los nodos <pais> del XML
			NodeList lista = doc.getElementsByTagName("pais");
			for (int i = 0; i < lista.getLength(); i++) {
				Node nodo = lista.item(i);
				if (nodo.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) nodo;

					// Extraemos el código del país (ID) y lo pasamos a
					// mayúsculas
					String id = e.getElementsByTagName("id").item(0)
							.getTextContent().trim().toUpperCase();

					// Extraemos el nombre del país
					String nombre = e.getElementsByTagName("nombre").item(0)
							.getTextContent().trim();

					// Guardamos el par clave-valor en el mapa
					paises.put(id, nombre);

					// Mostramos el país cargado por pantalla
					System.out.println(id + " - " + nombre);
				}
			}
		} catch (Exception e) {
			System.out.println("Error leyendo XML: " + e.getMessage());
		}

		return paises;
	}

	// Método que muestra por pantalla todas las nacionalidades cargadas desde
	// el XML
	// El usuario debe introducir el código de la nacionalidad deseada
	// Devuelve el nombre del país correspondiente al código introducido
	public static String seleccionarNacionalidad(Map<String, String> paises) {
		System.out.println("Selecciona la nacionalidad (introduce el código):");

		// Recorremos el mapa de países y mostramos código - nombre
		for (Map.Entry<String, String> entry : paises.entrySet()) {
			System.out.println(entry.getKey() + " - " + entry.getValue());
		}

		// Bucle que se repite hasta que el usuario introduzca un código válido
		while (true) {
			String codigo = sc.nextLine().trim().toUpperCase();
			if (paises.containsKey(codigo)) {
				return paises.get(codigo); // devolvemos el nombre del país
			}
			System.out.println("Código no válido. Inténtalo de nuevo:");
		}
	}

	// Método que permite crear una nueva persona en el sistema
	// Solo puede ser ejecutado por un usuario con perfil ADMIN
	// Se piden y validan todos los datos necesarios antes de guardar en
	// credenciales.txt
	public static void crearPersona() {
		String confirmacion = "";
		LocalDate fechaSenior;

		// Comprobamos que la sesión actual tenga perfil ADMIN
		if (sesion.getPerfil() != Perfiles.ADMIN) {
			System.out.println(
					"Solo un ADMINISTRADOR puede crear nuevas personas.");
			return;
		}

		// Pedimos nombre de usuario y validamos formato
		System.out.println("Introduce nombre de usuario:");
		String nombreUsuario = sc.nextLine().trim().toLowerCase();

		// Validamos longitud, espacios y caracteres permitidos
		while (nombreUsuario.length() <= 2 || nombreUsuario.contains(" ")
				|| !nombreUsuario.matches("[A-Za-z0-9_]+")) {
			System.out.println(
					"El nombre de usuario debe contener más de 2 caracteres y no debe tener espacios en blanco, diéresis ni tildes.");
			nombreUsuario = sc.nextLine().trim().toLowerCase();
		}

		// Validamos que no exista ya un usuario con ese nombre
		while (existeUsuario(nombreUsuario)) {
			System.out.println("Usuario ya existe.");
			nombreUsuario = sc.nextLine().trim();
		}

		// Pedimos contraseña
		System.out.println("Introduce password:");
		String contrasena = sc.nextLine().trim();

		// Validamos que la contraseña no esté repetida (según tu lógica)
		while (existeContrasena(contrasena)) {
			System.out.println("Contraseña ya existe, introduce otra:");
			contrasena = sc.nextLine().trim();
		}

		// Pedimos email
		System.out.println("Introduce email:");
		String email = sc.nextLine().trim();

		// Validamos que no exista ya ese email
		while (existeEmail(email)) {
			System.out.println("Email ya existe.");
			email = sc.nextLine().trim();
		}

		// Validamos formato del email
		while (!validarEmail(email)) {
			System.out.println(
					"Formato del email incorrecto, el email debe tener este formato: x@x.xx");
			email = sc.nextLine().trim();
		}

		// Pedimos nombre completo
		System.out.println("Introduce nombre completo:");
		String nombre = sc.nextLine().trim();

		// Pedimos nacionalidad (se selecciona de la lista cargada desde XML)
		System.out.println("Introduce nacionalidad:");
		String nacionalidad = seleccionarNacionalidad(cargarNacionalidades());

		// Pedimos perfil de la persona
		System.out.println("Introduce perfil (COORDINACION, ARTISTA):");
		String perfilTexto = sc.nextLine().trim().toUpperCase();
		Perfiles perfil = Perfiles.valueOf(perfilTexto);

		// Generamos un nuevo ID para la persona
		Long nuevoId = generarNuevoIdPersona();

		// Construimos la línea que se guardará en credenciales.txt
		String nuevaLinea = nuevoId + "|" + nombreUsuario + "|" + contrasena
				+ "|" + email + "|" + nombre + "|" + nacionalidad + "|"
				+ perfilTexto.toLowerCase();

		// Si el perfil es COORDINACION pedimos datos adicionales
		if (perfil.equals(Perfiles.COORDINACION)) {
			boolean esSenior = false;
			String opcion = "";

			// Preguntamos si es senior
			System.out.println("¿Es senior? 1-Si 2-No");
			opcion = sc.nextLine();
			switch (opcion) {
			case "1":
				esSenior = true;
				break;
			case "2":
				esSenior = false;
				break;
			default:
				System.out.println("Opción no válida, vuelve a intentarlo");
				break;
			}

			// Si es senior pedimos la fecha desde cuándo lo es
			if (esSenior) {
				System.out.println(
						"Desde cuándo? Introduce una fecha dd/mm/aaaa");
				fechaSenior = LocalDate.parse(sc.nextLine(), formatter);
			} else {
				fechaSenior = null;
			}

			// Añadimos estos datos a la línea (aunque no se persistan en otro
			// sitio)
			nuevaLinea = nuevaLinea + "|" + esSenior + "|" + fechaSenior;
		}

		// Mostramos los datos recogidos y pedimos confirmación
		System.out.println("Esta es la persona creada: " + nuevaLinea);
		System.out.println("¿Son los datos correctos? (Si/No)");
		confirmacion = sc.nextLine().trim();

		// Si confirma, guardamos en credenciales.txt
		if (confirmacion.equalsIgnoreCase("si")) {
			try (FileWriter fw = new FileWriter(ficheroCredenciales, true);
					BufferedWriter bw = new BufferedWriter(fw)) {
				bw.newLine();
				bw.write(nuevaLinea);
				System.out.println("Persona creada correctamente.");
			} catch (IOException e) {
				System.out.println("Error escribiendo en credenciales.txt: "
						+ e.getMessage());
			}
		} else {
			// Si no confirma, cancelamos la operación
			System.out.println("Operación cancelada. No se guardó la persona.");
			return;
		}
	}

	// Valida que el email tenga un formato x@x.xx
	public static boolean validarEmail(String email) {
		return email
				.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
	}

	// Comprueba si el usuario existe en el ficheroCredenciales
	public static boolean existeUsuario(String nombreUsuarioBuscado) {
		try (BufferedReader br = new BufferedReader(
				new FileReader(ficheroCredenciales))) {
			String linea;
			// Lee las líneas y compara el parámetro (usuario) con la columna
			// correspondiente dentro del fichero
			while ((linea = br.readLine()) != null) {
				String[] partes = linea.split("\\|");
				String nombreUsuario = partes[1].trim();
				if (nombreUsuario.equalsIgnoreCase(nombreUsuarioBuscado)) {
					return true;
				}
			}
		} catch (IOException e) {
			System.out.println(
					"Error leyendo credenciales.txt: " + e.getMessage());
		}
		return false;
	}

	// Comprueba si el usuario existe en el ficheroCredenciales
	public static boolean existeContrasena(String contrasenaBuscada) {
		try (BufferedReader br = new BufferedReader(
				new FileReader(ficheroCredenciales))) {
			String linea;
			// Lee las líneas y compara el parámetro (contraseña) con la columna
			// correspondiente dentro del fichero
			while ((linea = br.readLine()) != null) {
				String[] partes = linea.split("\\|");
				String nombreUsuario = partes[2].trim();
				if (nombreUsuario.equalsIgnoreCase(contrasenaBuscada)) {
					return true;
				}
			}
		} catch (IOException e) {
			System.out.println(
					"Error leyendo credenciales.txt: " + e.getMessage());
		}
		return false;
	}

	// Comprueba si el usuario existe en el ficheroCredenciales
	public static boolean existeEmail(String emailBuscado) {
		try (BufferedReader br = new BufferedReader(
				new FileReader(ficheroCredenciales))) {
			String linea;
			// Lee las líneas y compara el parámetro (email) con la columna
			// correspondiente dentro del fichero
			while ((linea = br.readLine()) != null) {
				String[] partes = linea.split("\\|");
				String email = partes[4].trim();
				if (email.equalsIgnoreCase(emailBuscado)) {
					return true;
				}
			}
		} catch (IOException e) {
			System.out.println(
					"Error leyendo credenciales.txt: " + e.getMessage());
		}
		return false;
	}

	// Método que muestra por pantalla todos los espectáculos registrados
	// Si el fichero no existe o está vacío, avisa de que no hay espectáculos
	// Solo se muestran los datos que puede ver un invitado (no toda la
	// información interna)
	public static void mostrarEspectaculos() {
		System.out.println("ESPECTÁCULOS:");

		// Comprobamos si el fichero existe y tiene contenido
		if (!ficheroEspectaculos.exists()
				|| ficheroEspectaculos.length() == 0) {
			System.out.println("No hay espectáculos registrados.");
			return;
		}

		try (ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(ficheroEspectaculos))) {

			// Leemos la lista completa de espectáculos desde el fichero binario
			List<Espectaculo> espectaculos = (List<Espectaculo>) ois
					.readObject();

			// Si la lista está vacía avisamos, en caso contrario mostramos cada
			// espectáculo
			if (espectaculos.isEmpty()) {
				System.out.println("No hay espectáculos registrados.");
			} else {
				for (Espectaculo e : espectaculos) {
					// Mostramos la información resumida que puede ver un
					// invitado
					System.out.println(e.espectaculoParaInvitados());
				}
			}

		} catch (Exception e) {
			// Si ocurre un error en la lectura mostramos el error y un aviso
			e.printStackTrace();
			System.out.println("❌ Fallo al cargar el archivo de espectáculos.");
		}
	}

	// Menú de gestión de personas, accesible únicamente para ADMIN
	public static void gestionarPersonas() {
		String opcionMenu = "";

		do {
			System.out.println(
					"Hola " + sesion.getPerfil() + ", selecciona una opción");
			System.out.println("1-Introducir una persona");
			System.out.println("2-Salir de la gestión de Personas");
			opcionMenu = sc.nextLine();

			switch (opcionMenu) {
			case "1":
				crearPersona(); // Llama al método que crea una nueva persona
				break;
			case "2":
				System.out.println("Saliendo de gestión de Personas");
				break;
			default:
				System.out.println("Opción no válida, vuelve a intentarlo");
				break;
			}
			// El menú se repite mientras no se elija salir y el perfil siga
			// siendo ADMIN
		} while (!opcionMenu.contentEquals("2")
				&& sesion.getPerfil().equals(Perfiles.ADMIN));
	}

	// Genera un nuevo ID para un espectáculo
	// Busca el ID más alto en espectaculos.dat y devuelve ese valor + 1
	public static Long generarNuevoIdEspectaculo() {
		Long idEspectaculo = 0L;

		// Si el fichero existe y contiene datos, leemos la lista de
		// espectáculos
		if (ficheroEspectaculos.exists() && ficheroEspectaculos.length() > 0) {
			try (ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(ficheroEspectaculos))) {

				List<Espectaculo> espectaculos = (List<Espectaculo>) ois
						.readObject();

				// Recorremos la lista para encontrar el ID más alto
				for (Espectaculo espectaculo : espectaculos) {
					if (espectaculo.getId() > idEspectaculo) {
						idEspectaculo = espectaculo.getId();
					}
				}
			} catch (Exception e) {
				System.out.println(
						"Error leyendo espectaculos.dat: " + e.getMessage());
			}
		}
		return idEspectaculo + 1; // devolvemos el siguiente ID disponible
	}

	// Genera un nuevo ID para una persona
	// Busca el ID más alto en credenciales.txt y devuelve ese valor + 1
	public static Long generarNuevoIdPersona() {
		Long idPersona = 0L;
		try (BufferedReader br = new BufferedReader(
				new FileReader(ficheroCredenciales))) {

			String linea;
			while ((linea = br.readLine()) != null) {
				String[] partes = linea.split("\\|");
				Long id = Long.parseLong(partes[0].trim());

				// Guardamos el mayor ID encontrado
				if (id > idPersona) {
					idPersona = id;
				}
			}
		} catch (IOException e) {
			System.out.println(
					"Error leyendo credenciales.txt: " + e.getMessage());
		}
		return idPersona + 1; // devolvemos el siguiente ID disponible
	}

	public static Coordinacion seleccionarCoordinador() {
		List<Coordinacion> listaCoordinadores = new ArrayList<>();

		// Leo el fichero
		try (BufferedReader br = new BufferedReader(
				new FileReader(ficheroCredenciales))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] partes = linea.split("\\|");
				if (partes.length == 7
						&& partes[6].trim().equalsIgnoreCase("coordinacion")) {
					Long idPersona = Long.parseLong(partes[0].trim());
					String nombre = partes[4].trim();
					Coordinacion c = new Coordinacion();
					c.setId(idPersona);
					c.setNombre(nombre);
					// Añadimos a la lista estos datos para que vean las
					// opciones de coordinadores disponibles
					listaCoordinadores.add(c);
				}
			}
		} catch (IOException e) {
			System.out.println(
					"Error leyendo credenciales.txt: " + e.getMessage());
			return null;
		}
		// Muestro el mensaje de que no hay coordinadores si fuese el caso
		if (listaCoordinadores.isEmpty()) {
			System.out.println("No hay coordinadores disponibles.");
			return null;
		}

		// Genero el idCoord incrementalmente
		for (int i = 0; i < listaCoordinadores.size(); i++) {
			listaCoordinadores.get(i).setIdCoord((long) (i + 1));
		}

		// Mostramos los coordinadores
		System.out.println("Selecciona un coordinador de los siguientes:");
		for (Coordinacion coordinador : listaCoordinadores) {
			System.out.println("idCoord: " + coordinador.getIdCoord()
					+ " | idPersona: " + coordinador.getId() + " | Nombre: "
					+ coordinador.getNombre());
		}

		// Seleccionamos el coordinador en funcion de su id
		System.out.println("Introduce el idCoord del Coordinador que quieres:");
		Long seleccionarCoord = sc.nextLong();
		sc.nextLine();

		for (Coordinacion c : listaCoordinadores) {
			if (c.getIdCoord().equals(seleccionarCoord)) {
				return c; // devolvemos directamente el coordinador encontrado
			}
		}

		// Si llegamos aquí, no se encontró
		System.out.println(
				"El id introducido no corresponde a un coordinador válido.");
		return null;
	}

	public static Long generarNuevoIdCoordinador() {
		Long idCoordinador = 0L;
		try (BufferedReader br = new BufferedReader(
				new FileReader(ficheroCredenciales))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] partes = linea.split("\\|");
				if (partes[6].trim().equalsIgnoreCase("coordinacion")) {
					idCoordinador++;
					System.out.println("IdPersona " + partes[0] + "IdCoord "
							+ idCoordinador);

				}
			}
		} catch (IOException e) {
			System.out.println(
					"Error leyendo ficheroCredenciales " + e.getMessage());
		}
		return idCoordinador + 1L;
	}

	public static Long generarIdCoordinador(Long idPersona) {
		Long idCoordinadorSeleccionado = 0L;
		Long idCoordinador = 0L;
		try (BufferedReader br = new BufferedReader(
				new FileReader(ficheroCredenciales))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] partes = linea.split("\\|");
				if (partes[6].equalsIgnoreCase("coordinacion")) {
					idCoordinador++;
					if (partes[0].trim().contentEquals(idPersona.toString())) {
						idCoordinadorSeleccionado = idCoordinador;
					}
				}
			}
		} catch (IOException e) {
			System.out.println(
					"Error leyendo ficheroCredenciales " + e.getMessage());
		}
		return idCoordinadorSeleccionado;
	}

	public static boolean esCoordinadorValido(Long idCoord) {
		if (!ficheroCredenciales.exists() || ficheroCredenciales.length() == 0)
			return false;

		try (BufferedReader br = new BufferedReader(
				new FileReader(ficheroCredenciales))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] partes = linea.split("\\|");
				if (partes.length >= 7) {
					long idPersona = Long.parseLong(partes[0].trim());
					String perfil = partes[6].trim().toLowerCase();

					if (idPersona == idCoord && perfil.equals("coordinacion")) {
						return true;
					}
				}
			}
		} catch (IOException e) {
			System.out.println(
					"Error leyendo ficheroCredenciales " + e.getMessage());
		}
		return false;
	}

	public static Long generarNuevoIdArtista() {
		Long idArtista = 0L;
		try (BufferedReader br = new BufferedReader(
				new FileReader(ficheroCredenciales))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] partes = linea.split("\\|");
				if (partes[6].trim().equalsIgnoreCase("artista")) {
					idArtista++;
				}
			}
		} catch (IOException e) {
			System.out.println(
					"Error leyendo ficheroCredenciales " + e.getMessage());
		}
		return idArtista + 1;
	}

	public static void logOut() {
		System.out.println("Cerrando sesion");
		sesion.setCredenciales(null);
		sesion.setPerfil(Perfiles.INVITADO);
	}

}
