package capstone.capstone.controller;

import capstone.capstone.domain.ChattingMessage;
import capstone.capstone.domain.ChattingRoom;
import capstone.capstone.domain.Post;
import capstone.capstone.dto.ChattingMessageResponse;
import capstone.capstone.repository.PostRepository;
import capstone.capstone.service.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ChattingController {
    @Autowired
    private ChattingService chattingService;

    @Autowired
    private ChattingRoomService chattingRoomService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    // 게시글에서 채팅방 입장(생성)
    @GetMapping("/chattingroom/post/{post_num}/{guest_num}")
    public ChattingList enterChattingRoom(@PathVariable Integer post_num, @PathVariable Integer guest_num) {
        Integer cht_room_num = chattingRoomService.enterChattingRoom(post_num, guest_num);
        ChattingList chattingList = new ChattingList(chattingService.enterChattingRoom(cht_room_num));
        chattingList.setting(cht_room_num);

        System.out.println(guest_num + "사용자가 " + post_num + "번 게시글 " + cht_room_num + "번 채팅방 입장");
        return chattingList;
    }

    // 채팅방 목록에서 채팅방 입장
    @GetMapping("/chatting/{cht_room_num}")
    public ChattingList enterChattingRoom(@PathVariable Integer cht_room_num) {
        ChattingList chattingList = new ChattingList(chattingService.enterChattingRoom(cht_room_num));
        chattingList.setting(cht_room_num);

        System.out.println(cht_room_num + "번 채팅방 입장");
        return chattingList;
    }

    // 채팅 전송
    @PostMapping("/chatting")
    public ChattingMessage sendMessage(@RequestBody ChattingMessage chatting) {
        System.out.println(chatting.getRoomNum() + "번 채팅방 -> " + chatting.getSenderNum() + "번 사용자: " + chatting.getContent());
        return chattingService.sendMessage(chatting);
    }

    // 전체 채팅방 목록 리턴(사용자)
    @GetMapping("/chattingroom/guest/{guest_num}")
    public List<ChattingRoomList> getAllChattingRoom(@PathVariable Integer guest_num) {
        List<ChattingRoomList> chattingRoomList = new ArrayList<>();

        for (ChattingRoom chattingRoom : chattingRoomService.getAllChattingRoom(guest_num)) {
            chattingRoomList.add(new ChattingRoomList(chattingRoom));
        }

        return chattingRoomList;
    }

    // 전체 채팅방 목록 리턴(관리자)
    @GetMapping("/chattingroom")
    public List<ChattingRoomList> getAllChattingRoom() {
        List<ChattingRoomList> chattingRoomList = new ArrayList<>();

        for (ChattingRoom chattingRoom : chattingRoomService.getAllChattingRoom()) {
            chattingRoomList.add(new ChattingRoomList(chattingRoom));
        }

        System.out.println("전체 활성 채팅방 목록 반환");
        return chattingRoomList;
    }

    @Getter
    class ChattingRoomList {
        Integer cht_room_num;
        Integer post_num;
        String host_info;
        String post_name;
        String last_cht_msg;
        String pictureURL;
        LocalDateTime last_cht_time;

        public ChattingRoomList(ChattingRoom chattingRoom) {
            this.cht_room_num = chattingRoom.getRoomNum();
            this.post_num = chattingRoom.getPostNum();

            Optional<Post> optionalPost = postRepository.findById(post_num);

            if (optionalPost.isPresent()) {
                this.post_name = optionalPost.get().getPostTitle();
                this.host_info = optionalPost.get().getMember().getNickname();
            } else
                throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다.");

            this.last_cht_msg = chattingService.getLastMsg(cht_room_num);
            this.last_cht_time = chattingService.getLastTime(cht_room_num);
            this.pictureURL = chattingRoomService.getChattingPostPicture(cht_room_num);
        }
    }

    @Getter
    class ChattingList {
        Integer cht_room_num;
        List<ChattingMessageResponse> chattingList;
        String post_title;
        Integer host_num;
        Integer guest_num;
        String pictureURL;

        public ChattingList(List<ChattingMessage> chattingList) {
            this.chattingList = new ArrayList<>();

            for(ChattingMessage chattingMessage : chattingList) {
                String senderProfileImage = memberService.getProfileImage(chattingMessage.getSenderNum());
                String senderNickname = memberService.findById(chattingMessage.getSenderNum()).getNickname();
                ChattingMessageResponse chattingMessageResponse = new ChattingMessageResponse(chattingMessage, senderProfileImage, senderNickname);

                this.chattingList.add(chattingMessageResponse);
            }
        }

        public void setting(Integer cht_room_num){
            this.post_title = chattingRoomService.getChattingPostTitle(cht_room_num);
            this.host_num = chattingRoomService.getHostInfo(cht_room_num);
            this.guest_num = chattingRoomService.getGuestInfo(cht_room_num);
            this.cht_room_num = cht_room_num;
            this.pictureURL = chattingRoomService.getChattingPostPicture(cht_room_num);
        }

        public void setCht_room_num(int cht_room_num) {this.cht_room_num = cht_room_num; }

        public void setPost_title(String post_title) {
            this.post_title = post_title;
        }

        public void setPictureURL(String pictureURL) { this.pictureURL = pictureURL; }
    }
}