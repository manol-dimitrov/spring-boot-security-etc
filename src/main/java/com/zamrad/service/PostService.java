package com.zamrad.service;

import com.zamrad.domain.posts.Post;
import com.zamrad.domain.posts.PostImage;
import com.zamrad.domain.profiles.Profile;
import com.zamrad.dto.Image;
import com.zamrad.dto.posts.NewPostDto;
import com.zamrad.repository.PostRepository;
import com.zamrad.service.artist.ArtistProfileNotFoundException;
import com.zamrad.service.artist.ProfileService;
import com.zamrad.service.photos.ProfilePhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;

@Component
public class PostService {

    private final static Function<NewPostDto, Post.PostBuilder> CONVERT_TO_POST = newPostDto -> Post.builder()
            .type(newPostDto.getType())
            .content(newPostDto.getContent())
            .dateTime(LocalDateTime.now())
            .link(newPostDto.getLink());

    private final static Function<Image, PostImage> CONVERT_TO_POST_IMAGE = image -> PostImage.builder().url(image.getUrl()).build();

    @Autowired
    private ProfilePhotoService profilePhotoService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private PostRepository postRepository;

    public Post createPost(NewPostDto newPost, MultipartFile[] images, Long socialId) {
        final Post.PostBuilder postBuilder = CONVERT_TO_POST.apply(newPost);

        final UUID posterId = getPosterProfile(socialId).getId();
        postBuilder.posterId(posterId);

        final List<List<Image>> uploadMultiplePhotos;
        final Set<Image> imageList;
        final Set<PostImage> postImages;

        if (!CollectionUtils.isEmpty(Arrays.asList(images))) {
            uploadMultiplePhotos = profilePhotoService.uploadImages(images);

            imageList = uploadMultiplePhotos.stream().flatMap(Collection::stream).collect(toSet());
            postImages = imageList.stream().map(CONVERT_TO_POST_IMAGE).collect(toSet());

            postBuilder.images(postImages);
        }

        return postRepository.save(postBuilder.build());
    }

    public Post getPost(UUID postId) {
        return postRepository.getOne(postId);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    private Profile getPosterProfile(Long socialId) {
        final Optional<Profile> myProfile = profileService.getMyProfile(socialId);
        return myProfile.orElseThrow(() -> new ArtistProfileNotFoundException("Profile does not exist."));
    }
}
