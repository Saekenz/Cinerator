package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.genre.Genre;
import at.saekenz.cinerator.model.genre.GenreDTO;
import at.saekenz.cinerator.model.genre.GenreDTOModelAssembler;
import at.saekenz.cinerator.model.genre.GenreMapper;
import at.saekenz.cinerator.service.IGenreService;
import at.saekenz.cinerator.util.CollectionModelBuilderService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/genres")
public class GenreController {

    @Autowired
    IGenreService genreService;

    @Autowired
    GenreMapper genreMapper;

    @Autowired
    private CollectionModelBuilderService modelBuilderService;

    private final GenreDTOModelAssembler genreDTOModelAssembler;

    public GenreController(GenreDTOModelAssembler genreDTOModelAssembler) {
        this.genreDTOModelAssembler = genreDTOModelAssembler;
    }

    @GetMapping()
    public ResponseEntity<?> findAllGenres() {
        List<Genre> genres = genreService.findAll();

        if (genres.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<GenreDTO>> collectionModel = modelBuilderService
                .createCollectionModelFromList(genres, genreMapper, genreDTOModelAssembler,
                        linkTo(methodOn(GenreController.class).findAllGenres()).withSelfRel());

        return ResponseEntity.ok(collectionModel);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findGenreById(@PathVariable Long id) {
        Genre genre = genreService.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "Genre"));

        return ResponseEntity
                .ok(genreDTOModelAssembler
                        .toModel(genreMapper.toDTO(genre)));
    }


}
