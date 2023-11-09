package capstone.capstone.controller;

import capstone.capstone.domain.Location;
import capstone.capstone.dto.PostResponse;
import capstone.capstone.domain.Post;
import capstone.capstone.service.ListService;
import capstone.capstone.service.ModelService;
import capstone.capstone.service.PostService;;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController // @Controller에 @ResponseBody가 결합한 어노테이션, 문자열과 JSON 등을 전송 가능
@RequestMapping("/api") // 요청에 대해 어떤 Controller, 어떤 메소드가 처리할지를 맵핑하기 위한 어노테이션
public class PostController {
    @Autowired // 객체 생성 시점에 스프링 컨테이너에서 해당 스프링 빈을 찾아서 주입
    private PostService postService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private ListService listService;

    // 게시글 저장 (리액트)
    @PostMapping("/post/react") // POST 방식: 특정 데이터를 서버로 제출하여 해당 데이터를 추가, 수정 또는 삭제하기 위해 데이터를 전송하는 방식
    public void createPost(
            @RequestPart(value = "post", required = false) // multipart/form-data에 특화되어 여러 복잡한 값을 처리할 때 사용할 수 있는 어노테이션
            Post post,
            @RequestPart(value = "files") // 쿼리 파라미터, 폼 데이터, Multipart 등 많은 요청 파라미터를 처리할 수 있는 어노테이션
            List<MultipartFile> files
    ) throws Exception {
        System.out.println(post.getUserNum() + "번 사용자 -> " + post.getModel().getModelName() + " 판매 게시글 작성");
        postService.createPost(post, files);
    }

    // 게시글 저장 (리액트 네이티브)
    @PostMapping("/post")
    public void createPost(
            @RequestPart(value = "userNum")
            String userNum,
            @RequestPart(value = "modelName")
            String modelName,
            @RequestPart(value = "grade")
            String grade,
            @RequestPart(value = "status")
            String status,
            @RequestPart(value = "price")
            String price,
            @RequestPart(value = "postTitle")
            String postTitle,
            @RequestPart(value = "postContent")
            String postContent,
            @RequestPart(value = "files")
            List<MultipartFile> files
    ) throws Exception {
        Post post = Post.builder()
                .userNum(Integer.parseInt(userNum))
                .model(modelService.findByModelName(modelName))
                .grade(grade)
                .status(status)
                .price(Integer.parseInt(price))
                .postTitle(postTitle)
                .postContent(postContent)
                .writtenDate(LocalDateTime.now())
                .build();

        postService.createPost(post, files);
        System.out.println(post.getUserNum() + "번 사용자 -> " + post.getModel().getModelName() + " 판매 게시글 작성");
    }

    // 전체 공개 게시글 목록 리턴
    @GetMapping("/post") // GET 방식: 정보를 단순히 조회하기 위해 사용하는 방식
    public List<PostResponse> getAllPost() throws IOException {
        System.out.println("공개 게시글 목록 반환");
        return postService.getAllPost();
    }

    // 해당 게시글 리턴
    @GetMapping("/post/{user_num}/{post_num}")
    public PostResponse getPostByNum(@PathVariable Integer user_num, @PathVariable Integer post_num) throws IOException {
        listService.visit(user_num, post_num);

        System.out.println(user_num + "번 사용자 " + post_num + "번 게시글 방문");
        return postService.getPostByNum(post_num);
    }

    // 해당 카테고리 내 게시글 목록 리턴
    @GetMapping("post/category/{category_name}")
    public List<PostResponse> getPostByCategory(@PathVariable String category_name) {
        System.out.println(category_name + " 게시글 목록 반환");
        return postService.getPostByCategory(category_name);
    }

    // 해당 모델 내 게시글 목록 리턴
    @GetMapping("post/model/{model_name}")
    public List<PostResponse> getPostByModel(@PathVariable String model_name) {
        System.out.println(model_name + " 게시글 목록 반환");
        return postService.getPostByModel(model_name);
    }

    // 오늘 작성된 게시글 목록 리턴
    @GetMapping("/post/today")
    public List<PostResponse> getTodayPost() {
        System.out.println("오늘 작성된 게시글 목록 반환");
        return postService.getTodayPost();
    }

    // 해당 게시글 수정
    @PostMapping("/post/{post_num}/modify")
    public void updatePost(@PathVariable Integer post_num, @RequestBody Post post) throws Exception {
        System.out.println(post.getUserNum() + "번 사용자 " + post_num + "번 게시글 수정");
        postService.updatePost(post_num, post);
    }

    // 해당 게시글 삭제
    @GetMapping("/post/{post_num}/delete")
    public void deletePost(@PathVariable Integer post_num) {
        System.out.println(post_num + "번 게시글 삭제");
        postService.deletePost(post_num);
    }

    // 게시글 제목으로 검색
    @GetMapping("post/name/{type}/{name}")
    public List<PostResponse> getPostByName(@PathVariable String type, @PathVariable String name) throws IOException {
        System.out.println(name + " 포함한 게시글 목록 반환");
        return postService.getPostByName(type, name);
    }

    // 해당 게시글 위치 리턴
    @GetMapping("post/location/{post_num}")
    public Location getPostLocation(@PathVariable Integer post_num) {
        System.out.println(post_num + "번 게시글 판매 위치 반환");
        return postService.getPostLocation(post_num);
    }

    // 해당 위치에서 해당 반경 내 게시글 목록 리턴
    @GetMapping("post/lonlat/{lon}/{lat}/{distance}")
    public List<PostResponse> getAroundPost(@PathVariable double lon, @PathVariable double lat, @PathVariable double distance) {
        System.out.println("(" + lon + ", " + lat + ")" + "에서 " + distance + "km 반경 내 게시글 목록 반환");
        return postService.getAroundPost(lon, lat, distance);
    }

    // 해당 게시글 판매완료 처리
    @GetMapping("/post/{post_num}/soldout")
    public void soldOut(@PathVariable Integer post_num) {
        System.out.println(post_num + "번 게시글 거래 완료");
        postService.soldOut(post_num);
    }

    // 해당 사용자의 판매완료 게시글 목록 리턴
    @GetMapping("/post/sell/{user_num}")
    public List<PostResponse> getSoldOutPost(@PathVariable Integer user_num) {
        System.out.println(user_num + "번 고객 판매 목록 반환");
        return postService.getSoldOutPost(user_num);
    }

    // 해당 승인 대기 게시글 승인
    @GetMapping("/approval/{post_num}/{model_name}")
    public void approvePost(@PathVariable Integer post_num, @PathVariable String model_name) {
        System.out.println(post_num + "번 게시글 승인 완료");
        postService.approvePost(post_num, model_name);
    }

    // 해당 승인 대기 게시글 거절
    @GetMapping("/approval/{post_num}/reject")
    public void rejectPost(@PathVariable Integer post_num) {
        System.out.println(post_num + "번 게시글 승인 거절");
        postService.rejectPost(post_num);
    }

    // 전체 승인 대기글 목록 리턴
    @GetMapping("/approval")
    public List<PostResponse> getAllWaitingApprovalPost() throws IOException {
        List<PostResponse> postList = new ArrayList<>();

        for (Post post : postService.getAllWaitingApprovalPost()){
            postList.add(postService.convertPostToPostResponse(post));
        }

        System.out.println("승인 대기 게시글 목록 반환");
        return postList;
    }

    // 해당 승인 대기 게시글 리턴
    @GetMapping("/approval/{post_num}")
    public PostResponse getWaitingApprovalPost(@PathVariable Integer post_num) throws IOException {
        System.out.println("승인 대기 게시글 방문");
        return postService.getPostByNum(post_num);
    }

    // 해당 게시글 숨김 처리
    @GetMapping("/post/{post_num}/hide")
    public void hidePost(@PathVariable Integer post_num) {
        System.out.println(post_num + "번 게시글 숨김 처리");
        postService.hidePost(post_num);
    }

    // 해당 숨김 처리된 게시글 공개 처리
    @GetMapping("/post/{post_num}/exposure")
    public void exposureHiddenPost(@PathVariable Integer post_num) {
        System.out.println(post_num + "번 게시글 공개 처리");
        postService.exposureHiddenPost(post_num);
    }

    // 전체 숨김 게시글 목록 리턴
    @GetMapping("/post/hidden")
    public List<PostResponse> getHiddenPost() throws IOException {
        System.out.println("숨김 게시글 목록 반환");
        return postService.getHiddenPost();
    }
}