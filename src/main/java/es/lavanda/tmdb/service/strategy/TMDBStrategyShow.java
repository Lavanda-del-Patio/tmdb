package es.lavanda.tmdb.service.strategy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.lavanda.lib.common.model.MediaIDTO;
import es.lavanda.lib.common.model.MediaODTO;
import es.lavanda.tmdb.model.tmdb.search.TMDBResultDTO;
import es.lavanda.tmdb.model.tmdb.search.TMDBSearchDTO;
import es.lavanda.tmdb.model.type.QueueType;
import es.lavanda.tmdb.service.ProducerService;
import es.lavanda.tmdb.service.impl.TMDBServiceShow;
import es.lavanda.tmdb.util.TmdbUtil;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TMDBStrategyShow implements TMDBStrategy {

    @Autowired
    private ProducerService producerService;
    @Autowired
    private TMDBServiceShow tmdbServiceShow;

    @Override
    public void execute(MediaIDTO mediaDTO, QueueType type) {
        log.info("Strategy Show  with mediaIDTO {}", mediaDTO);
        TMDBSearchDTO searchs = tmdbServiceShow.searchShow(mediaDTO.getTorrentCroppedTitle(),
                null);
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
        mediaODTO.setTitle(firstResult.getName());
        mediaODTO.setTitleOriginal(firstResult.getOriginalName());
        mediaODTO.setIdOriginal(String.valueOf(firstResult.getId()));
        mediaODTO.setImage(TmdbUtil.getW780Image(firstResult.getPosterPath()));
        mediaODTO.setReleaseDate(
                LocalDate.parse(firstResult.getFirstAirDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        mediaODTO.setBackdropImage(TmdbUtil.getOriginalImage(firstResult.getBackdropPath()));
        mediaODTO.setVoteAverage(firstResult.getVoteAverage());
        mediaODTO.setOverview(firstResult.getOverview());
        return mediaODTO;
    }

}
