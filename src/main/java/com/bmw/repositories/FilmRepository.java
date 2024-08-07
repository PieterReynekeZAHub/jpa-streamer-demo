package com.bmw.repositories;

import com.bmw.model.*;
import com.bmw.model.Film$;
import com.speedment.jpastreamer.application.*;
import com.speedment.jpastreamer.projection.*;
import com.speedment.jpastreamer.streamconfiguration.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.transaction.*;

import java.math.*;
import java.util.*;
import java.util.stream.*;

@ApplicationScoped
public class FilmRepository {

    @Inject
    JPAStreamer jpaStreamer;

    private static final int PAGE_SIZE = 20;

    public Optional<Film> getFilm(short filmId){
        return jpaStreamer.stream(Film.class)
                .filter(Film$.fimId.in(filmId))
                .findFirst();
    }

    public Stream<Film> getFilmByMinLength(short minLength){
        return jpaStreamer.stream(Film.class)
                .filter(Film$.length.greaterThan(minLength))
                .sorted(Film$.length);
    }

    public Stream<Film> getPageFilms(long page, short minLength) {
        return jpaStreamer.stream(Projection.select(Film$.fimId, Film$.title, Film$.length))
                .filter(Film$.length.greaterThan(minLength))
                .skip(page * PAGE_SIZE)
                .limit(PAGE_SIZE);

    }

    public Stream<Film> actors(String startsWith){
        final StreamConfiguration<Film> configuration = StreamConfiguration.of(Film.class)
                .joining(Film$.actors);
        return jpaStreamer.stream(configuration)
                .filter(Film$.title.startsWith(startsWith))
                .sorted(Film$.length.reversed());
    }

    @Transactional
    public void updateRentalRate(short minLength, Float rentalRate){
        jpaStreamer.stream(Film.class)
                .filter(Film$.length.greaterThan(minLength))
                .forEach(f -> {
                    f.setRentalRate(BigDecimal.valueOf(rentalRate));
                });
    }
}
