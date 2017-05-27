package com.zamrad.resources;

import com.zamrad.utility.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search/v1")
@CrossOrigin
@Api(value = "/search", description = "Search.")
public class SearchResource {

    @Autowired
    SearchService searchService;

    @ApiOperation(value = "Load search data.")
    @RequestMapping(value = "/load", method = RequestMethod.GET)
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    public void loadData() {
        searchService.loadAllData();
    }
}
