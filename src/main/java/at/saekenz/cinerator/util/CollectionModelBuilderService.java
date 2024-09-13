package at.saekenz.cinerator.util;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.movie.MovieDTO;
import at.saekenz.cinerator.model.movie.MovieDTOModelAssembler;
import at.saekenz.cinerator.model.movie.MovieMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CollectionModelBuilderService {

    @Autowired
    MovieMapper movieMapper;

    @Autowired
    MovieDTOModelAssembler movieDTOAssembler;

    /**
     *
     * @param movies {@link List} of {@link MovieDTO} objects that will be transformed to
     * {@link List} of EntityModel<{@link MovieDTO}>
     * @param selfLink link to the resource
     * @return {@link CollectionModel} that contains EntityModel<Movie> objects
     */
    public CollectionModel<EntityModel<MovieDTO>> createCollectionModelFromList(
            Collection<Movie> movies, Link selfLink) {

        Collection<EntityModel<MovieDTO>> movieModels = movies.stream()
                .map(movieMapper::toDTO)
                .map(movieDTOAssembler::toModel)
                .toList();

        return CollectionModel.of(movieModels, selfLink);
    }

    /**
     *
     * @param entities Collection of entities that will be transformed to {@link CollectionModel}
     * @param entityMapper used to map {@code Entity} to {@code EntityDTO}
     * @param entityDTOAssembler used to transform {@code Entity} to {@code EntityModel<Entity>}
     * @param selfLink link to the resource
     * @return {@link CollectionModel} that contains {@code EntityModel<Entity>} objects
     */
    public <T, D> CollectionModel<EntityModel<D>> createCollectionModelFromList(
            Collection<T> entities, EntityMapper<T, D> entityMapper,
            RepresentationModelAssembler<D, EntityModel<D>> entityDTOAssembler,
            Link selfLink) {

        Collection<EntityModel<D>> entityModels = entities.stream()
                .map(entityMapper::toDTO)
                .map(entityDTOAssembler::toModel)
                .toList();

        return CollectionModel.of(entityModels, selfLink);
    }
}
