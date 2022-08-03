package es.lavanda.tmdb.service.strategy;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Pattern PATTERN_SHOW_1 = Pattern.compile("(.*).S\\d{1,2}(.*)");
    private static final Pattern PATTERN_SHOW_2 = Pattern.compile("(.*) S\\d{1,2}(.*)");

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
        log.info("Searched {} with results {}", search, searchs.getResults());
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
        String folderName = path.getFileName().toString();
        // "/Users/luiscarlos/Documents/Github/LavandaDelPatio/filebot-executor/src/main/resources/filebot/El
        // incidente [BluRay 1080p][DTS 5.1 Castellano DTS-HD 5.1-Ingles+Subs][ES-EN]";
        Matcher matcher1 = PATTERN_SHOW_1.matcher(folderName);
        Matcher matcher2 = PATTERN_SHOW_2.matcher(folderName);
        if (matcher1.matches()) {
            log.info("Regex {}", PATTERN_SHOW_1.pattern());
            // System.out.println("GROUP 1: *" + matcher1.group(1) + "*");
            // String fileWithDots = generalMatcher.group(1);
            return matcher1.group(1).replace(".", " ");
        } else if (matcher2.matches()) {
            log.info("Regex {}", PATTERN_SHOW_2.pattern());
            // System.out.println("GROUP 1: *" + matcher2.group(1) + "*");
            // String fileWithDots = matcher2.group(1);
            return matcher2.group(1).replace(".", " ");
        } else if (folderName.contains("[") && folderName.contains("]")) {
            return folderName.split("\\[")[0];
        } else if (folderName.contains("(") && folderName.contains(")")) {
            return folderName.split("\\(")[0];
        } else {
            return folderName;
        }
    }

}
