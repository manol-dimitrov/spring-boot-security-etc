package com.zamrad.resources;

import com.zamrad.domain.profiles.Profile;
import com.zamrad.utility.SearchService;
import io.searchbox.core.SearchResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search/v1")
@CrossOrigin
@Api(value = "/search", description = "Search.")
public class SearchResource {

    private final SearchService searchService;

    @Autowired
    public SearchResource(SearchService searchService) {
        this.searchService = searchService;
    }

    @ApiOperation(value = "Load search data.")
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    public ResponseEntity<?> search(@RequestParam("query") String query) {
        final SearchResult searchResult = searchService.search(query).orElseThrow(() -> new RuntimeException("No search results."));

        final List<SearchResult.Hit<Profile, Void>> hits = searchResult.getHits(Profile.class);

        return ResponseEntity.ok(hits);
    }
}
