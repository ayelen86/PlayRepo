import static org.fest.assertions.Assertions.assertThat;
//import static play.mvc.Http.Status.CREATED;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;
import models.Persona;

import org.junit.Test;

import play.db.jpa.JPA;
import play.libs.Json;
import play.libs.WS;
import play.mvc.Content;

import com.fasterxml.jackson.databind.JsonNode;


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    @Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertThat(a).isEqualTo(2);
    }

    @Test
    public void renderTemplate() {
        Content html = views.html.index.render("Your new application is ready.");
        assertThat(contentType(html)).isEqualTo("text/html");
        assertThat(contentAsString(html)).contains("Your new application is ready.");
    }
    
    @Test
    public void crearPersonaSinErrores(){
    	running(testServer(3333, fakeApplication()), new Runnable() {
    		public void run() {
    	          Persona persona = new Persona ("Diego", "Diagonal 79");
    	          JsonNode personAsJsonNode = Json.toJson(persona);	
    	          WS.Response response = WS.url("http://localhost:3333/persona").post(personAsJsonNode).get();
    	          final Persona personFromRest = getPersonFromResponse(response);
    	          assertThat(response.getStatus()).isEqualTo(OK);
    	          //assertThat(response.getStatus()).isEqualTo(200); hace lo mismo que la linea anterior
    	          System.out.println("aaaa");
    	          System.out.println(persona.getNombre());
    	          System.out.println("aaaa");
    	          System.out.println(personFromRest.getNombre());
    	          System.out.println("aaaa");
    	          assertThat(persona.getNombre()).isEqualTo(personFromRest.getNombre());
    	          assertThat(persona.getDireccion()).isEqualTo(personFromRest.getDireccion());}
    });
    }
    
    @Test
    public void crearPersonaConError(){
    	
    	running(testServer(3333, fakeApplication()), new Runnable() {
    		public void run() {
    	          Persona persona = new Persona (null, "Diagonal 79");
    	          JsonNode personAsJsonNode = Json.toJson(persona);	
    	          WS.Response response = WS.url("http://localhost:3333/persona").post(personAsJsonNode).get();
    	          assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
    	          //assertThat(response.getStatus()).isEqualTo(400); hace lo mismo que la linea anterior
    	         
    	          }
    });
    	}

        private Persona getPersonFromResponse(WS.Response response) {
    	return getPersonFromJsonNode(response.asJson());
    	}

    	private Persona getPersonFromJsonNode(JsonNode jSonNode) {
    	return Json.fromJson(jSonNode, Persona.class);
    	}
    	
    	@Test
    	public void exitePersonaPorId() {
    		running(testServer(3333, fakeApplication()), new Runnable() {
    			@Override
    			public void run() {
    				WS.Response response = WS.url("http://localhost:3333/persona/1").get().get();

    				// Asserts
    				assertThat(response.getStatus()).isEqualTo(OK);
    				JsonNode jsonResponse = response.asJson();

    				assertThat(jsonResponse.findValue("nombre").asText()).isEqualTo("sabrina");
    				assertThat(jsonResponse.findValue("direccion").asText()).isEqualTo("diagonal 73");

    			}
    		});
    	}
    	
    	@Test
    	public void eliminarPersonaExistente() {
    		running(testServer(3333, fakeApplication()), new Runnable() {
    			@Override
    			public void run() {
    					
    				WS.Response response = WS.url("http://localhost:3333/persona/2").delete().get();

    				assertThat(response.getStatus()).isEqualTo(OK);
    				
    				JPA.withTransaction(new play.libs.F.Callback0() {
    					@Override
    					public void invoke() {
    						Persona persona = JPA.em().find(Persona.class, new Long(2));
    						assertThat(persona).isNull();

    					}
    				});
    					
    			}
    		});
    	}
    	
    	@Test
    	public void modificarPersonaExistente() {
    		running(testServer(3333, fakeApplication()), new Runnable() {
    			@Override
    			public void run() {
    				
    				Persona persona1 = new Persona();
    				persona1.setNombre("liliana");
    				JsonNode personaAsJson = Json.toJson(persona1);
    			// El nombre antes de la modificacion
    			  WS.Response response2 = WS.url("http://localhost:3333/persona/1").get().get();
       	          final Persona personFromRest = getPersonFromResponse(response2);
       	          System.out.println("Nombre antes de la modificacion del id:1 ");
       	          System.out.println(personFromRest.getNombre());
       	       // El nombre antes de la modificacion  
    				
    				WS.Response response = WS.url("http://localhost:3333/persona/1").put(personaAsJson).get();
    				//TODO siempre JSON
    				// Asserts
    				assertThat(response.getStatus()).isEqualTo(OK);
    				//linea 150 hace lo mismo que linea 140 para obtener la Persona 
    				final Persona persona2 = Json.fromJson(response.asJson(), Persona.class);
    				
    				JPA.withTransaction(new play.libs.F.Callback0() {
    					@Override
    					public void invoke() {
    						Persona persona = JPA.em().find(Persona.class, persona2.getId());
    						assertThat(persona.getNombre()).isEqualTo("liliana");
    						System.out.println("Nombre despues de la modificacion del id:1 ");
    						System.out.println(persona.getNombre());
    						assertThat(persona.getDireccion()).isEqualTo("diagonal 73");
    					}
    				});
    			}
    		});
    	}	
}
