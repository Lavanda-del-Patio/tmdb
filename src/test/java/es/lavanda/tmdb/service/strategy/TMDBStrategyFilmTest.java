package es.lavanda.tmdb.service.strategy;

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

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TMDBStrategyFilmTest {

    @Autowired
    private TMDBStrategyFilm tmdbStrategyFilm ;

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

}
