package com.zamrad.service;

import com.zamrad.domain.posts.Post;
import com.zamrad.domain.posts.PostImage;
import com.zamrad.dto.Image;
import com.zamrad.dto.posts.NewPostDto;
import com.zamrad.repository.PostRepository;
import com.zamrad.service.photos.ProfilePhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;

@Component
public class PostService {

    private final static Function<NewPostDto, Post.PostBuilder> CONVERT_TO_POST = newPostDto -> Post.builder()
            .content(newPostDto.getContent())
            .dateTime(LocalDateTime.now())
            .link(newPostDto.getLink());

    private final static Function<Image, PostImage> CONVERT_TO_POST_IMAGE = image -> PostImage.builder().url(image.getUrl()).build();

    @Autowired
    private ProfilePhotoService profilePhotoService;

    @Autowired
    private PostRepository postRepository;

    public Post createPost(NewPostDto newPost, MultipartFile[] images, Long socialId) {
        final Post.PostBuilder postBuilder = CONVERT_TO_POST.apply(newPost);
        final List<List<Image>> uploadMultiplePhotos = profilePhotoService.uploadMultiplePhotos(images);
        final Set<Image> imageList = uploadMultiplePhotos.stream().flatMap(Collection::stream).collect(toSet());

        final Set<PostImage> postImages = imageList.stream().map(CONVERT_TO_POST_IMAGE).collect(toSet());

        final Post post = postBuilder.images(postImages).build();

        return postRepository.save(post);
    }
}
