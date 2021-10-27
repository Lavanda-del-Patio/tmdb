package es.lavanda.tmdb.service.strategy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import es.lavanda.lib.common.model.MediaIDTO;
import es.lavanda.lib.common.model.MediaODTO;
import es.lavanda.tmdb.model.tmdb.search.TMDBResultDTO;
import es.lavanda.tmdb.model.tmdb.search.TMDBSearchDTO;
import es.lavanda.tmdb.model.type.QueueType;
import es.lavanda.tmdb.service.ProducerService;
import es.lavanda.tmdb.service.impl.TMDBServiceFilm;
import es.lavanda.tmdb.util.TmdbUtil;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TMDBStrategyFilm implements TMDBStrategy {

    @Autowired
    private ProducerService producerService;

    @Autowired
    private TMDBServiceFilm tmdbServiceFilmImpl;

    @Override
    public void execute(MediaIDTO mediaDTO, QueueType type) {
        log.info("On strategy Film with mediaIDTO {}", mediaDTO);
        TMDBSearchDTO searchs = tmdbServiceFilmImpl.searchFilm(mediaDTO.getTorrentCroppedTitle(),
                mediaDTO.getTorrentYear());
        log.info("Results of the search {}", searchs.getResults());
        if (Boolean.FALSE.equals(searchs.getResults().isEmpty())) {
            MediaODTO mediaODTO = createMediaODTO(searchs, mediaDTO);
            log.info("Ready to send Message with mediaODTO ", mediaODTO);
            producerService.sendMessage(mediaODTO, type);
        }

    }

    private static MediaODTO createMediaODTO(TMDBSearchDTO searchs, MediaIDTO mediaIDTO) {
        TMDBResultDTO firstResult = searchs.getResults().get(0);
        MediaODTO mediaODTO = new MediaODTO();
        mediaODTO.setId(mediaIDTO.getId());
        mediaODTO.setTitle(firstResult.getTitle());
        mediaODTO.setTitleOriginal(firstResult.getOriginalTitle());
        mediaODTO.setIdOriginal(String.valueOf(firstResult.getId()));
        mediaODTO.setImage(TmdbUtil.getW780Image(firstResult.getPosterPath()));
        if (StringUtils.hasText(firstResult.getReleaseDate())) {
            mediaODTO.setReleaseDate(
                    LocalDate.parse(firstResult.getReleaseDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        mediaODTO.setBackdropImage(TmdbUtil.getOriginalImage(firstResult.getBackdropPath()));
        mediaODTO.setVoteAverage(firstResult.getVoteAverage());
        mediaODTO.setOverview(firstResult.getOverview());
        return mediaODTO;
    }

}
