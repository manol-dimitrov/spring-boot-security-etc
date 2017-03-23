package com.zamrad.resources;

import com.zamrad.domain.posts.Post;
import com.zamrad.dto.posts.NewPostDto;
import com.zamrad.dto.posts.PostDto;
import com.zamrad.service.PostService;
import com.zamrad.service.user.Auth0TokenService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.Arrays;
import java.util.Objects;

@RestController
@RequestMapping("/posts/v1")
@CrossOrigin
@Api(value = "/posts", description = "Create, update or delete a community post.")
public class PostResource {
    private static final String POST_MEDIA_TYPE = "application/json; charset=UTF-8";

    @Autowired
    private PostService postService;

    @Autowired
    private Auth0TokenService auth0TokenService;

    @Autowired
    private org.springframework.core.env.Environment environment;

    @ApiOperation(value = "Retrieve a post by its id.", response = PostDto.class, produces = POST_MEDIA_TYPE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Post retrieved successfully."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials/request body."),
            @ApiResponse(code = 404, message = "No post with the given id exists.")
    })
    @RequestMapping(value = "/{postId}", method = RequestMethod.GET, produces = POST_MEDIA_TYPE)
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    public void getPost() {

    }

    @ApiOperation(value = "Create a community post.", response = PostDto.class, produces = POST_MEDIA_TYPE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Post created successfully."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials/request body."),
            @ApiResponse(code = 400, message = "The request body contains invalid fields.")
    })
    @RequestMapping(method = RequestMethod.POST, produces = POST_MEDIA_TYPE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    public ResponseEntity<?> createPost(@RequestPart(value = "images", required = false) MultipartFile[] photos,
                                        @RequestBody NewPostDto newPost,
                                        @ApiIgnore final Principal principal) {
        final Post post;
        try {
            post = postService.createPost(newPost, photos, getUserSocialId());
        } catch (Exception ex) {
            String error = String.format("{\"message\": \"%s\"}", ex.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(post.getId()).toUri());

        return new ResponseEntity<>(post, headers, HttpStatus.CREATED);
    }

    private Long getUserSocialId() {
        if (Arrays.stream(environment.getActiveProfiles()).anyMatch(profile -> Objects.equals(profile, "dev"))) {
            return 1392995950728274L;
        }
        return Long.valueOf(auth0TokenService.getSocialUserId().substring(9));
    }
}
