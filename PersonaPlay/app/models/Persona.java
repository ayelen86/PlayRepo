package models;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import java.util.List;

import play.data.validation.*;
import play.db.jpa.JPA;
import play.libs.F.Function0;
import play.libs.F.Promise;

//import play.db.jpa.JPA;


@Entity
@Table(name = "PERSON")
@SequenceGenerator(name="person_seq",sequenceName="person_seq")
public class Persona {
	@Column(name = "ID", nullable = false, columnDefinition = "NUMBER(10)")
	private Long id;
	@Constraints.Required
	@Column(name = "NOMBRE")
	private String nombre;
	@Column(name = "DIRECCION")
	private String direccion;
	
	public Persona (){
		
	}
	
	public Persona (String nombre,String direccion){
		this.nombre = nombre;
		this.setDireccion(direccion);
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="person_seq")
	public Long getId() {	
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}


	public String getDireccion() {
		return direccion;
	}


	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public static List<Persona> listAll() {
		//List<Persona> personas = JPA.em().createQuery("from Persona order by id").getResultList();
		//return personas;
		TypedQuery<Persona> query = JPA.em().createQuery("from Persona", Persona.class);
		return query.getResultList();
	}
	
	public static Promise<Persona> buscarPorId(final Long id) {
 
		return Promise.promise(new Function0<Persona>() {
			public Persona apply() throws Throwable {
				return JPA.withTransaction(new Function0<Persona>() {
					public Persona apply() throws Throwable {
						EntityManager e = JPA.em();
						Persona persona = e.find(Persona.class, id);
						return persona;
					} 
				});
			}			
		});
	}
	
	public static Promise<Persona> crearPersonaAsync(final Persona persona){
		//TODO lanzar la exception y manejarla con recover
		return Promise.promise(new Function0<Persona>() {
			public Persona apply() throws Throwable {
				
//				try {
					return JPA.withTransaction(new Function0<Persona>() {
						public Persona apply() throws Throwable {
							EntityManager e = JPA.em();
							e.persist(persona);
							return persona;
						} 
					});
//				} catch (Throwable e) {
//					e.printStackTrace();
//					return null;
//				} 
			}
		});		
	}
	
	public static Promise<Persona> buscarPersonaAsync(final Long personaId) {

		return Promise.promise(new Function0<Persona>() {
			public Persona apply() throws Throwable {

				try {
					return JPA.withTransaction(new Function0<Persona>() {
						public Persona apply() throws Throwable {
							Persona personResult = JPA.em().find(Persona.class,
									personaId);

							return personResult;
						}
					});
				} catch (Throwable e) {
					e.printStackTrace();
				}
				return null;
			}
		});

	}
	
public static Promise<Boolean> modificarAsync(final Persona persona, Persona newPersona){
		
		updatePersona(persona, newPersona);

		return Promise.promise(new Function0<Boolean>() {
			public Boolean apply() throws Throwable {
				
//				try {
					return JPA.withTransaction(new Function0<Boolean>() {
						public Boolean apply() throws Throwable {
							EntityManager e = JPA.em();
							e.merge(persona);
							return true;
						}	
					});
//				} catch (Exception e) {
//					e.printStackTrace();
//					return null;
//				}
			}
		});
		
	}

private static void updatePersona(final Persona persona, Persona newPersona) {
	if (newPersona.getNombre() != null && !newPersona.getNombre().equals(persona.getNombre())) {
		persona.setNombre(newPersona.getNombre());
	}
	
	if (newPersona.getDireccion() != null && !newPersona.getDireccion().equals(persona.getDireccion())) {
		persona.setDireccion(newPersona.getDireccion());
	}
	
}
	
	public static Promise<Boolean> eliminarAsync(final Persona promisePersona) throws Throwable {

		return Promise.promise(new Function0<Boolean>() {
			public Boolean apply() throws Throwable {
				return JPA.withTransaction(new Function0<Boolean>() {
					public Boolean apply() throws Throwable { 
						EntityManager em = JPA.em();
						em.remove(em.contains(promisePersona) ? promisePersona : em.merge(promisePersona));
						return true;
					}
				});
			}
		});	
	}

}
