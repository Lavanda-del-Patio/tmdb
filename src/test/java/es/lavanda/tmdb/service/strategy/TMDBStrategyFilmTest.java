package es.lavanda.tmdb.service.strategy;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import es.lavanda.lib.common.model.TelegramFilebotExecutionIDTO;

// @ExtendWith(SpringExtension.class)
// @SpringBootTest
// @AutoConfigureMockMvc
public class TMDBStrategyFilmTest {

    // @Autowired
    private TMDBStrategyFilm tmdbStrategyFilm;

    @Test
    @Disabled
    public void getStrategyWithTypeFilm() {
        TelegramFilebotExecutionIDTO telegramFilebotExecutionIDTO = new TelegramFilebotExecutionIDTO();
        telegramFilebotExecutionIDTO.setFile("El incidente BD1080.atomixhq.net.mkv");
        telegramFilebotExecutionIDTO.setPath(
                "/Users/luiscarlos/Documents/Github/LavandaDelPatio/filebot-executor/src/main/resources/filebot/El incidente [BluRay 1080p][DTS 5.1 Castellano DTS-HD 5.1-Ingles+Subs][ES-EN]");
        tmdbStrategyFilm.execute(telegramFilebotExecutionIDTO);
        // TMDBSearchDTO searchDTO = new TMDBSearchDTO();
        // List<TMDBResultDTO> results = new ArrayList<>();
        // TMDBResultDTO tmdb = new TMDBResultDTO();
        // tmdb.setMediaType(MediaTypeEnum.MOVIE);
        // results.add(tmdb);
        // searchDTO.setResults(results);
        // Optional<TMDBStrategy> strategy = tmdbStrategy.getFactory("wonderwoman",
        // Type.SHOW);
        // Assertions.assertEquals(tmdbStrategyShow, strategy.get());
    }

    @Test
    @Disabled
    public void tesss() {
        String path = "Peaky.Blinders.S06.2160p.WEB-DL.DDP5.1.HLG.HDR.HEVC-SEXXY";
        String pattern = "(.*).S0(.*)";
        // String test = "(test1,test2)";
        // Pattern PATTERN_SHOW_1 = Pattern.compile(pattern);
        Matcher generalMatcher = Pattern.compile(pattern).matcher(path);
        System.out.println(path.matches(pattern));
        System.out.println(generalMatcher.matches());
        System.out.println(generalMatcher.find());
        System.out.println(
                "SHORTPATH *" + getShortPath("Peaky.Blinders.S06.2160p.WEB-DL.DDP5.1.HLG.HDR.HEVC-SEXXY") + "*");

    }

    private static final Pattern PATTERN_SHOW_1 = Pattern.compile("(.*).S0(.*)");

    private String getShortPath(String filebotPath) {
        Path path = Path.of(filebotPath);
        String fileName = path.getFileName().toString();
        System.out.println("Parent path " + fileName);
        // "/Users/luiscarlos/Documents/Github/LavandaDelPatio/filebot-executor/src/main/resources/filebot/El
        // incidente [BluRay 1080p][DTS 5.1 Castellano DTS-HD 5.1-Ingles+Subs][ES-EN]";
        Matcher generalMatcher = PATTERN_SHOW_1.matcher(fileName);

        // System.out.println(generalMatcher.matches());
        if (fileName.contains("[") && fileName.contains("]")) {
            return fileName.split("\\[")[0];
        } else if (fileName.contains("(") && fileName.contains(")")) {
            return fileName.split("\\(")[0];
        } else if (generalMatcher.matches()) {
            // log.info("Regex {}", PATTERN_SHOW_1.pattern());
            // System.out.println("GROUP 1: *" + generalMatcher.group(1) + "*");
            // String fileWithDots = generalMatcher.group(1);
            return generalMatcher.group(1).replace(".", " ");
            // return fileWithDots;
        } else {
            return fileName;
        }
    }

}
