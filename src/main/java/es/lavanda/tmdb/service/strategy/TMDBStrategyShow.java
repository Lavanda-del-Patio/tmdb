package es.lavanda.tmdb.service.strategy;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.lavanda.lib.common.model.MediaIDTO;
import es.lavanda.lib.common.model.MediaODTO;
import es.lavanda.lib.common.model.TelegramFilebotExecutionIDTO;
import es.lavanda.lib.common.model.TelegramFilebotExecutionODTO;
import es.lavanda.lib.common.model.tmdb.search.TMDBResultDTO;
import es.lavanda.lib.common.model.tmdb.search.TMDBSearchDTO;
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

    @Override
    public void execute(TelegramFilebotExecutionIDTO telegramFilebotExecutionIDTO) {
        log.info("Strategy Show  with telegramFilebotExecutionIDTO {}", telegramFilebotExecutionIDTO);
        String search = getShortPath(telegramFilebotExecutionIDTO.getPath());
        TMDBSearchDTO searchs = tmdbServiceShow.searchShow(search,
                null);
        log.info("Results of the search {}", searchs.getResults());
        // if (Boolean.FALSE.equals(searchs.getResults().isEmpty())) {
        TelegramFilebotExecutionODTO telegramFilebotExecutionODTO = createTelegramFilebotExecutionODTO(searchs,
                telegramFilebotExecutionIDTO.getId());
        log.info("Ready to send Message with TelegramFilebotExecutionODTO ", telegramFilebotExecutionODTO);
        producerService.sendMessage(telegramFilebotExecutionODTO, QueueType.TELEGRAM_QUERY_TMDB_RESOLUTION);
        // }
    }

    private TelegramFilebotExecutionODTO createTelegramFilebotExecutionODTO(TMDBSearchDTO searchs, String id) {
        TelegramFilebotExecutionODTO telegramFilebotExecutionODTO = new TelegramFilebotExecutionODTO();
        telegramFilebotExecutionODTO.setId(id);
        Map<String, TMDBResultDTO> possibleChoices = new HashMap<>();
        for (TMDBResultDTO tMDBResultDTO : searchs.getResults()) {
            possibleChoices.put(String.valueOf(tMDBResultDTO.getId()), tMDBResultDTO);
        }
        telegramFilebotExecutionODTO.setPossibleChoices(possibleChoices);
        return telegramFilebotExecutionODTO;
    }

    private String getShortPath(String filebotPath) {
        Path path = Path.of(filebotPath);
        log.info("Parent path {}", path.getFileName().toString());
        // "/Users/luiscarlos/Documents/Github/LavandaDelPatio/filebot-executor/src/main/resources/filebot/El
        // incidente [BluRay 1080p][DTS 5.1 Castellano DTS-HD 5.1-Ingles+Subs][ES-EN]";
        if (path.getFileName().toString().contains("[") && path.getFileName().toString().contains("]")) {
            return path.getFileName().toString().split("\\[")[0];
        } else if (path.getFileName().toString().contains("(") && path.getFileName().toString().contains(")")) {
            return path.getFileName().toString().split("\\(")[0];
        } else
            return path.getFileName().toString();
    }

}
