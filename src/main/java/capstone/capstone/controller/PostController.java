package capstone.capstone.controller;

import capstone.capstone.domain.Location;
import capstone.capstone.extendedDomain.PostWithPicture;
import capstone.capstone.domain.Post;
import capstone.capstone.service.ListService;
import capstone.capstone.service.PostService;;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//@CrossOrigin(origins = "http://localhost:3000") // CORS 문제를 해결하기 위한 어노테이션
//@CrossOrigin(origins = "http://52.78.130.186:80")
@CrossOrigin(origins = "http://52.78.130.186")
@RestController // @Controller에 @ResponseBody가 결합한 어노테이션, 문자열과 JSON 등을 전송 가능
@RequestMapping("/api") // 요청에 대해 어떤 Controller, 어떤 메소드가 처리할지를 맵핑하기 위한 어노테이션
public class PostController {
    @Autowired // 객체 생성 시점에 스프링 컨테이너에서 해당 스프링 빈을 찾아서 주입
    private PostService postService;

    @Autowired
    private ListService listService;

    //글 목록의 데이터를 리턴
    @GetMapping("/post") // GET 방식: 정보를 단순히 조회하기 위해 사용하는 방식
    public List<PostWithPicture> getAllPosts() throws IOException {
        return postService.getAllPosts();
    }

    //숨김 처리된 글 목록의 데이터를 리턴
    @GetMapping("/post/hidden")
    public List<PostWithPicture> getHiddenPosts() throws IOException {
        return postService.getHiddenPosts();
    }

    //승인 대기글 목록의 데이터를 리턴
    @GetMapping("/approval")
    public List<PostWithPicture> getAllWaitingApprovalPosts() throws IOException {
        List<PostWithPicture> postList = new ArrayList<>();
        for (Post post : postService.getAllWaitingApprovalPosts()){
            postList.add(postService.PostToPostWithPicture(post));
        }
        return postList;
    }

    //글을 저장 (리액트)
    @PostMapping("/post") // POST 방식: 특정 데이터를 서버로 제출하여 해당 데이터를 추가, 수정 또는 삭제하기 위해 데이터를 전송하는 방식
    public void createPost(
            @RequestPart(value = "post", required = false) // multipart/form-data에 특화되어 여러 복잡한 값을 처리할 때 사용할 수 있는 어노테이션이다.
            Post post,
            @RequestPart(value = "files") // 쿼리 파라미터, 폼 데이터, Multipart 등 많은 요청 파라미터를 처리할 수 있는 어노테이션이다.
            List<MultipartFile> files
    ) throws Exception {
        //더미 넣기 위해서 written_date를 랜덤하게, 월, 일, 시, 분, 초 모두 변경하게함
//        post.setWritten_date(LocalDateTime.now().withMonth((int) (Math.random() * 12) + 1).withDayOfMonth((int) (Math.random() * 31) + 1).withHour((int) (Math.random() * 24)).withMinute((int) (Math.random() * 60)).withSecond((int) (Math.random() * 60)));
        post.setWritten_date(LocalDateTime.now().plusHours(9));
        postService.createPost(post, files);
    }

    //글을 저장 (리액트 네이티브)
    @PostMapping("/post/native")
    public void createPost(
            @RequestPart(value = "model_name")
            String model_name,
            @RequestPart(value = "user_no")
            String user_no,
            @RequestPart(value = "grade")
            String grade,
            @RequestPart(value = "status")
            String status,
            @RequestPart(value = "price")
            String price,
            @RequestPart(value = "post_title")
            String post_title,
            @RequestPart(value = "post_content")
            String post_content,
            @RequestPart(value = "files")
            List<MultipartFile> files
    ) throws Exception {
        Post post = new Post();
        post.setModel_name(model_name);
        post.setUser_num(Integer.parseInt(user_no));
        post.setGrade(grade);
        post.setStatus(status);
        post.setPrice(Integer.parseInt(price));
        post.setPost_title(post_title);
        post.setPost_content(post_content);
        post.setWritten_date(LocalDateTime.now().plusHours(9));

        System.out.println(post);
        System.out.println("--------------------");
        System.out.println(files);
        postService.createPost(post, files);
    }

    //특정 게시글과 게시글의 사진 리턴
    @GetMapping("/post/{user_num}/{no}")
    public PostWithPicture getPostByNo(@PathVariable Integer user_num, @PathVariable Integer no) throws IOException {
        listService.addvisit(user_num, no);
        return postService.getPost(no);
    }

    //특정 게시글 수정
    @PostMapping("/post/{post_num}/modify")
    public void updatePost(@PathVariable Integer post_num, @RequestBody Post post) throws Exception {
        System.out.println(post_num);
        System.out.println(post.getPost_title());
        post.setWritten_date(LocalDateTime.now().plusHours(9));
        System.out.println(post.getWritten_date());
        postService.updatePost(post_num, post);
    }

    //특정 게시글 삭제
    @GetMapping("/post/{num}/delete")
    public void deletePost(@PathVariable Integer num){
        postService.deletePost(num);
    }

    //특정 승인 대기 게시글과 게시글의 사진 리턴
    @GetMapping("/approval/{no}")
    public PostWithPicture getWaitingApprovalPostByNo(@PathVariable Integer no) throws IOException {
        return postService.getPost(no);
    }

    //승인 대기 게시글 승인
    @GetMapping("/approval/{no}/{model_name}")
    public void approvePost(@PathVariable Integer no, @PathVariable String model_name) {
        postService.approvePost(no, model_name);
    }

    //승인 대기 게시글 거절
    @GetMapping("/approval/{no}/reject")
    public void rejectPost(@PathVariable Integer no) {
        postService.rejectPost(no);
    }

    //해당 카테고리 내의 게시글을 리턴
    @GetMapping("post/category/{category}")
    public List<PostWithPicture> getPostByCategory(@PathVariable String category){
        List<PostWithPicture> postList = new ArrayList<>();
        for (Post post : postService.getCategoryPosts(category)){
            postList.add(postService.PostToPostWithPicture(post));
        }
        return postList;
    }

    //해당 모델의 게시글을 리턴
    @GetMapping("post/model/{model}")
    public List<PostWithPicture> getPostByModel(@PathVariable String model) {
        List<PostWithPicture> postList = new ArrayList<>();
        for (Post post : postService.getModelPosts(model)){
            postList.add(postService.PostToPostWithPicture(post));
        }
        return postList;
    }

    //게시글 제목으로 검색(name이 포함되는 제목 검색해서 목록 반환)
    @GetMapping("post/name/{type}/{name}")  //type에는 무조건 asc OR desc로, asc:오름차순, desc:내림차순
    public List<PostWithPicture> getPostByName(@PathVariable String type, @PathVariable String name) throws IOException {
        List<PostWithPicture> postList = new ArrayList<>();
        for (Post post : postService.getNamePosts(type, name)){
            postList.add(postService.PostToPostWithPicture(post));
        }
        return postList;
    }
    //게시들 위치 반환
    @GetMapping("post/location/{post_num}")
    public Location getPostLocation(@PathVariable int post_num){
        return postService.getLocation(post_num);
    }

    @GetMapping("post/lonlat/{lon}/{lat}/{distance}")
    public List<PostWithPicture> getAroundLocation(@PathVariable double lon, @PathVariable double lat, @PathVariable double distance){
        return postService.getAroundLocation(lon, lat, distance);
    }

    //판매완료 처리
    @GetMapping("/post/{post_num}/soldout")
    public void setStatusSoldout(@PathVariable int post_num){
        postService.setStatusSoldout(post_num);
    }
}

