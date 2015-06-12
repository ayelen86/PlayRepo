package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.management.InvalidAttributeValueException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

import models.Persona;
import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;

public class PersonaController extends Controller {

	@Transactional
	public static Result list() {
		List<Persona> personas = Persona.listAll();
		return ok(Json.toJson(personas));
	}

	@Transactional
	public static Promise<Result> crearPersona(){
		
		Persona persona = Json.fromJson(request().body().asJson(), Persona.class);
    	//if (persona == null) 
		//por esta condicion porque el atributo nombre tiene una Constraints
		if (persona.getNombre() == null ){         
    		return Promise.<Result>pure(badRequest("bad_request"));
		}
    	
    	Promise<Persona> proPersona = Persona.crearPersonaAsync(persona);
  	
		return proPersona.map(new Function<Persona, Result>(){
			public Result apply(Persona persona) throws Throwable {
//				if (book == null) return badRequest("bad_request");
				
				Persona personaD = new Persona();
				personaD.setId(persona.getId());
				personaD.setNombre(persona.getNombre());
				personaD.setDireccion(persona.getDireccion());
				
				return ok(Json.toJson(personaD));
			}
		}).recover(new Function<Throwable, Result>() {
			@Override
			public Result apply(Throwable e) throws Throwable {
				
				
				Logger.warn(e.getMessage());
				
				if (e instanceof InvalidAttributeValueException) {
					Logger.error("Bad request", e);
					return badRequest();
				}
				return managerError(e);
			}
		});
//	System.out.println("Hola");
//	return null;
	}
	
private static Result managerError(Throwable e) {
		
		if (e.getCause() instanceof EntityNotFoundException) {
			
		}
		if (e.getCause() instanceof NoResultException) {
			Logger.error("The person wasn't created.", e);
			return notFound();
		} else if (e instanceof InvalidAttributeValueException) {
			Logger.error("Bad request", e);
			return badRequest();
		} else {
			Logger.error("Internal server error", e);
			return internalServerError();
		}
	}
	
@Transactional
public static Promise<Result> buscarPersonaPorId(final Long id) {

	Promise<Persona> promisePersona = Persona.buscarPersonaAsync(id);
	return promisePersona.map(new Function<Persona, Result>() {

		@Override
		public Result apply(Persona promisePersona) throws Throwable {

			if (promisePersona != null) {
				return ok(Json.toJson(promisePersona));
			} else {
				return notFound("No existe la persona.");
			}

		}
	}).recover(new Function<Throwable, Result>() {
		@Override
		public Result apply(Throwable e) throws Throwable {
			return internalServerError();

		}
	});

}

@Transactional
public static Promise<Result> modificarPersona(final Long id) {

		final Persona newPersona = Json.fromJson(request().body().asJson(), Persona.class);
		if (newPersona == null) {
 		return Promise.<Result>pure(badRequest("bad_request"));
		}

 	final Promise<Persona> promisePersona = Persona.buscarPorId(id);
 	
 	if (promisePersona == null) {
 		return Promise.<Result>pure(notFound("not_found"));
		}
 	
 	return promisePersona.flatMap(new Function<Persona, Promise<Result>>() {
         public Promise<Result> apply(Persona promisePersona) throws Throwable {
        	 final Promise<Boolean> updatedPersona = Persona.modificarAsync(promisePersona, newPersona);
             return updatedPersona.map(new Function<Boolean, Result>() {
                 
            	 public Result apply(Boolean updatedPersona) throws Throwable {
						Map<String, String> result = new HashMap<String, String>();
						result.put("id", id.toString());
						if (updatedPersona == true) {
							return ok(Json.toJson(result));
						} else {
							return badRequest(Json.toJson(result));
						}   
                 }
             
             });
         }	
 	}).recover(new Function<Throwable, Result>() {
			@Override
			public Result apply(Throwable e) throws Throwable {
				if (e instanceof EntityNotFoundException) {
					Logger.error("Person not found.", e);
					return notFound("not_found");
				}
				return managerError(e);
			}
		});

 }

// hacer con flatmap promise anidados
   @Transactional
   public static Promise<Result> eliminarPersonaPorId(final Long id) {
			
		if (id == null) {
   		return Promise.<Result>pure(badRequest("bad_request"));
		}
   	
		final Promise<Persona> promisePersona = Persona.buscarPorId(id);
   	
   	if (promisePersona == null) {
   		return Promise.<Result>pure(notFound("not_found"));
		}
   	
   	return promisePersona.flatMap(new Function<Persona, Promise<Result>>() {
   		
            public Promise<Result> apply(Persona promisePersona) throws Throwable {
           	 final Promise<Boolean> deletedPersona = Persona.eliminarAsync(promisePersona);
                return deletedPersona.map(new Function<Boolean, Result>() {
                    
               	 public Result apply(Boolean deletedPersona) throws Throwable {
           	 		 Map<String, String> result = new HashMap<String, String>();
           	 		 result.put("id", id.toString());
                   	 if (deletedPersona == true) {
                   		 return ok(Json.toJson(result));
						} else {
							return badRequest(Json.toJson(result));
						}   
                    }
                
                });
            }
        }).recover(new Function<Throwable, Result>() {
			@Override
			public Result apply(Throwable e) throws Throwable {
				if (e instanceof EntityNotFoundException) {
					Logger.error("Person not found.", e);
					return notFound("not_found");
				}
				return managerError(e);
			}
		});
   }  
	
}
