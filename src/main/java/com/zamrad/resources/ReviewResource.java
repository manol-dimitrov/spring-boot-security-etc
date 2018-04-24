package com.zamrad.resources;

import com.zamrad.domain.reviews.Review;
import com.zamrad.dto.profiles.NewReviewDto;
import com.zamrad.service.review.ReviewNotFoundException;
import com.zamrad.service.review.ReviewService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/reviews/v1")
@CrossOrigin
@Api(value = "/reviews", description = "Create, update or delete a review.")
public class ReviewResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewResource.class);
    private static final String REVIEW_MEDIA_TYPE = "application/json; charset=UTF-8";

    private final ReviewService reviewService;

    @Autowired
    public ReviewResource(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @ApiOperation(value = "Retrieve a review for an artist by id.", response = Review.class, produces = REVIEW_MEDIA_TYPE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Review retrieved successfully."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials/request body."),
            @ApiResponse(code = 404, message = "No review with the given id exists.")
    })
    @RequestMapping(value = "/{reviewId}", method = RequestMethod.GET, produces = REVIEW_MEDIA_TYPE)
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    public ResponseEntity<?> getReview(@PathVariable("reviewId") UUID reviewId, @ApiIgnore final Principal principal) {
        final Review review;
        try {
            review = reviewService.getReview(reviewId);
        } catch (ReviewNotFoundException ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Object>(review, HttpStatus.OK);
    }

    @ApiOperation(value = "Create a review for an artist.", consumes = REVIEW_MEDIA_TYPE, produces = REVIEW_MEDIA_TYPE)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Review created successfully."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials/request body."),
            @ApiResponse(code = 422, message = "The request body had some fields that violated constraints.")
    })
    @RequestMapping(value = "/artist/{artistId}", method = RequestMethod.POST, produces = REVIEW_MEDIA_TYPE, consumes = REVIEW_MEDIA_TYPE)
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    public ResponseEntity<?> createReview(@RequestBody NewReviewDto newReviewDto, @PathVariable UUID artistId, @ApiIgnore final Principal principal) {
        final Review review = reviewService.createReview(artistId, newReviewDto);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(review.getId()).toUri());

        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Deletes an existing review for an artist.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted the review."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials."),
            @ApiResponse(code = 404, message = "No review was found with the given id"),
    })
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    public ResponseEntity<?> deleteReview(@PathVariable UUID reviewId, @ApiIgnore final Principal principal) {
        try {
            reviewService.deleteReview(reviewId);
        } catch (ReviewNotFoundException ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }
}
