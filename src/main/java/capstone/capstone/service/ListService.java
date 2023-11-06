package capstone.capstone.service;

import capstone.capstone.domain.LikeList;
import capstone.capstone.extendedDomain.PostWithPicture;
import capstone.capstone.domain.VisitList;
import capstone.capstone.repository.LikeListRepository;
import capstone.capstone.repository.VisitListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ListService {
    @Autowired
    private PostService postService;

    @Autowired
    private LikeListRepository likelistRepository;

    @Autowired
    private VisitListRepository visitListRepository;

    public void addLikeList(int user_num, int post_num) {
        LikeList like = new LikeList(user_num, post_num);

        likelistRepository.save(like);
    }

    public void deleteLikeList(int user_num, int post_num) {
        LikeList like = new LikeList(user_num, post_num);

        likelistRepository.delete(like);
    }

    public List<PostWithPicture> getLikeList(int user_num) throws IOException {
        List<Integer> postlist = likelistRepository.getLikeList(user_num);
        List<PostWithPicture> postWithPictureslist = new ArrayList<PostWithPicture>();

        for (int n : postlist) {
            PostWithPicture postWithPicture = postService.getPostByNum(n);
            if (postWithPicture != null) {
                postWithPictureslist.add(postWithPicture);
            }
        }

        return postWithPictureslist;
    }

    public List<Integer> getLikeListPostNum(int user_num) throws IOException {
        return likelistRepository.getLikeList(user_num);
    }

    public void visit(int user_num, int post_num) {
        VisitList visit = new VisitList(user_num, post_num);

        visitListRepository.save(visit);
    }

    public void deleteVisitList(int user_num, int post_num) {
        VisitList visit = new VisitList(user_num, post_num);

        visitListRepository.delete(visit);
    }

    public List<PostWithPicture> getVisitList(int user_num) throws IOException {
        List<Integer> postlist = visitListRepository.getVisitList(user_num);
        List<PostWithPicture> postWithPictureslist = new ArrayList<>();

        for (int n : postlist) {
            PostWithPicture postWithPicture = postService.getPostByNum(n);
            if (postWithPicture != null) {
                postWithPictureslist.add(postWithPicture);
            }
        }

        return postWithPictureslist;
    }
}