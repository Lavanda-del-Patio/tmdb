package es.lavanda.tmdb.model.tmdb.search;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TMDBSearchDTO {
    private int page;

    @JsonProperty("total_results")
    private int totalResults;

    @JsonProperty("total_pages")
    private int totalPages;

    private List<TMDBResultDTO> results;

}
