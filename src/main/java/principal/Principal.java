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
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

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
			System.out.println("❌ El archivo credenciales.txt no existe.");
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
						System.out.println("⚠Perfil no reconocido: " + perfil);
						credencialesCorrectas = false;
					}

				}

			}
			// Avisamos si hay problemas en la lectura del archivo
		} catch (IOException e) {
			System.out.println("❌ Error al leer el archivo de credenciales.");
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
						"❌ La fecha de fin debe ser posterior a la de inicio.");
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

			System.out.println("✅ Espectáculo creado: " + nuevoEspectaculo);

		} catch (Exception e) {
			System.out.println(
					"Error al introducir los datos: " + e.getMessage());
		}
	}

	public static boolean existeNombreEspectaculo(String nombreBuscado) {
		if (!ficheroEspectaculos.exists() || ficheroEspectaculos.length() == 0)
			return false;

		try (ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(ficheroEspectaculos))) {
			List<Espectaculo> espectaculos = (List<Espectaculo>) ois
					.readObject();
			for (Espectaculo e : espectaculos) {
				if (e.getNombre().equalsIgnoreCase(nombreBuscado)) {
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println(
					"⚠️ Error leyendo espectáculos: " + e.getMessage());
		}
		return false;
	}

	public static void guardarEspectaculo(Espectaculo nuevo) {
		List<Espectaculo> espectaculos = new ArrayList<>();

		// 1. Leer los espectáculos existentes (si el fichero ya existe)
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

		// 2. Añadir el nuevo espectáculo
		espectaculos.add(nuevo);

		// 3. Guardar la lista completa en el fichero
		try (ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(ficheroEspectaculos))) {
			oos.writeObject(espectaculos);
			System.out.println("Espectáculo guardado correctamente en "
					+ "espectaculos.dat");
		} catch (IOException e) {
			System.out.println(
					"❌ Error al guardar el espectáculo: " + e.getMessage());
		}
	}

	public static void crearPersona() {
		if (sesion.getPerfil() != Perfiles.ADMIN) {
			System.out.println(
					"Solo un ADMINISTRADOR puede crear nuevas personas.");
			return;
		}

		System.out.println("Introduce nombre de usuario:");
		String nombreUsuario = sc.nextLine().trim();

		System.out.println("Introduce password:");
		String password = sc.nextLine().trim();

		System.out.println("Introduce email:");
		String email = sc.nextLine().trim();

		System.out.println("Introduce nombre completo:");
		String nombre = sc.nextLine().trim();

		System.out.println("Introduce nacionalidad:");
		String nacionalidad = sc.nextLine().trim();

		System.out.println(
				"Introduce perfil (ADMINISTRADOR, COORDINACION, ARTISTA):");
		String perfilStr = sc.nextLine().trim().toUpperCase();
		Perfiles perfil = Perfiles.valueOf(perfilStr);

		// Validar duplicados
		while (existeUsuario(nombreUsuario)) {
			System.out.println("Usuario ya existe.");
			nombreUsuario = sc.nextLine().trim();
		}
		
		while (existeEmail(email)) {
			System.out.println("Email ya existe.");
			email = sc.nextLine().trim();
		}

		Long nuevoId = generarNuevoIdPersona();

		// Construir línea
		String nuevaLinea = nuevoId + "|" + nombreUsuario + "|" + password + "|"
				+ email + "|" + nombre + "|" + nacionalidad + "|"
				+ perfilStr.toLowerCase();

		try (FileWriter fw = new FileWriter("credenciales.txt", true);
				BufferedWriter bw = new BufferedWriter(fw)) {
			bw.write(nuevaLinea);
			bw.newLine();
			System.out.println("Persona creada correctamente.");
		} catch (IOException e) {
			System.out.println(
					"Error escribiendo en credenciales.txt: " + e.getMessage());
		}
	}

	public static boolean existeUsuario(String nombreUsuarioBuscado) {
		try (BufferedReader br = new BufferedReader(
				new FileReader(ficheroCredenciales))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] partes = linea.split("\\|");
				if (partes.length == 7) {
					String nombreUsuario = partes[1].trim();
					if (nombreUsuario.equalsIgnoreCase(nombreUsuarioBuscado)) {
						return true;
					}
				}
			}
		} catch (IOException e) {
			System.out.println(
					"Error leyendo credenciales.txt: " + e.getMessage());
		}
		return false;
	}
	
	public static boolean existeEmail(String emailBuscado) {
		try (BufferedReader br = new BufferedReader(
				new FileReader(ficheroCredenciales))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] partes = linea.split("\\|");
				if (partes.length == 7) {
					String email = partes[4].trim();
					if (email.equalsIgnoreCase(emailBuscado)) {
						return true;
					}
				}
			}
		} catch (IOException e) {
			System.out.println(
					"Error leyendo credenciales.txt: " + e.getMessage());
		}
		return false;
		
	}
	

	public static void mostrarEspectaculos() {
		System.out.println("ESPECTÁCULOS:");

		if (!ficheroEspectaculos.exists()
				|| ficheroEspectaculos.length() == 0) {
			System.out.println("No hay espectáculos registrados.");
			return;
		}

		try (ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(ficheroEspectaculos))) {
			// Leemos la lista completa
			List<Espectaculo> espectaculos = (List<Espectaculo>) ois
					.readObject();

			if (espectaculos.isEmpty()) {
				System.out.println("No hay espectáculos registrados.");
			} else {
				for (Espectaculo e : espectaculos) {
					// Método que muestra solo lo que puede ver un invitado
					System.out.println(e.espectaculoParaInvitados());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fallo al cargar el archivo");
		}
	}

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

				break;
			case "2":
				System.out.println("Saliendo de gestión de Personas");
				break;

			default:
				System.out.println("Opción no valida, vuelve a intentarlo");
				break;
			}
		} while (!opcionMenu.contentEquals("2")
				&& sesion.getPerfil().equals(Perfiles.ADMIN));
	}

	public static Long generarNuevoIdEspectaculo() {
		Long idEspectaculo = 0L;

		if (ficheroEspectaculos.exists() && ficheroEspectaculos.length() > 0) {
			try (ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(ficheroEspectaculos))) {
				List<Espectaculo> espectaculos = (List<Espectaculo>) ois
						.readObject();
				for (Espectaculo espectaculo : espectaculos) {
					if (espectaculo.getId() > idEspectaculo) {
						idEspectaculo = espectaculo.getId();
					}
				}
			} catch (Exception e) {
				System.out.println(
						"⚠️ Error leyendo espectaculos.dat: " + e.getMessage());
			}
		}
		return idEspectaculo + 1;
	}

	public static Long generarNuevoIdPersona() {
		Long idPersona = 0L;
		try (BufferedReader br = new BufferedReader(
				new FileReader(ficheroCredenciales))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] partes = linea.split("\\|");
				Long id = Long.parseLong(partes[0].trim());
				if (id > idPersona) {
					idPersona = id;
				}
			}
		} catch (IOException e) {
			System.out.println(
					"Error leyendo credenciales.txt: " + e.getMessage());
		}
		return idPersona + 1;
	}

	public static Coordinacion seleccionarCoordinador() {
		List<Coordinacion> listaCoordinadores = new ArrayList<>();
		Coordinacion coordinadorSeleccionado = null;

		try (BufferedReader br = new BufferedReader(
				new FileReader(ficheroCredenciales))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] partes = linea.split("\\|");
				if (partes.length >= 7
						&& partes[6].trim().equalsIgnoreCase("coordinacion")) {
					Long idPersona = Long.parseLong(partes[0].trim());
					String nombre = partes[4].trim(); // ajusta índice según tu
														// fichero
					Coordinacion c = new Coordinacion();
					c.setId(idPersona);
					c.setNombre(nombre);
					listaCoordinadores.add(c);
				}
			}
		} catch (IOException e) {
			System.out.println(
					"Error leyendo credenciales.txt: " + e.getMessage());
			return null;
		}

		if (listaCoordinadores.isEmpty()) {
			System.out.println("❌ No hay coordinadores disponibles.");
			return null;
		}

		// 2. Asignar idCoord incremental
		for (int i = 0; i < listaCoordinadores.size(); i++) {
			listaCoordinadores.get(i).setIdCoord((long) (i + 1));
		}

		// 3. Mostrar lista
		System.out.println("Selecciona un coordinador de los siguientes:");
		for (Coordinacion c : listaCoordinadores) {
			System.out.println("idCoord: " + c.getIdCoord() + " | idPersona: "
					+ c.getId() + " | Nombre: " + c.getNombre());
		}

		// 4. Pedir selección
		System.out.println("Introduce el idCoord del Coordinador que quieres:");
		Long seleccionarCoord = sc.nextLong();
		sc.nextLine();

		// 5. Buscar y devolver
		for (Coordinacion c : listaCoordinadores) {
			if (c.getIdCoord().equals(seleccionarCoord)) {
				return c; // devolvemos directamente el coordinador encontrado
			}
		}

		// Si llegamos aquí, no se encontró
		System.out.println(
				"❌ El id introducido no corresponde a un coordinador válido.");
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
					"Error leyendo credenciales.txt: " + e.getMessage());
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
					"Error leyendo credenciales.txt: " + e.getMessage());
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
					"⚠️ Error leyendo credenciales.txt: " + e.getMessage());
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
					"Error leyendo credenciales.txt: " + e.getMessage());
		}
		return idArtista + 1;
	}

	public static void logOut() {
		System.out.println("Cerrando sesion");
		sesion.setCredenciales(null);
		sesion.setPerfil(Perfiles.INVITADO);
	}

}
