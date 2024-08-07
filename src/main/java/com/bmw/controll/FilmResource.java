package com.bmw.controll;

import com.bmw.model.*;
import com.bmw.repositories.*;
import jakarta.inject.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import java.util.*;
import java.util.stream.*;

@Path("/")
public class FilmResource {

    @Inject
    FilmRepository filmRepository;

    @GET
    @Path("/hello-world")
    @Produces(MediaType.TEXT_PLAIN)
    public String helloWorld(){
        return "Hello World";
    }

    @GET
    @Path("/findFilm/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getFilm(@PathParam("id") short id){
        Optional<Film> film = filmRepository.getFilm(id);
        return film.isPresent()? film.get().getTitle(): "Film not found";
    }

    @GET
    @Path("/films/{page}/{minLength}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPageFilms(@PathParam("page") long page, @PathParam("minLength") short minLength){
        return filmRepository.getPageFilms(page, minLength).map( f -> String.format("%s %s", f.getTitle(), f.getLength()))
                .collect(Collectors.joining("\n"));
    }

    @GET
    @Path("/actors/{startsWith}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPageFilms(@PathParam("startsWith") String startsWith){
        return filmRepository.actors(startsWith).map( f -> String.format("%s (%d min): %s",
                        f.getTitle(),
                        f.getLength(),
                        f.getActors().stream()
                                .map(a -> String.format("%s %s", a.getFirstName(), a.getLastName())).collect(Collectors.joining(", "))))
                .collect(Collectors.joining("\n"));
    }

    @GET
    @Path("/films/{minLength}/{rate}")
    @Produces(MediaType.TEXT_PLAIN)
    public String updateFilmRentalRate(@PathParam("minLength") short minlength, @PathParam("rate") Float rate){

        filmRepository.updateRentalRate(minlength, rate);
        return filmRepository.getFilmByMinLength(minlength).map( f -> String.format("%s (%d min) - R%f", f.getTitle(), f.getLength(), f.getRentalRate()))
                .collect(Collectors.joining("\n"));
    }
}
