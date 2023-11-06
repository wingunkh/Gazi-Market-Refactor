package capstone.capstone.service;

import capstone.capstone.domain.ChattingMessage;
import capstone.capstone.repository.ChattingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChattingService {
    @Autowired
    private ChattingRepository chattingRepository;

    public ChattingMessage sendMessage(ChattingMessage chatting) {
        return chattingRepository.save(chatting);
    }

    public List<ChattingMessage> enterChattingRoom(int cht_room_num) {
        return chattingRepository.enterChattingRoom(cht_room_num);
    }

    public String getLastMsg(int cht_room_num){
        return  chattingRepository.getLastMsg(cht_room_num);
    }

    public LocalDateTime getLastTime(int cht_room_num){
        return  chattingRepository.getLastTime(cht_room_num);
    }
}
