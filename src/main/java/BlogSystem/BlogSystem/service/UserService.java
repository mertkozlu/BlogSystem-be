package BlogSystem.BlogSystem.service;

import BlogSystem.BlogSystem.dataAccess.CommentRepository;
import BlogSystem.BlogSystem.dataAccess.PostRepository;
import BlogSystem.BlogSystem.dataAccess.UserRepository;
import BlogSystem.BlogSystem.dto.GetAllUsersDto;
import BlogSystem.BlogSystem.dto.requests.AddUserRequest;
import BlogSystem.BlogSystem.dto.requests.UpdateUserRequest;
import BlogSystem.BlogSystem.dto.responses.GetAllUserResponse;
import BlogSystem.BlogSystem.entities.Comment;
import BlogSystem.BlogSystem.entities.Post;
import BlogSystem.BlogSystem.entities.User;
import BlogSystem.BlogSystem.exception.BusinessException;
import BlogSystem.BlogSystem.mapper.ModelMapperService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapperService modelMapperService;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public UserService(UserRepository userRepository, ModelMapperService modelMapperService,
                       CommentRepository commentRepository,
                       PostRepository postRepository) {
        this.userRepository = userRepository;
        this.modelMapperService = modelMapperService;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public GetAllUserResponse getAllUsers() {
        GetAllUserResponse response = new GetAllUserResponse();
        List<GetAllUsersDto> dtos = userRepository.findAll()
                .stream()
                .filter(Objects::nonNull)
                .map(this::convertUserToGetAllUsersDto)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(dtos)) {
            throw new BusinessException("Empty list");
        }

        response.setGetAllUsersDto(dtos);
        response.setResultCode("1");
        response.setResultDescription("Success");

        return response;

    }

    public User saveOneUser(AddUserRequest newUser) {
        User user = this.modelMapperService.forRequest().map(newUser, User.class);
        user.setCreationDate(new Date());

        return userRepository.save(user);
    }

    public void deleteOneUserById(Long userId) {
        Integer userCount = userRepository.countUser();
        if (userCount <= 1) {
            throw new BusinessException("User cannot be deleted there most be add list one user.");
        }
        List<Comment> comments = commentRepository.findByUser_UserId(userId);
        Post post = postRepository.findByUser_UserId(userId);
        if (!CollectionUtils.isEmpty(comments) || Objects.nonNull(post)) {
            throw new BusinessException("User cannot be deleted while the user has posts and comments.");
        }
        this.userRepository.deleteById(userId);
    }

    public void updateOneUser(UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(updateUserRequest.getUserId()).orElseThrow(() -> new BusinessException("User can not found"));
        User userToUpdate = this.modelMapperService.forRequest().map(updateUserRequest, User.class);
        userToUpdate.setCreationDate(user.getCreationDate());
        this.userRepository.save(userToUpdate);

    }


    private GetAllUsersDto convertUserToGetAllUsersDto(User user) {
        GetAllUsersDto getAllUsersDto = new GetAllUsersDto();
        getAllUsersDto.setUserId(user.getUserId());
        getAllUsersDto.setUserName(user.getUserName());
        getAllUsersDto.setEmail(user.getEmail());
        getAllUsersDto.setCreationDate(user.getCreationDate());

        return getAllUsersDto;

    }
}