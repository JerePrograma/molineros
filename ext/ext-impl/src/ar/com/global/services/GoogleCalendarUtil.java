package ar.com.global.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ar.com.ospim.util.DateUtils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;

public class GoogleCalendarUtil {
	/** Application name. */
//	private static final String APPLICATION_NAME = "agendaseguimientoempresa"; //-- sva comento para poner lo de abajo
	private static final String APPLICATION_NAME = "calendargoogleconnection"; 
	public static final String CALENDAR_ID = "ospim.org.ar_f9nika51al0mvi2ou9q2g44fhg@group.calendar.google.com";	// Barbi
																													

	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(
			System.getProperty("catalina.base"), "conf/data_store");
	private static final java.io.File CERT_DIR = new java.io.File(
			System.getProperty("catalina.base"), "conf/");
	
	// private static final java.io.File DATA_STORE_DIR = new
	// java.io.File("/home/fbrachi/data_store");
	
	//private static final java.io.File CERT_DIR = new java.io.File(
	//		System.getProperty("catalina.base"), "/home/daniel/liferay-portal-5.2.3/tomcat-6.0.18/conf");
	

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/**
	 * Global instance of the scopes required by this quickstart.
	 * 
	 * If modifying these scopes, delete your previously saved credentials at
	 * ~/.credentials/calendar-java-quickstart.json
	 */
	private static final List<String> SCOPES = Arrays
			.asList(CalendarScopes.CALENDAR);

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @return an authorized Credential object.
	 * @throws IOException
	 * @throws GeneralSecurityException 
	 */
	public static Credential authorize_ant(String emailsss) throws IOException, GeneralSecurityException {
		// Load client secrets.
		System.out.println("Working Directory = "
				+ System.getProperty("user.dir"));
		File configFile = new File(
				CERT_DIR,
				"client_secret_577318197442-cg0mgjgo4m21823n6a0826i6mvr1e0v5.apps.googleusercontent.com.json");// fbrachi
		
		InputStream in = new FileInputStream(configFile);

		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
				JSON_FACTORY, new InputStreamReader(in));

		GoogleAuthorizationCodeFlow.Builder builder = new GoogleAuthorizationCodeFlow.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES);
		builder.setDataStoreFactory(DATA_STORE_FACTORY);
		builder.setAccessType("offline");
		GoogleAuthorizationCodeFlow flow = builder.build();
        Credential credential= new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("conexioncalendario@agendaseguimientoempresa.iam.gserviceaccount.com");
        
		return credential;
	}
	
	
	public static GoogleCredential authorize(String emailsss) throws IOException, GeneralSecurityException {
		// Load client secrets.
		System.out.println("Working Directory = "
				+ System.getProperty("user.dir"));
/*		
			File configFile = new File(
				CERT_DIR,
				"AgendaSeguimientoEmpresa-1f4554db6e4c.json");
*/			
			File configFile = new File(
					CERT_DIR,
			        "CalendarGoogleConnection-f15a84671b88.json");		
		InputStream in = new FileInputStream(configFile);
  
		
        GoogleCredential readJsonFile = GoogleCredential 
        	      .fromStream(in, HTTP_TRANSPORT, JSON_FACTORY).createScoped(Collections.singleton(CalendarScopes.CALENDAR)); 

        GoogleCredential credential = new GoogleCredential.Builder().setTransport(readJsonFile.getTransport()) 
        	      .setJsonFactory(readJsonFile.getJsonFactory()) 
        	      .setServiceAccountId(readJsonFile.getServiceAccountId()) 
        	      .setServiceAccountScopes(readJsonFile.getServiceAccountScopes()) 
        	      .setServiceAccountPrivateKey(readJsonFile.getServiceAccountPrivateKey())
                  .setServiceAccountScopes(readJsonFile.getServiceAccountScopes())
                  .build(); 
		return credential;
	}	
	
	

	/**
	 * Build and return an authorized Calendar client service.
	 * 
	 * @return an authorized Calendar client service
	 * @throws IOException
	 * @throws GeneralSecurityException 
	 */
	public static com.google.api.services.calendar.Calendar getCalendarService(
			String email) throws IOException, GeneralSecurityException {
		GoogleCredential credential = authorize(email);
		
		return  new com.google.api.services.calendar.Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, null) .setHttpRequestInitializer(credential).setApplicationName(APPLICATION_NAME).build(); 
		
	}

	public static void main(String[] args) throws Exception {
		// Build a new authorized API client service.
		// Note: Do not confuse this class with the
		// com.google.api.services.calendar.model.Calendar class.
//		String[] email = new String[] { ("fbrachi@ospim.org.ar") };
		String[] email = new String[] { "info@ospim.org.ar" , "dsulfaro@uoma.org.ar" };
		
		
			       // Make your Google API call
		
		com.google.api.services.calendar.Calendar service = GoogleCalendarUtil
				.getCalendarService(email[0]);
		// Retrieve an event
		getCalendars(service);
		// List the next 10 events from the primary calendar.
		getEvents(service, 10);

		// service.events().delete(CALENDAR_ID,
		// "ihml1scu4ba3at6ric2cb5la8s").execute();

		// CREATE EVENT!
		Calendar fechaAgenda = DateUtils.getCalendarGMTMenos3();
		fechaAgenda.set(Calendar.HOUR_OF_DAY, 18);
		
		createEvent(service, fechaAgenda.getTime(), "Prueba de sistemas Seg.Emp",
				"Cabildo 2900, Buenos Aires, Argentina",
				"Una prueba de cómo agendar eventos en java", email);
		

	}

	public static void deleteEvents(
			com.google.api.services.calendar.Calendar service, String eventId)
			throws Exception {
		service.events().delete(CALENDAR_ID, eventId).execute();
	}

	public static String updateOrCreateEvent(
			com.google.api.services.calendar.Calendar service, String eventId,
			Date fecha, String summary, String location, String description,
			String[] emails) throws Exception {
		Event event = null;
		if (eventId != null && eventId.trim().length() > 0) {
			event = service.events().get(CALENDAR_ID, eventId).execute();
			
			DateTime startDateTime = new DateTime(fecha);
			
			EventDateTime start = new EventDateTime()
					.setDateTime(startDateTime).setTimeZone(
							"America/Argentina/Cordoba");
			event.setStart(start);

			// DateTime endDateTime = new DateTime("2016-03-04T17:00:00-07:00");
			DateTime endDateTime = new DateTime(fecha);
			EventDateTime end = new EventDateTime().setDateTime(endDateTime)
					.setTimeZone("America/Argentina/Cordoba");
			
			event.setEnd(end);
			event.setSummary(summary);
			event.setLocation(location);
			event.setDescription(description);
			event.setVisibility("private");

			List<EventAttendee> attendees = new ArrayList<EventAttendee>();
			for (String email : emails) {
				attendees.add(new EventAttendee().setEmail(email));
			}
			event.setAttendees(attendees);

			service.events().update(CALENDAR_ID, event.getId(), event).execute();
			return event.getId();
		}else{
			return createEvent(service, fecha, summary, location, description, emails);
		}

	}

	public static String createEvent(
			com.google.api.services.calendar.Calendar service, Date fecha,
			String summary, String location, String description, String[] emails)
			throws Exception {
		Event event = new Event().setSummary(summary).setLocation(location)
				.setDescription(description);

		// DateTime startDateTime = new DateTime("2016-03-04T09:00:00-07:00");
		DateTime startDateTime = new DateTime(fecha);
		EventDateTime start = new EventDateTime().setDateTime(startDateTime)
				.setTimeZone("America/Argentina/Cordoba");
		event.setStart(start);

		// DateTime endDateTime = new DateTime("2016-03-04T17:00:00-07:00");
		DateTime endDateTime = new DateTime(fecha);
		EventDateTime end = new EventDateTime().setDateTime(endDateTime)
				.setTimeZone("America/Argentina/Cordoba");
		;

		event.setEnd(end);

		List<EventAttendee> attendees = new ArrayList<EventAttendee>();
		for (String email : emails) {
			attendees.add(new EventAttendee().setEmail(email));
		}
		event.setAttendees(attendees);

		EventReminder[] reminderOverrides = new EventReminder[] {
				new EventReminder().setMethod("email").setMinutes(60),// (24 *
																		// 60),
				new EventReminder().setMethod("popup").setMinutes(10), };
		Event.Reminders reminders = new Event.Reminders().setUseDefault(false)
				.setOverrides(Arrays.asList(reminderOverrides));
		event.setReminders(reminders);
		event.setVisibility("private");

		String calendarId = CALENDAR_ID;
		event = service.events().insert(calendarId, event).execute();
		System.out.printf("Event created: %s\n", event.getHtmlLink());
		return event.getId();

	}

	public static void getEvents(
			com.google.api.services.calendar.Calendar service, int cantEventos)
			throws Exception {
		DateTime now = new DateTime(System.currentTimeMillis());

		Events events = service.events().list(CALENDAR_ID)
				.setMaxResults(cantEventos).setTimeMin(now)
				.setOrderBy("startTime").setSingleEvents(true).execute();

		List<Event> items = events.getItems();
		if (items.size() == 0) {
			System.out.println("No upcoming events found.");
		} else {
			System.out.println("Upcoming events");
			for (Event event : items) {
				DateTime start = event.getStart().getDateTime();
				if (start == null) {
					start = event.getStart().getDate();
				}
				System.out.printf("%s %s (%s)\n", event.getId(),
						event.getSummary(), start);
			}
		}
	}

	public static void getCalendars(
			com.google.api.services.calendar.Calendar service) throws Exception {
		// Iterate through entries in calendar list
		String pageToken = null;
		do {
			CalendarList calendarList = service.calendarList().list()
					.setPageToken(pageToken).execute();
			List<CalendarListEntry> items = calendarList.getItems();

			for (CalendarListEntry calendarListEntry : items) {
				System.out.println(calendarListEntry.getSummary());
			}
			pageToken = calendarList.getNextPageToken();
		} while (pageToken != null);
	}

}


