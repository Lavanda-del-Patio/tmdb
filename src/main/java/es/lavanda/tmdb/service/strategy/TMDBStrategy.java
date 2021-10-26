package es.lavanda.tmdb.service.strategy;

import es.lavanda.lib.common.model.MediaIDTO;
import es.lavanda.tmdb.model.type.QueueType;

public interface TMDBStrategy {

    void execute( MediaIDTO mediaDTO, QueueType type);

}
